package ar.edu.utn.frba.inventariobackend.model;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * Represents a line item within an Order, detailing a specific product and its quantity.
 * This acts as a join table between Orders and Product with an additional 'amount' attribute.
 */
@Getter
@Entity
@Table(name = "item_by_operation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_operation", "id_product", "item_type"})
})
public class ItemByOperation {

    /**
     * Auto-generated unique identifier for this order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_by_operation_seq")
    @SequenceGenerator(name = "item_by_operation_seq", sequenceName = "item_by_operation_seq", allocationSize = 1)
    private Long id;

    /**
     * The operation to which this item belongs.
     */
    @Column(name = "id_operation", nullable = false)
    private Long idOperation;

    /**
     * The product included in this operation item.
     */
    @Column(name = "id_product", nullable = false)
    private Long idProduct;

    /**
     * The type of the operation, either order or shipment.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    /**
     * The quantity of the product requested in this operation item.
     * Originally referred to as stock.
     */
    @Column(nullable = false)
    private Integer amount;

    /**
     * Default constructor required by JPA.
     */
    public ItemByOperation() {}

    /**
     * Constructs a new {@code ItemXOrder}.
     *
     * @param idOperation the id of the operation this item belongs to
     * @param idProduct   the product in this item
     * @param itemType    the type of item (shipment or order)
     * @param amount      the quantity of the product
     */
    public ItemByOperation(Long idOperation, Long idProduct, ItemType itemType, Integer amount) {
        this.idOperation = idOperation;
        this.idProduct = idProduct;
        this.itemType = itemType;
        this.amount = amount;
    }
}
