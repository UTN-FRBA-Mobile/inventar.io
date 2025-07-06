package ar.edu.utn.frba.inventariobackend.dto.response;

import ar.edu.utn.frba.inventariobackend.model.Location;

/**
 * Represents a location retrieved from the database.
 *
 * @param id        the identifier of the location.
 * @param latitude  the latitude coordinate.
 * @param longitude the longitude coordinate.
 * @param radius    the radius around the location, e.g., for area of interest.
 * @param name      the name of the location.
 */

public record LocationResponse(
    long id,
    double latitude,
    double longitude,
    double radius,
    String name
) {

    /**
     * Creates a {@link LocationResponse} from a {@link Location} domain object.
     *
     * @param location the {@link Location} entity to convert.
     * @return a new {@link LocationResponse} instance with data copied from the entity.
     */
    public static LocationResponse fromLocation(Location location) {
        return new LocationResponse(
            location.getId(),
            location.getLatitude(),
            location.getLongitude(),
            location.getRadius(),
            location.getName());
    }
}
