package ar.edu.utn.frba.inventariobackend.repository;

import ar.edu.utn.frba.inventariobackend.model.StockByLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link StockByLocation} entities.
 * <p>
 * Extends {@link JpaRepository} to inherit methods for saving, deleting,
 * and finding {@link StockByLocation} entities by their ID or other criteria.
 * </p>
 */
@Repository
public interface StockByLocationRepository extends JpaRepository<StockByLocation, Long> {
}