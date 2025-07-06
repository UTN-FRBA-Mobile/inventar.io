package ar.edu.utn.frba.inventariobackend.repository;

import ar.edu.utn.frba.inventariobackend.model.StockByLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on
 * {@link StockByLocation} entities.
 * <p>
 * Extends {@link JpaRepository} to inherit methods for saving, deleting, and
 * finding {@link StockByLocation} entities by their ID or other criteria.
 * </p>
 */
@Repository
public interface StockByLocationRepository extends JpaRepository<StockByLocation, Long> {
	/**
	 * Finds StockByLocation entities where idProduct is in the given list and
	 * idLocation matches the single provided location ID.
	 * 
	 * @param idProducts
	 *            A list of product IDs.
	 * @param idLocation
	 *            The single location ID.
	 * @return A list of ProductLocation entities matching the criteria.
	 */
	List<StockByLocation> findByIdProductInAndIdLocation(List<Long> idProducts, Long idLocation);
}