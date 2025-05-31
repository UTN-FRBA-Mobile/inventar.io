package ar.edu.utn.frba.inventariobackend.repository;

import ar.edu.utn.frba.inventariobackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link Product} entities.
 * <p>
 * Extends {@link JpaRepository} to inherit methods for saving, deleting,
 * and finding {@link Product} entities by their ID or other criteria.
 * </p>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}