package ar.edu.utn.frba.inventariobackend.repository;

import ar.edu.utn.frba.inventariobackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on {@link Order} entities.
 * <p>
 * Extends {@link JpaRepository} to inherit methods for saving, deleting,
 * and finding {@link Order} entities by their ID or other criteria.
 * </p>
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Finds orders by location id.
     *
     * @param idLocation the id location to search
     * @return a {@link List} containing the orders from that location
     */
    List<Order> findAllByIdLocation(Long idLocation);
}
