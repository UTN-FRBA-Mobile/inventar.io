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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for managing operations like Orders and Shipments.
 */
@Service
@RequiredArgsConstructor
public class OperationService {
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final ProductRepository productRepository;
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

        return OrderResponse.fromOrder(
            pushedOrder, request.productAmount(), getProductNames(request.productAmount()));
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
                 new ItemByOperation(pushedShipment.getId(), entry.getKey(), ItemType.SHIPMENT, entry.getValue()))
            .toList();

        itemByOperationRepository.saveAll(itemsByOperation);

        return ShipmentResponse.fromShipment(
            shipment, request.productAmount(), getProductNames(request.productAmount()));
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
                .map(this::getOrderResponse)
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
                .map(this::getShipmentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an optional shipment associated to an ID.
     *
     * @param id The ID of the shipment.
     * @return An optional {@link ShipmentResponse} referencing the shipment.
     */
    public Optional<ShipmentResponse> getShipment(long id) {
        return shipmentRepository.findById(id).map(this::getShipmentResponse);
    }

    /**
     * Retrieves an optional order associated to an ID.
     *
     * @param id The ID of the order.
     * @return An optional {@link OrderResponse} referencing the order.
     */
    public Optional<OrderResponse> getOrder(long id) {
        return orderRepository.findById(id).map(this::getOrderResponse);
    }

    /**
     * Utility method to get the product amount related to an operation.
     *
     * @param operationId The id of the associated operation.
     * @param itemType    The type of the item which can be ORDER or SHIPMENT.
     * @return A {@link Map} with the products and it's amounts.
     */
    private Map<Long, Integer> getProductAmount(long operationId, ItemType itemType) {
        return itemByOperationRepository
            .findAllByIdOperationAndItemType(operationId, itemType)
            .stream()
            .collect(Collectors.toMap(ItemByOperation::getIdProduct, ItemByOperation::getAmount));
    }

    /**
     * Utility method to get the product names.
     *
     * @param productAmount The map of product amounts from where to know the products to fetch.
     * @return A {@link Map} with the products and it's names.
     */
    private Map<Long, String> getProductNames(Map<Long, Integer> productAmount) {
        return productAmount.keySet()
            .stream()
            .map(id -> Map.entry(id, productRepository.getReferenceById(id).getName()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Utility method to get the order response from an order.
     *
     * @param order The order to generate the response from.
     * @return An {@link OrderResponse} with the order.
     */
    private OrderResponse getOrderResponse(Order order) {
        Map<Long, Integer> productAmount = getProductAmount(order.getId(), ItemType.ORDER);
        Map<Long, String> productNames = getProductNames(productAmount);
        return OrderResponse.fromOrder(order, productAmount, productNames);
    }

    /**
     * Utility method to get the order response from a shipment.
     *
     * @param shipment The shipment to generate the response from.
     * @return A {@link ShipmentResponse} with the shipment.
     */
    private ShipmentResponse getShipmentResponse(Shipment shipment) {
        Map<Long, Integer> productAmount = getProductAmount(shipment.getId(), ItemType.SHIPMENT);
        Map<Long, String> productNames = getProductNames(productAmount);
        return ShipmentResponse.fromShipment(shipment, productAmount, productNames);
    }
}