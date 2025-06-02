package ar.edu.utn.frba.inventariobackend.dto.response;

import ar.edu.utn.frba.inventariobackend.model.Product;

/**
 * Represents the data of a product being sent to the client.
 *
 * @param id           The unique identifier of the product.
 * @param name         The name of the product.
 * @param description  A detailed description of the product.
 * @param ean13        The EAN-13 barcode of the product.
 * @param base64image  A Base64 encoded string representing the product's image.
 */
public record ProductResponse(
    Long id,
    String name,
    String description,
    String ean13,
    String base64image
) {
    /**
     * Creates a {@link ProductResponse} from a {@link Product} domain object.
     *
     * @param product the {@link Product} entity to convert.
     * @return a new {@link ProductResponse} instance with data copied from the entity.
     */
    public static ProductResponse fromProduct(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getEan13(),
            product.getBase64image());
    }
}