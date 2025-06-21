package ar.edu.utn.frba.inventariobackend.service;

import ar.edu.utn.frba.inventariobackend.dto.request.CreateStockEntryRequest;
import ar.edu.utn.frba.inventariobackend.dto.request.ProductCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.ProductResponse;
import ar.edu.utn.frba.inventariobackend.model.Product;
import ar.edu.utn.frba.inventariobackend.model.StockByLocation;
import ar.edu.utn.frba.inventariobackend.repository.LocationRepository;
import ar.edu.utn.frba.inventariobackend.repository.ProductRepository;
import ar.edu.utn.frba.inventariobackend.repository.StockByLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service layer for managing {@link Product} entities and their stock levels ({@link StockByLocation}).
 * Provides methods for creating products, managing stock entries, and retrieving product information.
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final StockByLocationRepository stockByLocationRepository;
    private final LocationRepository locationRepository;

    /**
     * Creates a new product based on the provided request data.
     *
     * @param request The DTO containing data for the new product.
     * @return A {@link ProductResponse} DTO representing the newly created product.
     */
    @Transactional
    public ProductResponse createProduct(ProductCreationRequest request) {
        Product product = ProductCreationRequest.toProduct(request);
        return ProductResponse.fromProduct(productRepository.save(product));
    }

    /**
     * Creates a new stock entry for a product at a specific location.
     * This represents a {@link StockByLocation} record.
     *
     * @param request The DTO containing data for the new stock entry.
     * @throws NoSuchElementException if the specified Product or Location is not found.
     */
    @Transactional
    public void createStockEntry(CreateStockEntryRequest request) {
        // Product id validation
        productRepository.findById(request.productId())
            .orElseThrow(() -> new NoSuchElementException("Product not found with ID: " + request.productId()));

        // Location id validation
        locationRepository.findById(request.locationId())
            .orElseThrow(() -> new NoSuchElementException("Location not found with ID: " + request.locationId()));

        StockByLocation stockByLocation = CreateStockEntryRequest.toStockByLocation(request);
        stockByLocationRepository.save(stockByLocation);
    }

    /**
     * Retrieves a map of stock quantities for a given list of product IDs and a specific location.
     * <p>
     * This method queries the `stockByLocationRepository` to find stock entries that match
     * any of the provided product IDs within the specified single location.
     * The result is then transformed into a `Map` where the key is the product ID (`Long`)
     * and the value is the corresponding stock quantity (`Integer`).
     *
     * @param ids        A {@code List} of {@code Long} representing the product identifiers for which
     *                   stock entries are to be retrieved.
     * @param locationId A {@code Long} representing the unique identifier of the location
     *                   for which the stock entries are to be retrieved.
     * @return A {@code Map} where keys are product IDs ({@code Long}) and values are their
     * respective stock quantities ({@code Integer}) at the specified location.
     * If no stock entries are found for the given criteria, an empty map is returned.
     * The map will contain only products that have an entry for the specified location
     * and are present in the input {@code ids} list.
     */
    public Map<Long, Integer> getStockEntries(List<Long> ids, Long locationId) {
        List<StockByLocation> stockByLocations =
            stockByLocationRepository.findByIdProductInAndIdLocation(ids, locationId);

        Map<Long, Integer> actualStockMap = stockByLocations.stream()
            .collect(Collectors.toMap(
                StockByLocation::getIdProduct,
                StockByLocation::getStock
            ));

        return ids.stream()
            .collect(Collectors.toMap(
                productId -> productId, // Key is the product ID
                productId -> actualStockMap.getOrDefault(productId, 0)
            ));
    }

    /**
     * Retrieves a list of products based on the provided list of product IDs.
     *
     * @param ids    A list of product IDs to fetch.
     * @param ean13s A list of product EAN13s to be fetched.
     * @return A map of {@link ProductResponse} DTOs for the found products with the id as key.
     * If an ID is not found, it's silently ignored (as per `findAllById` behavior).
     */
    @Transactional
    public Map<Long, ProductResponse> getProductsByFilters(List<Long> ids, List<String> ean13s) {
        if (ids.isEmpty() && ean13s.isEmpty()) {
            return productRepository.findAll().stream()
                .collect(Collectors.toMap(
                    Product::getId,
                    ProductResponse::fromProduct
                ));
        }

        return
            Stream.concat(
                    productRepository.findAllById(ids).stream(),
                    productRepository.findAllByEan13In(ean13s).stream())
                .collect(
                    Collectors.toMap(
                        Product::getId,
                        ProductResponse::fromProduct,
                        (product1, __) -> product1));
    }
}