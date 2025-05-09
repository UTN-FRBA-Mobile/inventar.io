package ar.edu.utn.frba.inventariobackend.service;

import ar.edu.utn.frba.inventariobackend.dto.request.AddUserRequest;
import ar.edu.utn.frba.inventariobackend.model.User;
import ar.edu.utn.frba.inventariobackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class that provides user-related operations such as retrieving and creating users.
 * Interacts with the {@link UserRepository} to manage {@link User} entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the found {@link User}, or empty if not found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Adds a new user to the system.
     *
     * @param addUserRequest the request containing user data
     * @return the newly created {@link User} after being persisted
     */
    public User addUser(AddUserRequest addUserRequest) {
        User user = AddUserRequest.toUser(addUserRequest, passwordEncoder);
        return userRepository.save(user);
    }
}
