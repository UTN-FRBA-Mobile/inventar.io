package ar.edu.utn.frba.inventariobackend.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter that intercepts HTTP requests to validate and parse JWT tokens.
 * <p>
 * This filter checks for the "Authorization" header in the incoming request,
 * validates the JWT token, extracts authentication details, and populates the
 * {@link SecurityContextHolder} with the user's authentication if valid.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/auth/login",
        "/auth/refresh",
        "/api/v1/user",
        "/api/v1/location"
    );

    private static final List<String> PUBLIC_PREFIXES = List.of(
        "/v3/api-docs",
        "/swagger-ui",
        "/swagger-ui.html"
    );

    private final JwtUtil jwtUtil;

    /**
     * Filters incoming HTTP requests to extract and validate JWT access tokens.
     *
     * @param request           the incoming HTTP request.
     * @param response          the outgoing HTTP response.
     * @param chain             the filter chain to continue processing the request.
     * @throws ServletException in case of a servlet error.
     * @throws IOException      in case of I/O error.
     */
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain
    ) throws ServletException, IOException {
        if (PUBLIC_ENDPOINTS.stream().noneMatch(path -> request.getServletPath().equals(path)) &&
            PUBLIC_PREFIXES.stream().noneMatch(path -> request.getServletPath().startsWith(path))
        ) {
            boolean isTokenValid = false;
            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                if (jwtUtil.isTokenValid(token, "access")) {
                    Claims claims = jwtUtil.extractClaims(token);
                    String username = claims.getSubject();
                    Long locationId = claims.get("locationId", Long.class);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(username, null, List.of());
                    auth.setDetails(new AuthenticationDetails(locationId));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    isTokenValid = true;
                }
            }

            if (!isTokenValid) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}