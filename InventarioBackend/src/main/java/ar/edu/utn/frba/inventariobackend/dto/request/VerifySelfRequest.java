package ar.edu.utn.frba.inventariobackend.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Represents a request to get available locations based in a position.
 *
 * @param latitude the latitude component of the geographic location.
 * @param longitude the longitude component of the geographic location.
 */
public record VerifySelfRequest(@NotNull Double latitude, @NotNull Double longitude) {
}
