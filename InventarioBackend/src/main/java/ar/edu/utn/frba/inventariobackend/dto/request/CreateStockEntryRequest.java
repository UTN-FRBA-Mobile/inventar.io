package ar.edu.utn.frba.inventariobackend.dto.request;

import ar.edu.utn.frba.inventariobackend.model.StockByLocation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents the request payload for creating or updating a product's stock entry
 * in a specific location (i.e., a StockByLocation record).
 *
 * @param productId     The unique identifier of the product (required).
 * @param locationId    The unique identifier of the location where the stock is being registered (required).
 * @param stock         The quantity of the product at this location (required, non-negative).
 * @param innerLocation A string describing a more specific placement within the main location (e.g., aisle, shelf).
 */
public record CreateStockEntryRequest(
        @NotNull(message = "Product ID cannot be null.")
        Long productId,

        @NotNull(message = "Location ID cannot be null.")
        Long locationId,

        @NotNull(message = "Stock quantity cannot be null.")
        @Min(value = 0, message = "Stock quantity cannot be negative.")
        Integer stock,

        @Size(max = 255, message = "Inner location description cannot exceed 255 characters.")
        String innerLocation
) {
    /**
     * Converts a {@link CreateStockEntryRequest} to a new {@link StockByLocation} entity.
     * This method initializes the {@link StockByLocation} entity with data directly available
     * from the request, such as product ID, location ID, stock quantity, and inner location string.
     *
     * @param request the {@link CreateStockEntryRequest} containing the data for the stock entry.
     * @return a new {@link StockByLocation} entity initialized with data from the request.
     */
    public static StockByLocation toStockByLocation(CreateStockEntryRequest request) {
        return new StockByLocation(
            request.productId(),
            request.locationId(),
            request.stock(),
            request.innerLocation()
        );
    }
}