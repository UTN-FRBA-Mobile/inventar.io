package ar.edu.utn.frba.inventariobackend.controller;

import ar.edu.utn.frba.inventariobackend.dto.request.CreateStockEntryRequest; // Renamed for clarity
import ar.edu.utn.frba.inventariobackend.dto.request.ProductCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.ProductResponse;
import ar.edu.utn.frba.inventariobackend.service.ProductService;

import ar.edu.utn.frba.inventariobackend.utils.TokenUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final TokenUtils tokenUtils;

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
    @PostMapping("/stock")
    public ResponseEntity<?> addProductStockEntry(@Valid @RequestBody CreateStockEntryRequest createStockEntryRequest) {
        productService.createStockEntry(createStockEntryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Gets the stock associated to a specific location of certain products.
     *
     * @param ids An optional list of {@link Long} representing the unique identifiers of the stocks to be fetched.
     * @return A {@link ResponseEntity} with a map of stocks by product ID.
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/stock")
    public ResponseEntity<Map<Long, Integer>> getProductsStockEntry(@RequestParam Optional<List<Long>> ids) {
        Map<Long, Integer> stocks =
            productService.getStockEntries(ids.orElse(List.of()), tokenUtils.getLocationIdFromToken());
        return ResponseEntity.ok(stocks);
    }

    /**
     * Retrieves a list of products based on a provided list of product IDs and/or EAN13.
     *
     * @param ids    An optional list of {@link Long} representing the unique identifiers of the products to be fetched.
     * @param ean13s An optional list of {@link String} representing the EAN13 codes from the products to be fetched.
     * Both fields as a comma-separated query parameter (e.g., /api/v1/products/lookup?ids=1,2,3).
     * @return A {@link ResponseEntity} containing a list of {@link ProductResponse} objects for the requested products and HTTP status 200 (OK).
     * Returns an empty list if no products match the given IDs.
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Map<Long, ProductResponse>> getProductsByFilters(
        @RequestParam Optional<List<Long>> ids,
        @RequestParam Optional<List<String>> ean13s
    ) {
        Map<Long, ProductResponse> products =
            productService.getProductsByFilters(ids.orElse(List.of()), ean13s.orElse(List.of()));
        return ResponseEntity.ok(products);
    }
}