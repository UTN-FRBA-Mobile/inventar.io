package ar.edu.utn.frba.inventariobackend.controller;

import ar.edu.utn.frba.inventariobackend.dto.request.LocationCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.request.LocationGetRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.LocationResponse;
import ar.edu.utn.frba.inventariobackend.model.Location;
import ar.edu.utn.frba.inventariobackend.service.LocationService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * REST controller for managing locations.
 * Handles HTTP requests related to the {@link Location} entity.
 */
@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    /**
     * Retrieves a location based in a certain position.
     *
     * @param locationGetRequest the location details used to get the locations.
     * @return a {@link LocationResponse} entity or it throws an exception if not available.
     */
    @GetMapping
    public LocationResponse getLocations(@Valid @NotNull LocationGetRequest locationGetRequest) {
        Optional<LocationResponse> possibleLocation = locationService.getLocationByPosition(locationGetRequest);
        return possibleLocation.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No está ubicado en ningun depósito"));
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
