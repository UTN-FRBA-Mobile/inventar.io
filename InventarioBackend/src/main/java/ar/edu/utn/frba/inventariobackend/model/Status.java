package ar.edu.utn.frba.inventariobackend.model;

/**
 * Represents the possible states of an Order or Shipment.
 */
public enum Status {
    /**
     * The order/shipment has been created but not yet processed.
     */
    PENDING,

    /**
     * The order/shipment is currently being processed.
     */
    IN_PROGRESS,

    /**
     * The order/shipment has been successfully completed.
     */
    COMPLETED,

    /**
     * The order/shipment is blocked and cannot be processed.
     */
    BLOCKED,

    /**
     * The order/shipment has been cancelled.
     */
    CANCELLED,
}