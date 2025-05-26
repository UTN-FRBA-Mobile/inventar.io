package ar.edu.utn.frba.inventariobackend.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility component for generating and validating JSON Web Tokens (JWT) using HMAC algorithms.
 * Supports access and refresh tokens, and embeds a custom claim for locationId.
 */
@Component
public class JwtUtil {
    /** Validity duration for access tokens (15 minutes). */
    //private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15;
    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 30;

    /** Validity duration for refresh tokens (7 days). */
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7;

    /** MAC algorithm used for signing tokens (HS512). */
    private static final MacAlgorithm MAC_ALGORITHM = Jwts.SIG.HS512;

    /** Secret key used to sign and verify JWTs. */
    private final SecretKey key = MAC_ALGORITHM.key().build();

    /**
     * Generates a signed JWT with embedded claims including the username, locationId,
     * and a flag indicating whether it's an access or refresh token.
     *
     * @param username   the username to set as the subject of the token.
     * @param locationId the location ID to embed as a custom claim.
     * @param isRefresh  whether this is a refresh token (true) or access token (false).
     * @return the signed and compacted JWT string.
     */
    public String generateToken(String username, Long locationId, boolean isRefresh) {
        long validity = isRefresh ? REFRESH_TOKEN_VALIDITY : ACCESS_TOKEN_VALIDITY;
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);
        return Jwts.builder()
                .subject(username)
                .claim("locationId", locationId)
                .claim("type", isRefresh ? "refresh" : "access")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key, MAC_ALGORITHM)
                .compact();
    }

    /**
     * Extracts the claims (payload) from a valid signed JWT.
     *
     * @param token the JWT to parse.
     * @return the {@link Claims} contained in the token.
     * @throws JwtException if the token is invalid or cannot be verified.
     */
    public Claims extractClaims(String token) {
        JwtParser parser = Jwts.parser().verifyWith(key).build();

        return parser.parseSignedClaims(token).getPayload();
    }

    /**
     * Validates whether a token is correctly signed and matches the expected type
     * (either "access" or "refresh").
     *
     * @param token        the JWT to validate.
     * @param expectedType the expected token type ("access" or "refresh").
     * @return {@code true} if valid and of correct type, {@code false} otherwise.
     */
    public boolean isTokenValid(String token, String expectedType) {
        try {
            Claims claims = extractClaims(token);
            return expectedType.equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Creates a new access token based on a refresh token.
     * @param refreshToken the token in which we will be based to create the new access token.
     * @return a signed JWT string.
     */
    public String getNewAccessTokenFromRefreshToken(String refreshToken) {
        Claims claims = extractClaims(refreshToken);
        String username = claims.getSubject();
        Long locationId = claims.get("locationId", Long.class);

        return generateToken(username, locationId, false);
    }
}
