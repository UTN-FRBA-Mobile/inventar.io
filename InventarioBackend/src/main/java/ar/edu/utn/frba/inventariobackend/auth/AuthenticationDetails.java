package ar.edu.utn.frba.inventariobackend.auth;

/**
 * Stores additional authentication-related information associated with a user.
 * <p>
 * This record is typically used to attach metadata—such as the user's
 * {@code locationId}— to the security context during authentication.
 *
 * @param locationId
 *            the identifier of the location associated with the authenticated
 *            user.
 */
public record AuthenticationDetails(Long locationId) {
}