package ar.edu.utn.frba.inventariobackend.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

/**
 * Represents a user account stored in the database.
 * This entity includes authentication credentials and profile information.
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
     * The hashed password stored in the database.
     * Original password is formed by concatenating the plain password and the username,
     * then hashed and finally encoded using BCrypt.
     */
    private String password;

    /**
     * The full name of the user.
     */
    private String name;

    /**
     * An image of the user, encoded as a Base64 string.
     */
    @Lob
    private String base64image;

    /**
     * A list of the ids the user is allowed to work from.
     */
    @ElementCollection
    private List<Long> allowedLocations;

    /**
     * Default constructor required by JPA.
     */
    public User() {}

    /**
     * Constructs a new {@code User} with the provided credentials and profile information.
     *
     * @param username         the username for login
     * @param password         the hashed password
     * @param name             the full name of the user
     * @param base64image      a base64-encoded image of the user
     * @param allowedLocations the ids of the locations that are allowed for the user
     */
    public User(String username, String password, String name, String base64image, List<Long> allowedLocations) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.base64image = base64image;
        this.allowedLocations = allowedLocations;
    }
}
