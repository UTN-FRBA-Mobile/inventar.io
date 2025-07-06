package ar.edu.utn.frba.inventariobackend.dto.request;

/**
 * Represents the response containing both access and refresh tokens issued upon
 * successful authentication.
 *
 * @param accessToken
 *            the short-lived access token used for authenticated requests.
 * @param refreshToken
 *            the long-lived refresh token used to obtain new access tokens.
 */
public record TokenResponse(String accessToken, String refreshToken) {
}