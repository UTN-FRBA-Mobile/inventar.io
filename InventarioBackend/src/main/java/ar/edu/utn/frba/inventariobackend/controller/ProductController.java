package ar.edu.utn.frba.inventariobackend.controller;

import ar.edu.utn.frba.inventariobackend.dto.request.CreateStockEntryRequest; // Renamed for clarity
import ar.edu.utn.frba.inventariobackend.dto.request.ProductCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.ProductResponse;
import ar.edu.utn.frba.inventariobackend.service.ProductService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing products and their stock levels in various locations.
 * Provides endpoints for creating products, managing product stock (StockByLocation),
 * and retrieving product information.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    // private final TokenUtils tokenUtils; // Uncomment if needed for user/location context from token

    // --- Placeholder DTOs (would be in separate files) ---
    // record ProductCreationRequest(String name, String description, String ean13, String imagen_base64) {}
    // record ProductResponse(Long id, String name, String description, String ean13, String imagen_base64) {}
    // record CreateStockEntryRequest(Long productId, Long locationId, int stock, String innerLocation) {}
    // record StockByLocationResponse(Long id, Long productId, String productName, Long locationId, String locationName, int stock, String innerLocation) {}


    /**
     * Creates a new product in the system.
     *
     * @param productCreationRequest A DTO containing the necessary information to create a new product.
     * @return A {@link ResponseEntity} containing the {@link ProductResponse} of the newly created product and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreationRequest productCreationRequest) {
        ProductResponse createdProduct = productService.createProduct(productCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Adds a product stock entry to a specific location (creates a StockByLocation record).
     * This endpoint is used to define how much of a certain product exists in a particular location,
     * including its specific placement within that location.
     *
     * @param createStockEntryRequest A DTO containing product ID, location ID, stock quantity, and inner-location details.
     * @return A {@link ResponseEntity} with HTTP status 201 (Created).
     */
    @PostMapping("/{id}/stock")
    public ResponseEntity<?> addProductStockEntry(@Valid @RequestBody CreateStockEntryRequest createStockEntryRequest) {
        productService.createStockEntry(createStockEntryRequest); // Assuming service method name change
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retrieves a list of products based on a provided list of product IDs.
     *
     * @param ids A list of {@link Long} representing the unique identifiers of the products to be fetched.
     * Passed as a comma-separated query parameter (e.g., /api/v1/products/lookup?ids=1,2,3).
     * @return A {@link ResponseEntity} containing a list of {@link ProductResponse} objects for the requested products and HTTP status 200 (OK).
     * Returns an empty list if no products match the given IDs.
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/lookup")
    public ResponseEntity<Map<Long, ProductResponse>> getProductsByIds(@RequestParam List<Long> ids) {
        Map<Long, ProductResponse> products = productService.getProductsByIds(ids);
        return ResponseEntity.ok(products);
    }
}