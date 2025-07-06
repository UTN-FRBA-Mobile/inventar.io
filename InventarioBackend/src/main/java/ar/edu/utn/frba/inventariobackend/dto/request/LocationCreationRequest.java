package ar.edu.utn.frba.inventariobackend.dto.request;

import ar.edu.utn.frba.inventariobackend.model.Location;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents a create request to add a new location to the database.
 *
 * @param latitude
 *            the latitude coordinate (required).
 * @param longitude
 *            the longitude coordinate (required).
 * @param radius
 *            the radius around the location, e.g., for area of interest
 *            (required).
 * @param name
 *            the name of the location.
 */
public record LocationCreationRequest(@DecimalMin("-90") @DecimalMax("90") @NotNull double latitude,

		@Column(name = "longitude", nullable = false) @DecimalMin("-90") @DecimalMax("90") @NotNull double longitude,

		@Column(name = "radius", nullable = false) @Positive @NotNull double radius,

		@Column(name = "name", nullable = false) @NotNull String name) {

	/**
	 * Creates a {@code Location} entity from a {@link LocationCreationRequest}.
	 *
	 * @param request
	 *            the request object containing location data
	 * @return a new {@code Location} instance
	 */
	public static Location toLocation(LocationCreationRequest request) {
		return new Location(request.latitude(), request.longitude(), request.radius(), request.name());
	}
}
