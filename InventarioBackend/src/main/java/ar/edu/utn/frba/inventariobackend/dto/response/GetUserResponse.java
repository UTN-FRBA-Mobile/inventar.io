package ar.edu.utn.frba.inventariobackend.dto.response;

import ar.edu.utn.frba.inventariobackend.model.Location;
import ar.edu.utn.frba.inventariobackend.model.User;

import java.util.List;

/**
 * DTO for sending user details in responses.
 * This record excludes sensitive information such as the password.
 *
 * @param id               the unique identifier of the user
 * @param username         the username of the user
 * @param name             the full name of the user
 * @param base64image      a base64-encoded image of the user
 * @param allowedLocations a list of locations the user is allowed to work from
 */
public record GetUserResponse(
    Long id,
    String username,
    String name,
    String base64image,
    List<LocationResponse> allowedLocations
) {
    /**
     * Creates a {@code GetUserResponse} from a {@code User} entity.
     *
     * @param user the user entity to map from
     * @return a {@code GetUserResponse} with all non-sensitive fields from the user
     */
    public static GetUserResponse from(User user, List<Location> locations) {
        return new GetUserResponse(
            user.getId(),
            user.getUsername(),
            user.getName(),
            user.getBase64image(),
            locations.stream().map(LocationResponse::fromLocation).toList()
        );
    }
}
