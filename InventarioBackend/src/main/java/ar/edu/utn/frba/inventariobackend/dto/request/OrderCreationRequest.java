package ar.edu.utn.frba.inventariobackend.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents the request payload for creating a new order.
 *
 * @param scheduledDate
 *            The date and time when the order is scheduled for processing
 *            (optional).
 * @param idLocation
 *            The id of the location related to this order.
 * @param productAmount
 *            A list of items to be included in the order (required, non-empty).
 * @param sender
 *            Identifier for who or what originated this order (required,
 *            non-blank).
 */
public record OrderCreationRequest(
		@FutureOrPresent(message = "Scheduled date must be in the present or future.") LocalDateTime scheduledDate,

		@NotNull(message = "Location ID cannot be null.") Long idLocation,

		@NotEmpty(message = "Order must contain at least one item.") Map<Long, Integer> productAmount,

		@NotBlank(message = "Sender cannot be blank.") @Size(max = 255, message = "Sender must be less than 255 characters.") String sender) {
}