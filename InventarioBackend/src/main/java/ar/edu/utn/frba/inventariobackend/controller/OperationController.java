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
}