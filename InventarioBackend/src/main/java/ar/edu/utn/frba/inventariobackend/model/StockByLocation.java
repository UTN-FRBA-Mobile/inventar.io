package ar.edu.utn.frba.inventariobackend.model;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * Represents the stock of a specific product at a specific location.
 */
@Getter
@Entity
@Table(name = "stock_by_location", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_product", "id_location"})
})
public class StockByLocation {

    /**
     * Auto-generated unique identifier for this stock entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_by_location_seq")
    @SequenceGenerator(name = "stock_by_location_seq", sequenceName = "stock_by_location_seq", allocationSize = 1)
    private Long id;

    /**
     * The product whose stock is being tracked.
     */
    @Column(name = "id_product", nullable = false)
    private Long idProduct;

    /**
     * The identifier of the location where the product is stored.
     */
    @Column(name = "id_location", nullable = false)
    private Long idLocation;

    /**
     * The quantity of the product available at this location.
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * A string describing a more specific location within the main location
     * (e.g., aisle, shelf, bin).
     */
    @Column(name = "inner_location")
    private String innerLocation;

    /**
     * Default constructor required by JPA.
     */
    public StockByLocation() {}

    /**
     * Constructs a new {@code ProductXLocation} entry.
     *
     * @param idProduct     the product
     * @param idLocation    the identifier of the location
     * @param stock         the stock quantity
     * @param innerLocation a specific sub-location string
     */
    public StockByLocation(Long idProduct, Long idLocation, Integer stock, String innerLocation) {
        this.idProduct = idProduct;
        this.idLocation = idLocation;
        this.stock = stock;
        this.innerLocation = innerLocation;
    }
}