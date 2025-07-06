package ar.edu.utn.frba.inventariobackend.utils;

import ar.edu.utn.frba.inventariobackend.auth.AuthenticationDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Utility class for extracting user-related information from the Spring
 * Security context. Primarily used for accessing custom authentication details
 * such as username and location ID stored in JWT tokens.
 */
@Component
public class TokenUtils {

	/**
	 * Retrieves the location ID of the currently authenticated user from the
	 * security context.
	 *
	 * @return the location ID associated with the current authentication
	 * @throws ResponseStatusException
	 *             if authentication is not present or details are invalid (HTTP
	 *             403)
	 */
	public Long getLocationIdFromToken() {
		return ((AuthenticationDetails) getAuthentication().getDetails()).locationId();
	}

	/**
	 * Retrieves the username of the currently authenticated user from the security
	 * context.
	 *
	 * @return the username of the currently authenticated user
	 * @throws ResponseStatusException
	 *             if authentication is not present or details are invalid (HTTP
	 *             403)
	 */
	public String getUsernameFromToken() {
		return getAuthentication().getName();
	}

	/**
	 * Helper method to retrieve the current {@link Authentication} object from the
	 * security context.
	 *
	 * @return the current {@link Authentication} object
	 * @throws ResponseStatusException
	 *             if authentication is not found or invalid (HTTP 403)
	 */
	private Authentication getAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getDetails() instanceof AuthenticationDetails)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se puede obtener el usuario/location");
		}

		return authentication;
	}
}
