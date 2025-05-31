package ar.edu.utn.frba.inventariobackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * Represents an outgoing shipment of products.
 * Shipments are not primarily managed via QR code scanning for initiation.
 */
@Getter
@Entity
@Table(name = "shipment")
public class Shipment {

    /**
     * Auto-generated unique identifier for the shipment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipment_seq")
    @SequenceGenerator(name = "shipment_seq", sequenceName = "shipment_seq", allocationSize = 1)
    private Long id;

    /**
     * The current state of the shipment.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    /**
     * The date and time when the shipment was created.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    /**
     * The date and time when the shipment was last modified.
     */
    private LocalDateTime lastModifiedDate;

    /**
     * The name of the customer or recipient of the shipment.
     */
    @Column(nullable = false)
    private String customerName;

    /**
     * Default constructor required by JPA.
     */
    public Shipment() {}

    /**
     * Constructs a new {@code Shipment}.
     *
     * @param status           the initial state of the shipment
     * @param creationDate     the creation date
     * @param lastModifiedDate the last modified date
     * @param customerName     the name of the customer
     */
    public Shipment(Status status, LocalDateTime creationDate, LocalDateTime lastModifiedDate, String customerName) {
        this.status = status;
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
        this.customerName = customerName;
    }

    /**
     * Set creation and last modified date when initially created.
     */
    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
    }

    /**
     * Update the last modified date once we perform an update on the shipment.
     */
    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}