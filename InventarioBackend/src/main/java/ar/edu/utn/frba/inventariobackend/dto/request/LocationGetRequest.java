package ar.edu.utn.frba.inventariobackend.dto.request;

import jakarta.annotation.Nullable;

/**
 * Represents a request to get available locations based in a position.
 *
 * @param latitude the latitude component of the geographic location.
 * @param longitude the longitude component of the geographic location.
 */
public record LocationGetRequest(@Nullable Double latitude, @Nullable Double longitude) {
}
