package ar.edu.utn.frba.inventariobackend.dto.response;

import java.util.Map;

/**
 * DTO for sending stock information, including product quantities and
 * associated location names.
 *
 * @param stockCount
 *            A {@code Map} where the key is the product ID ({@code Long}) and
 *            the value is the stock quantity ({@code Integer}) for that
 *            product.
 * @param locationDetails
 *            A {@code Map} where the key is the product ID ({@code Long}) and
 *            the value is the name of that location ({@code String}).
 */
public record StockResponse(Map<Long, Integer> stockCount, Map<Long, String> locationDetails) {
}
