package ar.edu.utn.frba.inventariobackend.controller;

import ar.edu.utn.frba.inventariobackend.dto.request.OrderCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.request.ShipmentCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.OrderResponse;
import ar.edu.utn.frba.inventariobackend.dto.response.ShipmentResponse;

import ar.edu.utn.frba.inventariobackend.service.OperationService;

import ar.edu.utn.frba.inventariobackend.utils.TokenUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;


/**
 * REST controller for managing operational tasks such as Orders and Shipments.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService operationService;
    private final TokenUtils tokenUtils;

    /**
     * Creates a new order. This endpoint does not require authentication.
     *
     * @param orderCreationRequest The request DTO containing details for the new order.
     * @return A {@link ResponseEntity} containing the {@link OrderResponse} of the newly created order and HTTP status 201 (Created).
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreationRequest orderCreationRequest) {
        OrderResponse createdOrder = operationService.createOrder(orderCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * Creates a new shipment. This endpoint does not require authentication.
     *
     * @param shipmentCreationRequest The request DTO containing details for the new shipment.
     * @return A {@link ResponseEntity} containing the {@link ShipmentResponse} of the newly created shipment and HTTP status 201 (Created).
     */
    @PostMapping("/shipments")
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody ShipmentCreationRequest shipmentCreationRequest) {
        ShipmentResponse createdShipment = operationService.createShipment(shipmentCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShipment);
    }

    /**
     * Retrieves a list of all orders. Requires authentication.
     *
     * @return A {@link ResponseEntity} containing a list of {@link OrderResponse} objects and HTTP status 200 (OK).
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        List<OrderResponse> orders = operationService.getAllOrders(tokenUtils.getLocationIdFromToken());
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves a list of all shipments. Requires authentication.
     *
     * @return A {@link ResponseEntity} containing a list of {@link ShipmentResponse} objects and HTTP status 200 (OK).
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/shipments")
    public ResponseEntity<List<ShipmentResponse>> getShipments() {
        List<ShipmentResponse> shipments = operationService.getAllShipments(tokenUtils.getLocationIdFromToken());
        return ResponseEntity.ok(shipments);
    }

    /**
     * Retrieves a shipment. Requires authentication.
     *
     * @param id The identifier of the shipment.
     * @return A {@link ResponseEntity} containing a {@link ShipmentResponse} object (or a 404 if not found).
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/shipments/{id}")
    public ResponseEntity<ShipmentResponse> getShipment(@PathVariable long id) {
        Optional<ShipmentResponse> optionalShipment = operationService.getShipment(id);
        return optionalShipment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves an order. Requires authentication.
     *
     * @param id The identifier of the order.
     * @return A {@link ResponseEntity} containing an {@link OrderResponse} object (or a 404 if not found).
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable long id) {
        Optional<OrderResponse> optionalOrder = operationService.getOrder(id);
        return optionalOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Initiates the start process for a specific shipment.
     * <p>
     * This endpoint requires authentication with a bearer token.
     * When invoked, it delegates the business logic to the {@code operationService}
     * to begin the processing of the shipment identified by the provided ID.
     * </p>
     *
     * @param id The unique identifier of the shipment to be started.
     * @return A {@link ResponseEntity} containing a {@link ShipmentResponse} object
     * that represents the outcome or details of the started shipment operation.
     * The HTTP status code will reflect the success or failure of the operation.
     */
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/shipments/{id}/start")
    public ResponseEntity<?> startShipment(@PathVariable long id) {
        try {
            return ResponseEntity.ok(operationService.startShipment(id));
        } catch (NoSuchElementException __) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Initiates the start process for a specific order.
     * <p>
     * This endpoint requires authentication with a bearer token.
     * When invoked, it delegates the business logic to the {@code operationService}
     * to begin the processing of the order identified by the provided ID.
     * </p>
     *
     * @param id The unique identifier of the order to be started.
     * @return A {@link ResponseEntity} containing an {@link OrderResponse} object
     * that represents the outcome or details of the started order operation.
     * The HTTP status code will reflect the success or failure of the operation.
     */
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/orders/{id}/start")
    public ResponseEntity<?> startOrder(@PathVariable long id) {
        try {
            return ResponseEntity.ok(operationService.startOrder(id));
        } catch (NoSuchElementException __) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Finishes the processing of a specific shipment.
     * <p>
     * This endpoint requires authentication with a bearer token. It delegates the finishing
     * logic to the {@code operationService}.
     * </p>
     *
     * @param id The unique identifier of the shipment to be finished.
     * @return A {@link ResponseEntity} containing:
     * <ul>
     * <li>{@code 200 OK} with a {@link ShipmentResponse} if the shipment is successfully finished.</li>
     * <li>{@code 404 Not Found} if no shipment with the given ID exists.</li>
     * <li>{@code 400 Bad Request} with an error message if the shipment cannot be finished due to its
     * current state or business rules.</li>
     * </ul>
     */
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/shipments/{id}/finish")
    public ResponseEntity<?> finishShipment(@PathVariable long id) {
        try {
            return ResponseEntity.ok(operationService.finishShipment(id));
        } catch (NoSuchElementException __) {
            return ResponseEntity.notFound().build();
        }  catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Finishes the processing of a specific order, including details about processed stock.
     * <p>
     * This endpoint requires authentication with a bearer token. It processes the order and
     * the stock quantities associated with its completion, delegating the logic to the
     * {@code operationService}.
     * </p>
     *
     * @param id             The unique identifier of the order to be finished.
     * @param processedStock A {@code Map} where keys are product IDs ({@code Long}) and values
     * are the quantities ({@code Integer}) of those products that were processed/fulfilled
     * as part of finishing this order. This body is subject to validation.
     * @return A {@link ResponseEntity} containing:
     * <ul>
     * <li>{@code 200 OK} with an {@link OrderResponse} if the order is successfully finished.</li>
     * <li>{@code 404 Not Found} if no order with the given ID exists.</li>
     * <li>{@code 400 Bad Request} with an error message if the order cannot be finished
     * due to its current state, business rules, or if the {@code processedStock}
     * payload is invalid.</li>
     * </ul>
     */
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/orders/{id}/finish")
    public ResponseEntity<?> finishOrder(@PathVariable long id, @Valid @RequestBody Map<Long, Integer> processedStock) {
        try {
            return ResponseEntity.ok(operationService.finishOrder(id, processedStock));
        } catch (NoSuchElementException __) {
            return ResponseEntity.notFound().build();
        }  catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Blocks a specific shipment, preventing further processing.
     * <p>
     * This endpoint requires authentication with a bearer token. It marks the shipment as
     * blocked, delegating the status update logic to the {@code operationService}.
     * </p>
     *
     * @param id The unique identifier of the shipment to be blocked.
     * @return A {@link ResponseEntity} containing:
     * <ul>
     * <li>{@code 200 OK} with a {@link ShipmentResponse} if the shipment is successfully blocked.</li>
     * <li>{@code 404 Not Found} if no shipment with the given ID exists.</li>
     * <li>{@code 400 Bad Request} with an error message if the shipment cannot be blocked due to its current state or business rules.</li>
     * </ul>
     */
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/shipments/{id}/block")
    public ResponseEntity<?> blockShipment(@PathVariable long id) {
        try {
            return ResponseEntity.ok(operationService.blockShipment(id));
        } catch (NoSuchElementException __) {
            return ResponseEntity.notFound().build();
        }  catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}