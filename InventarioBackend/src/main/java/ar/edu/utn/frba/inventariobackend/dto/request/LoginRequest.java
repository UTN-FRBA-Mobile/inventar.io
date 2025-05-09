package ar.edu.utn.frba.inventariobackend.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Represents the login request payload used to authenticate a user.
 *
 * @param username  the user's username (required).
 * @param password  the user's password (required).
 * @param latitude  the latitude of the user's current location (required).
 * @param longitude the longitude of the user's current location (required).
 */
public record LoginRequest(
        @NotNull String username,
        @NotNull String password,
        @NotNull Double latitude,
        @NotNull Double longitude
) {
}