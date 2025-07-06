package ar.edu.utn.frba.inventariobackend.dto.request;

import ar.edu.utn.frba.inventariobackend.model.User;

import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * DTO for receiving user creation requests. This class is used to carry data
 * required to create a new {@link User}.
 *
 * @param username
 *            the username for authentication
 * @param password
 *            the raw password that will be processed (hashed + salted)
 * @param name
 *            the full name of the user
 * @param imageURL
 *            a base64-encoded representation of the user's image
 * @param allowedLocations
 *            a list of ids of the locations where the user can work from
 */
public record AddUserRequest(@NotNull String username, @NotNull String password, @NotNull String name, String imageURL,
		@NotNull List<Long> allowedLocations) {
	/**
	 * Converts this request to a {@link User} entity.
	 *
	 * @param addUserRequest
	 *            the user creation request
	 * @return a new {@link User} entity initialized with the request data
	 */
	public static User toUser(AddUserRequest addUserRequest, PasswordEncoder passwordEncoder) {
		return new User(addUserRequest.username(), passwordEncoder.encode(addUserRequest.password()),
				addUserRequest.name(), addUserRequest.imageURL(), addUserRequest.allowedLocations());
	}
}
