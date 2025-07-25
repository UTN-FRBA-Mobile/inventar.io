package ar.edu.utn.frba.inventariobackend.service;

import ar.edu.utn.frba.inventariobackend.dto.request.CreateStockEntryRequest;
import ar.edu.utn.frba.inventariobackend.dto.request.ProductCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.StockResponse;
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
     * Retrieves detailed stock information for a list of products at a given location.
     * <p>
     * Returns stock counts (defaulting to 0) and inner location strings (defaulting to "")
     * for all requested product IDs within the specified location.
     * </p>
     *
     * @param ids        List of product identifiers.
     * @param locationId Unique identifier of the location.
     * @return A {@link StockResponse} with product IDs mapped to their stock counts and inner location strings.
     */
    public StockResponse getStockDetails(List<Long> ids, Long locationId) {
        List<StockByLocation> stockByLocations =
            stockByLocationRepository.findByIdProductInAndIdLocation(ids, locationId);

        Map<Long, Map.Entry<Integer, String>> detailsMap = stockByLocations.stream()
            .collect(Collectors.toMap(
                StockByLocation::getIdProduct,
                stock -> Map.entry(stock.getStock(), stock.getInnerLocation())
            ));

        Map.Entry<Integer, String> defaultEntry = Map.entry(0, "Desconocido");
        return new StockResponse(
            ids.stream()
                .collect(Collectors.toMap(
                    productId -> productId, // Key is the product ID
                    productId -> detailsMap.getOrDefault(productId, defaultEntry).getKey())),
            ids.stream()
                .collect(Collectors.toMap(
                    productId -> productId, // Key is the product ID
                    productId -> detailsMap.getOrDefault(productId, defaultEntry).getValue()))
        );
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