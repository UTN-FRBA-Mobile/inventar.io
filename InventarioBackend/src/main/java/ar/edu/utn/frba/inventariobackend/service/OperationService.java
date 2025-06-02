package ar.edu.utn.frba.inventariobackend.service;

import ar.edu.utn.frba.inventariobackend.dto.request.OrderCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.request.ShipmentCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.OrderResponse;
import ar.edu.utn.frba.inventariobackend.dto.response.ShipmentResponse;
import ar.edu.utn.frba.inventariobackend.model.*;
import ar.edu.utn.frba.inventariobackend.repository.ItemByOperationRepository;
import ar.edu.utn.frba.inventariobackend.repository.OrderRepository;
import ar.edu.utn.frba.inventariobackend.repository.ProductRepository;
import ar.edu.utn.frba.inventariobackend.repository.ShipmentRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Service layer for managing operations like Orders and Shipments.
 */
@Service
@RequiredArgsConstructor
public class OperationService {
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final ItemByOperationRepository itemByOperationRepository;

    /**
     * Creates a new order with its associated items.
     *
     * @param request The DTO containing details for the new order and its items.
     * @return An {@link OrderResponse} representing the newly created order.
     * @throws NoSuchElementException if any product ID in the order items is not found.
     */
    @Transactional
    public OrderResponse createOrder(OrderCreationRequest request) {
        Order order = new Order(
            Status.PENDING,
            null,
            request.scheduledDate(),
            null,
            request.idLocation(),
            request.sender());

        Order pushedOrder = orderRepository.save(order);

        var itemsByOperation = request.productAmount()
            .entrySet()
            .stream()
            .map(entry -> new ItemByOperation(pushedOrder.getId(), entry.getKey(), ItemType.ORDER, entry.getValue()))
            .toList();

        itemByOperationRepository.saveAll(itemsByOperation);

        return OrderResponse.fromOrder(pushedOrder, request.productAmount());
    }


    /**
     * Creates a new shipment with its associated items.
     *
     * @param request The DTO containing details for the new shipment and its items.
     * @return A {@link ShipmentResponse} representing the newly created shipment.
     * @throws NoSuchElementException if any product ID in the shipment items is not found.
     */
    @Transactional
    public ShipmentResponse createShipment(ShipmentCreationRequest request) {
        Shipment shipment = new Shipment(
            Status.PENDING,
            null,
            null,
            request.idLocation(),
            request.customerName());

        Shipment pushedShipment = shipmentRepository.save(shipment);

        var itemsByOperation = request.productAmount()
            .entrySet()
            .stream()
            .map(entry ->
                 new ItemByOperation(pushedShipment.getId(), entry.getKey(), ItemType.ORDER, entry.getValue()))
            .toList();

        itemByOperationRepository.saveAll(itemsByOperation);

        return ShipmentResponse.fromShipment(shipment, request.productAmount());
    }

    /**
     * Retrieves all orders associated with a specific location ID.
     *
     * @param locationId The ID of the location to filter orders by.
     * @return A list of {@link OrderResponse} objects.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(Long locationId) {
        if (locationId == null) {
            throw new IllegalArgumentException("Location ID must be provided to filter orders.");
        }

        return orderRepository.findAllByIdLocation(locationId)
                .stream()
                .map(order -> {
                    Map<Long, Integer> productAmount = itemByOperationRepository
                        .findAllByIdOperationAndItemType(order.getId(), ItemType.ORDER)
                        .stream()
                        .collect(Collectors.toMap(ItemByOperation::getIdProduct, ItemByOperation::getAmount));
                    return OrderResponse.fromOrder(order, productAmount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all shipments associated with a specific location ID.
     *
     * @param locationId The ID of the location to filter shipments by.
     * @return A list of {@link ShipmentResponse} objects.
     */
    @Transactional(readOnly = true)
    public List<ShipmentResponse> getAllShipments(Long locationId) {
        if (locationId == null) {
            throw new IllegalArgumentException("Location ID must be provided to filter shipments.");
        }

        return shipmentRepository.findAllByIdLocation(locationId)
                .stream()
                .map(shipment -> {
                    Map<Long, Integer> productAmount = itemByOperationRepository
                        .findAllByIdOperationAndItemType(shipment.getId(), ItemType.SHIPMENT)
                        .stream()
                        .collect(Collectors.toMap(ItemByOperation::getIdProduct, ItemByOperation::getAmount));
                    return ShipmentResponse.fromShipment(shipment, productAmount);
                })
                .collect(Collectors.toList());
    }
}