package ar.edu.utn.frba.inventariobackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom implementation of {@link AuthenticationEntryPoint} for handling authentication failures.
 * <p>
 * This class is triggered by Spring Security when an unauthenticated user attempts to access a protected resource.
 * Instead of the default behavior (which might be a redirect to a login page or a generic 403 error),
 * this entry point ensures that a clear {@code 401 Unauthorized} HTTP status is returned.
 * <p>
 * It crafts a consistent JSON error response, which is ideal for RESTful APIs consumed by clients
 * that expect structured error messages.
 *
 * @see org.springframework.security.web.AuthenticationEntryPoint
 */
@Component("customAuthenticationEntryPoint")
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme.
     * <p>
     * This method is invoked when an unauthenticated user requests a secured HTTP resource and an
     * {@code AuthenticationException} is thrown. It overrides the default behavior to send a
     * custom JSON error response with an HTTP status code of 401 (Unauthorized).
     *
     * @param request       The request that resulted in an {@link AuthenticationException}.
     * @param response      The response. Used to send the custom error message back to the client.
     * @param authException The exception that triggered the commencement. Its message is included in the response body.
     * @throws IOException if an input or output error is encountered when writing the response.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException
    ) throws IOException {
        if (authException instanceof InsufficientAuthenticationException) {
            // Set the response content type to JSON and the status to 401 Unauthorized.
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // Create a structured error message body.
            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            body.put("message", "Authentication failed: " + authException.getMessage());
            body.put("path", request.getServletPath());

            // Use ObjectMapper to write the map as a JSON string to the response's output stream.
            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
        }
    }
}