package ar.edu.utn.frba.inventariobackend.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

/**
 * Represents a user account stored in the database. This entity includes
 * authentication credentials and profile information.
 */
@Getter
@Entity
@Table(name = "userAccount")
public class User {

	/**
	 * Auto-generated unique identifier for the user.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
	private Long id;

	/**
	 * The username used for authentication.
	 */
	private String username;

	/**
	 * The hashed password stored in the database. Original password is formed by
	 * concatenating the plain password and the username, then hashed and finally
	 * encoded using BCrypt.
	 */
	private String password;

	/**
	 * The full name of the user.
	 */
	private String name;

	/**
	 * An image of the user, encoded as a Base64 string.
	 */
	private String imageURL;

	/**
	 * A list of the ids the user is allowed to work from.
	 */
	@ElementCollection
	private List<Long> allowedLocations;

	/**
	 * Default constructor required by JPA.
	 */
	public User() {
	}

	/**
	 * Constructs a new {@code User} with the provided credentials and profile
	 * information.
	 *
	 * @param username
	 *            the username for login
	 * @param password
	 *            the hashed password
	 * @param name
	 *            the full name of the user
	 * @param imageURL
	 *            image url of the user
	 * @param allowedLocations
	 *            the ids of the locations that are allowed for the user
	 */
	public User(String username, String password, String name, String imageURL, List<Long> allowedLocations) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.imageURL = imageURL;
		this.allowedLocations = allowedLocations;
	}
}
