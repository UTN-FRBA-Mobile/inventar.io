package ar.edu.utn.frba.inventariobackend.repository;

import ar.edu.utn.frba.inventariobackend.model.Product;
import ar.edu.utn.frba.inventariobackend.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on {@link Product}
 * entities.
 * <p>
 * Extends {@link JpaRepository} to inherit methods for saving, deleting, and
 * finding {@link Product} entities by their ID or other criteria.
 * </p>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	/**
	 * Finds products by EAN13.
	 *
	 * @param ean13s
	 *            the EAN13 of the products to search.
	 * @return a {@link List} containing the products.
	 */
	List<Product> findAllByEan13In(List<String> ean13s);
}