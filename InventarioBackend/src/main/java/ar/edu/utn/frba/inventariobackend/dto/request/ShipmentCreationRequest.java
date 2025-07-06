package ar.edu.utn.frba.inventariobackend.dto.request;

import jakarta.validation.constraints.*;

import java.util.Map;

/**
 * Represents the request payload for creating a new order.
 *
 * @param idLocation
 *            The id of the location related to this order.
 * @param productAmount
 *            A list of items to be included in the order (required, non-empty).
 * @param customerName
 *            The name of the customer for this order (required, non-blank).
 */
public record ShipmentCreationRequest(@NotNull(message = "Location ID cannot be null.") Long idLocation,

		@NotEmpty(message = "Order must contain at least one item.") Map<Long, Integer> productAmount,

		@NotBlank(message = "Customer name cannot be blank.") @Size(max = 255, message = "Customer name must be less than 255 characters.") String customerName) {
}
