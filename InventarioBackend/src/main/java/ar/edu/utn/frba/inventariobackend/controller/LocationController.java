package ar.edu.utn.frba.inventariobackend.controller;

import ar.edu.utn.frba.inventariobackend.dto.request.LocationCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.request.LocationGetRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.LocationResponse;
import ar.edu.utn.frba.inventariobackend.model.Location;
import ar.edu.utn.frba.inventariobackend.service.LocationService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * Retrieves a list of all locations.
     *
     * @param locationGetRequest the location details used to get the locations.
     * @return a list of {@link LocationResponse} entities.
     */
    @GetMapping
    public List<LocationResponse> getLocations(@Nullable LocationGetRequest locationGetRequest) {
        // Default to get all if any field is missing.
        if (locationGetRequest == null ||
            locationGetRequest.latitude() == null ||
            locationGetRequest.longitude() == null
        ) {
            return locationService.getLocations();
        }

        return locationService.getLocationsByPosition(locationGetRequest);
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
