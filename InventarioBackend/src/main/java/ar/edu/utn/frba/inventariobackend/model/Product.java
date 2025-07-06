package ar.edu.utn.frba.inventariobackend.model;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * Represents a product in our system.
 */
@Getter
@Entity
@Table(name = "product")
public class Product {

	/**
	 * Auto-generated unique identifier for the product.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
	@SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
	private Long id;

	/**
	 * The name of the product.
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * A detailed description of the product.
	 */
	private String description;

	/**
	 * The EAN-13 barcode of the product.
	 */
	@Column(unique = true)
	private String ean13;

	/**
	 * Base64 encoded string representing the product's image.
	 */
	private String imageURL;

	/**
	 * Default constructor required by JPA.
	 */
	public Product() {
	}

	/**
	 * Constructs a new {@code Product}.
	 *
	 * @param name
	 *            the name of the product
	 * @param description
	 *            a detailed description of the product
	 * @param ean13
	 *            the EAN-13 barcode of the product
	 * @param imageURL
	 *            link to the product's image
	 */
	public Product(String name, String description, String ean13, String imageURL) {
		this.name = name;
		this.description = description;
		this.ean13 = ean13;
		this.imageURL = imageURL;
	}
}