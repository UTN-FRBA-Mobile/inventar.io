package ar.edu.utn.frba.inventariobackend.repository;

import ar.edu.utn.frba.inventariobackend.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on {@link Shipment} entities.
 * <p>
 * Extends {@link JpaRepository} to inherit methods for saving, deleting,
 * and finding {@link Shipment} entities by their ID or other criteria.
 * </p>
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    /**
     * Finds shipments by location id.
     *
     * @param idLocation the id location to search
     * @return a {@link List} containing the shipments from that location
     */
    List<Shipment> findAllByIdLocation(Long idLocation);
}