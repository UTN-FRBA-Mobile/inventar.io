package ar.edu.utn.frba.inventariobackend.controller;

import ar.edu.utn.frba.inventariobackend.dto.request.LocationCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.request.VerifySelfRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.LocationResponse;
import ar.edu.utn.frba.inventariobackend.model.Location;
import ar.edu.utn.frba.inventariobackend.service.LocationService;
import ar.edu.utn.frba.inventariobackend.utils.TokenUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * REST controller for managing locations.
 * Handles HTTP requests related to the {@link Location} entity and performs actions such as retrieving,
 * creating, and verifying user locations.
 */
@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;
    private final TokenUtils tokenUtils;

    /**
     * Retrieves the location of the authenticated user (self) using the location ID stored in the authentication details.
     *
     * @return the {@link LocationResponse} representing the location of the authenticated user
     * @throws ResponseStatusException if the authentication details are not found or invalid (HTTP 403 Forbidden)
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/self")
    public LocationResponse getSelfLocation() {
        return locationService.getLocationById(tokenUtils.getLocationIdFromToken());
    }

    /**
     * Verifies if the provided latitude and longitude match the location of the authenticated user.
     *
     * @param verifySelfRequest the request containing latitude and longitude to be verified
     * @return {@code true} if the provided coordinates match the authenticated user's location, {@code false} otherwise
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/verify")
    public boolean verifySelfLocation(@Valid @NotNull VerifySelfRequest verifySelfRequest) {
        Optional<LocationResponse> location =
                locationService.getLocationByPosition(verifySelfRequest.latitude(), verifySelfRequest.longitude());

        return location.isPresent() && tokenUtils.getLocationIdFromToken().equals(location.get().id());
    }

    /**
     * Creates a new location based on the provided request.
     *
     * @param locationCreationRequest the location creation data.
     * @return the created {@link LocationResponse} DTO.
     */
    @PostMapping()
    public LocationResponse createLocation(@RequestBody LocationCreationRequest locationCreationRequest) {
        return locationService.createLocation(locationCreationRequest);
    }
}
