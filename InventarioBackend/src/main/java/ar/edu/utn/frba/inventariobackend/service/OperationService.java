package ar.edu.utn.frba.inventariobackend.service;

import ar.edu.utn.frba.inventariobackend.dto.request.OrderCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.request.ShipmentCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.OrderResponse;
import ar.edu.utn.frba.inventariobackend.dto.response.ShipmentResponse;
import ar.edu.utn.frba.inventariobackend.model.*;
import ar.edu.utn.frba.inventariobackend.repository.*;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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
    private final StockByLocationRepository stockByLocationRepository;

    private final Map<Long, Object> shipmentLocks = new ConcurrentHashMap<>();
    private final Map<Long, Object> orderLocks = new ConcurrentHashMap<>();

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
    @Transactional(readOnly = true)
    public Optional<ShipmentResponse> getShipment(long id) {
        return shipmentRepository.findById(id).map(this::getShipmentResponse);
    }

    /**
     * Retrieves an optional order associated to an ID.
     *
     * @param id The ID of the order.
     * @return An optional {@link OrderResponse} referencing the order.
     */
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrder(long id) {
        return orderRepository.findById(id).map(this::getOrderResponse);
    }

    /**
     * Initiates the processing of a specific shipment.
     * <p>
     * This method ensures thread-safe execution per shipment ID, validates the shipment's
     * {@link Status#PENDING} state, and checks for sufficient stock at the shipment's location.
     * The shipment's status is updated to {@link Status#IN_PROGRESS} if stock is available,
     * or to {@link Status#BLOCKED} otherwise.
     * </p>
     *
     * @param id The unique identifier of the shipment to be started.
     * @return A {@link ShipmentResponse} reflecting the updated shipment status.
     * @throws NoSuchElementException If the shipment is not found.
     * @throws IllegalStateException If the shipment is not pending or if there is insufficient stock.
     */
    @Transactional(noRollbackFor = IllegalStateException.class)
    public ShipmentResponse startShipment(long id) {
        Object lockObject = shipmentLocks.computeIfAbsent(id, k -> new Object());

        synchronized (lockObject) {
            Shipment shipment = shipmentRepository.findById(id).orElseThrow(NoSuchElementException::new);

            if (shipment.getStatus() != Status.PENDING) {
                throw new IllegalStateException("Shipment not pending");
            }

            Map<Long, Integer> requiredStock = getProductAmount(shipment.getId(), ItemType.SHIPMENT);

            // Validate stock related to the shipment.
            List<StockByLocation> stocksByLocation = stockByLocationRepository
                .findByIdProductInAndIdLocation(requiredStock.keySet().stream().toList(), shipment.getIdLocation());

            Map<Long, Integer> actualStock = stocksByLocation.stream()
                .map(stockByLocation -> Map.entry(stockByLocation.getIdProduct(), stockByLocation.getStock()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            boolean enoughStock = requiredStock.entrySet()
                .stream()
                .allMatch(entry -> entry.getValue() <= actualStock.getOrDefault(entry.getKey(), 0));

            shipment.updateStatus(enoughStock ? Status.IN_PROGRESS : Status.BLOCKED);
            shipmentRepository.save(shipment);

            if (!enoughStock) {
                throw new IllegalStateException("Not enough stock");
            }

            return getShipmentResponse(shipment);
        }
    }

    /**
     * Initiates the processing of a specific order.
     * <p>
     * This method ensures thread-safe execution per order ID and validates the order's
     * {@link Status#PENDING} state. The order's status is updated to {@link Status#PENDING}.
     * </p>
     *
     * @param id The unique identifier of the order to be started.
     * @return An {@link OrderResponse} reflecting the updated order status.
     * @throws NoSuchElementException If the order is not found.
     * @throws IllegalStateException If the order is not pending.
     */
    @Transactional
    public OrderResponse startOrder(long id) {
        Object lockObject = orderLocks.computeIfAbsent(id, k -> new Object());

        synchronized (lockObject) {
            Order order = orderRepository.findById(id).orElseThrow(NoSuchElementException::new);

            if (order.getStatus() != Status.PENDING) {
                throw new IllegalStateException("Shipment not pending");
            }

            order.updateStatus(Status.IN_PROGRESS);
            orderRepository.save(order);
            return getOrderResponse(order);
        }
    }

    /**
     * Completes a specific shipment, making it thread-safe by ID.
     * <p>
     * Updates shipment status to {@link Status#COMPLETED} and deducts required stock.
     * </p>
     *
     * @param id The unique identifier of the shipment to complete.
     * @return A {@link ShipmentResponse} for the completed shipment.
     * @throws NoSuchElementException If the shipment is not found.
     * @throws IllegalStateException If the shipment is not in {@link Status#IN_PROGRESS}.
     */
    @Transactional
    public ShipmentResponse finishShipment(long id) {
        Object lockObject = shipmentLocks.computeIfAbsent(id, k -> new Object());

        synchronized (lockObject) {
            Shipment shipment = shipmentRepository.findById(id).orElseThrow(NoSuchElementException::new);

            if (shipment.getStatus() != Status.IN_PROGRESS) {
                throw new IllegalStateException("Shipment not in progress");
            }

            shipment.updateStatus(Status.COMPLETED);
            shipmentRepository.save(shipment);

            // Update stock available
            Map<Long, Integer> requiredStock = getProductAmount(shipment.getId(), ItemType.SHIPMENT);
            List<StockByLocation> stocksByLocation = stockByLocationRepository
                .findByIdProductInAndIdLocation(requiredStock.keySet().stream().toList(), shipment.getIdLocation());
            stocksByLocation.forEach(
                stockByLocation -> stockByLocation.takeStock(requiredStock.get(stockByLocation.getIdProduct())));
            stockByLocationRepository.saveAll(stocksByLocation);

            return getShipmentResponse(shipment);
        }
    }

    /**
     * Completes a specific order, making it thread-safe by ID.
     * <p>
     * Updates order status to {@link Status#COMPLETED} and deducts stock using the provided
     * `processedStock` quantities.
     * </p>
     *
     * @param id             The unique identifier of the order to complete.
     * @param processedStock Map of product IDs to quantities to deduct from stock.
     * @return An {@link OrderResponse} for the completed order.
     * @throws NoSuchElementException If the order is not found.
     * @throws IllegalStateException If the order is not in {@link Status#IN_PROGRESS}.
     */
    @Transactional
    public OrderResponse finishOrder(long id, Map<Long, Integer> processedStock) {
        Object lockObject = orderLocks.computeIfAbsent(id, k -> new Object());

        synchronized (lockObject) {
            Order order = orderRepository.findById(id).orElseThrow(NoSuchElementException::new);

            if (order.getStatus() != Status.IN_PROGRESS) {
                throw new IllegalStateException("Order not in progress");
            }

            order.updateStatus(Status.COMPLETED);
            orderRepository.save(order);

            List<StockByLocation> stocksByLocation = stockByLocationRepository
                .findByIdProductInAndIdLocation(processedStock.keySet().stream().toList(), order.getIdLocation());

            stocksByLocation.forEach(stockByLocation -> {
                stockByLocation.addStock(processedStock.getOrDefault(stockByLocation.getIdProduct(), 0));
            });
            stockByLocationRepository.saveAll(stocksByLocation);

            return getOrderResponse(order);
        }
    }

    /**
     * Blocks a specific shipment, making it thread-safe by ID.
     * <p>
     * Updates the shipment's status to {@link Status#BLOCKED} without performing any
     * stock operations. This indicates that the shipment's processing has been halted.
     * </p>
     *
     * @param id The unique identifier of the shipment to block.
     * @return A {@link ShipmentResponse} for the blocked shipment.
     * @throws NoSuchElementException If the shipment is not found.
     * @throws IllegalStateException If the shipment cannot be blocked from its current status.
     */
    @Transactional
    public ShipmentResponse blockShipment(long id) {
        Object lockObject = shipmentLocks.computeIfAbsent(id, k -> new Object());

        synchronized (lockObject) {
            Shipment shipment = shipmentRepository.findById(id).orElseThrow(NoSuchElementException::new);

            if (shipment.getStatus() != Status.IN_PROGRESS) {
                throw new IllegalStateException("Shipment not in progress");
            }

            shipment.updateStatus(Status.BLOCKED);
            shipmentRepository.save(shipment);

            return getShipmentResponse(shipment);
        }
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