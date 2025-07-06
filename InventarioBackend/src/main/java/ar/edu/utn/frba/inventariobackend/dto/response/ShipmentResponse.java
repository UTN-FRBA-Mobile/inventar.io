package ar.edu.utn.frba.inventariobackend.dto.response;

import ar.edu.utn.frba.inventariobackend.model.Shipment;
import ar.edu.utn.frba.inventariobackend.model.Status;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents the data of a shipment being sent to the client.
 *
 * @param id
 *            The unique identifier of the shipment.
 * @param status
 *            The current state of the shipment.
 * @param creationDate
 *            The date and time when the shipment was created.
 * @param lastModifiedDate
 *            The date and time when the shipment was last modified.
 * @param idLocation
 *            The location of this shipment
 * @param customerName
 *            The customer for this shipment.
 * @param productAmount
 *            A map with id as key and amount as value.
 * @param productNames
 *            A map with id as key and names as value.
 */
public record ShipmentResponse(Long id, Status status, LocalDateTime creationDate, LocalDateTime lastModifiedDate,
		Long idLocation, String customerName, Map<Long, Integer> productAmount, Map<Long, String> productNames) {

	/**
	 * Creates an ShipmentResponse DTO from a Shipment entity.
	 *
	 * @param shipment
	 *            The Shipment entity.
	 * @param productAmount
	 *            The amount of products for the shipment.
	 * @param productNames
	 *            The names of the products.
	 * @return A ShipmentResponse DTO.
	 */
	public static ShipmentResponse fromShipment(Shipment shipment, Map<Long, Integer> productAmount,
			Map<Long, String> productNames) {
		return new ShipmentResponse(shipment.getId(), shipment.getStatus(), shipment.getCreationDate(),
				shipment.getLastModifiedDate(), shipment.getIdLocation(), shipment.getCustomerName(), productAmount,
				productNames);
	}
}
