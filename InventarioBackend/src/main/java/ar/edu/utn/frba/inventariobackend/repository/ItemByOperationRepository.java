package ar.edu.utn.frba.inventariobackend.repository;

import ar.edu.utn.frba.inventariobackend.model.ItemByOperation;
import ar.edu.utn.frba.inventariobackend.model.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on
 * {@link ItemByOperation} entities.
 * <p>
 * Extends {@link JpaRepository} to inherit methods for saving, deleting, and
 * finding {@link ItemByOperation} entities by their ID or other criteria.
 * </p>
 */
@Repository
public interface ItemByOperationRepository extends JpaRepository<ItemByOperation, Long> {
	/**
	 * Finds all {@link ItemByOperation} entities that match the given operation ID
	 * and item type.
	 * <p>
	 * This method relies on Spring Data JPA's query derivation. It expects the
	 * {@link ItemByOperation} entity to have fields named {@code idOperation}
	 * (matching the type of the first parameter) and {@code itemType} (matching the
	 * type of the second parameter).
	 * </p>
	 *
	 * @param idOperation
	 *            The ID of the operation (e.g., Order ID or Shipment ID) to filter
	 *            by.
	 * @param itemType
	 *            The type of the item (e.g., {@link ItemType#ORDER} or
	 *            {@link ItemType#SHIPMENT}) to filter by.
	 * @return A list of {@link ItemByOperation} entities matching both criteria.
	 *         Empty list if no matches are found.
	 */
	List<ItemByOperation> findAllByIdOperationAndItemType(Long idOperation, ItemType itemType);
}
