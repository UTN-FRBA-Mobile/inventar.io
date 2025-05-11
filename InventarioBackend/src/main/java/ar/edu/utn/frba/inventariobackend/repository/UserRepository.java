package ar.edu.utn.frba.inventariobackend.repository;

import ar.edu.utn.frba.inventariobackend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link User} entities.
 * <p>
 * Extends {@link JpaRepository} to inherit methods for saving, deleting,
 * and finding {@link User} entities by their ID or other criteria.
 * </p>
 */
@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by username.
     *
     * @param username the username to search
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByUsername(String username);
}
