package ar.edu.utn.frba.inventariobackend.dto.request;

import ar.edu.utn.frba.inventariobackend.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents the request payload for creating a new product.
 *
 * @param name
 *            The name of the product (required, non-blank).
 * @param description
 *            A detailed description of the product (optional).
 * @param ean13
 *            The EAN-13 barcode of the product (optional, must be 13 digits if
 *            provided).
 * @param imageURL
 *            Image URL of the product (optional).
 */
public record ProductCreationRequest(
		@NotBlank(message = "Product name cannot be blank.") @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters.") String name,

		@Size(max = 4000, message = "Product description cannot exceed 4000 characters.") String description,

		@Pattern(regexp = "^[0-9]{13}$", message = "EAN-13 must be exactly 13 digits.") String ean13,

		String imageURL) {
	/**
	 * Converts this request to a {@link Product} entity.
	 *
	 * @param productCreationRequest
	 *            the product creation request
	 * @return a new {@link Product} entity initialized with the request data
	 */
	public static Product toProduct(ProductCreationRequest productCreationRequest) {
		return new Product(productCreationRequest.name(), productCreationRequest.description(),
				productCreationRequest.ean13(), productCreationRequest.imageURL());
	}
}