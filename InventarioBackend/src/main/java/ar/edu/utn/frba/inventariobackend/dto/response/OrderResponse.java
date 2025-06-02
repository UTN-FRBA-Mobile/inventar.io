package ar.edu.utn.frba.inventariobackend.dto.response;

import ar.edu.utn.frba.inventariobackend.model.Order;
import ar.edu.utn.frba.inventariobackend.model.Status;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents the data of an order being sent to the client.
 *
 * @param id               The unique identifier of the order.
 * @param status           The current state of the order.
 * @param creationDate     The date and time when the order was created.
 * @param scheduledDate    The date and time when the order is scheduled for processing.
 * @param lastModifiedDate The date and time when the order was last modified.
 * @param idLocation       The location of this order
 * @param sender           Identifier for who or what originated this order.
 * @param productAmount    A map with id as key and amount as value.
 */
public record OrderResponse(
    Long id,
    Status status,
    LocalDateTime creationDate,
    LocalDateTime scheduledDate,
    LocalDateTime lastModifiedDate,
    Long idLocation,
    String sender,
    Map<Long, Integer> productAmount
) {

    /**
     * Creates an OrderResponse DTO from an Orders entity.
     *
     * @param order The Order entity.
     * @param productAmount the amount of products for the order.
     * @return An OrderResponse DTO.
     */
    public static OrderResponse fromOrder(Order order, Map<Long, Integer> productAmount) {
        return new OrderResponse(
            order.getId(),
            order.getStatus(),
            order.getCreationDate(),
            order.getScheduledDate(),
            order.getLastModifiedDate(),
            order.getIdLocation(),
            order.getSender(),
            productAmount);
    }
}