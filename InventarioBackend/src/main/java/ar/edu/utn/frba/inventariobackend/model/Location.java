package ar.edu.utn.frba.inventariobackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

/**
 * Represents a geographic location stored in the database.
 */
@Getter
@Entity
@Table(name = "location")
public class Location {
	/**
	 * Auto-generated unique identifier for the location.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq")
	@SequenceGenerator(name = "location_seq", sequenceName = "location_seq", allocationSize = 1)
	long id;

	/**
	 * The latitude coordinate (required). Between -90 and 90.
	 */
	@Column(name = "latitude", nullable = false)
	@DecimalMin("-90")
	@DecimalMax("90")
	@NotNull
	double latitude;

	/**
	 * The longitude coordinate (required). Between -90 and 90.
	 */
	@Column(name = "longitude", nullable = false)
	@DecimalMin("-90")
	@DecimalMax("90")
	@NotNull
	double longitude;

	/**
	 * The radius around the location, e.g., for area of interest (required).
	 */
	@Column(name = "radius", nullable = false)
	@Positive
	@NotNull
	double radius;

	/**
	 * The name of the location (required).
	 */
	@Column(name = "name", nullable = false)
	@NotNull
	String name;

	/**
	 * Default no-arg constructor for JPA.
	 */
	public Location() {
	}

	/**
	 * Constructs a new {@code Location} instance with the provided values.
	 *
	 * @param latitude
	 *            the latitude coordinate, between -90 and 90
	 * @param longitude
	 *            the longitude coordinate, between -90 and 90
	 * @param radius
	 *            the radius around the location (must be positive)
	 * @param name
	 *            the name of the location
	 */
	public Location(double latitude, double longitude, double radius, String name) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.name = name;
	}
}
