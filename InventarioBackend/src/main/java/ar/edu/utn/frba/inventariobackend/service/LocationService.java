package ar.edu.utn.frba.inventariobackend.service;

import ar.edu.utn.frba.inventariobackend.dto.request.LocationCreationRequest;
import ar.edu.utn.frba.inventariobackend.dto.request.VerifySelfRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.LocationResponse;
import ar.edu.utn.frba.inventariobackend.model.Location;
import ar.edu.utn.frba.inventariobackend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Service layer for managing {@link Location} entities.
 * <p>
 * Provides methods for retrieving and creating locations,
 * acting as an intermediary between the controller and repository.
 */
@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    /**
     * Retrieves all stored locations from the database.
     *
     * @return a list of all {@link LocationResponse} entities
     */
    public List<LocationResponse> getLocations() {
        return locationRepository.findAll()
            .stream()
            .map(LocationResponse::fromLocation)
            .toList();
    }

    /**
     * Retrieves an optional location based on a given position request.
     *
     * @param latitude the latitude of the coordinate.
     * @param longitude the longitude of the coordinate.
     * @return an Optional of {@link LocationResponse} which will provide the location or none if not available.
     */
    public Optional<LocationResponse> getLocationByPosition(Double latitude, Double longitude) {
        return locationRepository.findAll()
            .stream()
            .map(location -> {
                double distanceToReference = calculateDistance(
                    Objects.requireNonNull(latitude),
                    Objects.requireNonNull(longitude),
                    location.getLatitude(),
                    location.getLongitude());

                return Pair.of(location, distanceToReference);
            })
            .filter(pair -> pair.getValue() <= pair.getKey().getRadius())
            .sorted(Comparator.comparingDouble(Pair::getValue))
            .map(pair -> LocationResponse.fromLocation(pair.getKey()))
            .findFirst();
    }

    /**
     * Retrieves a location by its ID and returns it as a {@link LocationResponse}.
     *
     * @param locationId the unique identifier of the location to retrieve
     * @return the {@link LocationResponse} representing the location with the specified ID
     * @throws NoSuchElementException if no location with the given ID is found
     */
    public LocationResponse getLocationById(Long locationId) {
        return LocationResponse.fromLocation(locationRepository.findById(locationId).orElseThrow());
    }

    /**
     * Creates and stores a new {@link Location} based on the provided request.
     *
     * @param locationCreationRequest the request containing location creation data
     * @return a {@link LocationResponse} with the details of the created entity
     */
    public LocationResponse createLocation(LocationCreationRequest locationCreationRequest) {
        Location location = LocationCreationRequest.toLocation(locationCreationRequest);
        return LocationResponse.fromLocation(locationRepository.save(location));
    }

    /**
     * Calculates the distance in meters between two points on Earth using the Haversine formula.
     *
     * @param originLat  Latitude of the first point
     * @param originLon  Longitude of the first point
     * @param targetLat  Latitude of the second point
     * @param targetLon  Longitude of the second point
     * @return distance in meters
     */
    private double calculateDistance(double originLat, double originLon, double targetLat, double targetLon) {
        double earthRadius = 6371000;
        double deltaLatRadians = Math.toRadians(targetLat - originLat);
        double deltaLonRadians = Math.toRadians(targetLon - originLon);

        double originLatRadians = Math.toRadians(originLat);
        double targetLatRadians = Math.toRadians(targetLat);

        // Haversine formula components
        double haversineOfCentralAngle = Math.pow(Math.sin(deltaLatRadians / 2), 2)
                + Math.pow(Math.sin(deltaLonRadians / 2), 2)
                * Math.cos(originLatRadians) * Math.cos(targetLatRadians);

        double centralAngle = 2 * Math.asin(Math.sqrt(haversineOfCentralAngle));

        return earthRadius * centralAngle;
    }
}
