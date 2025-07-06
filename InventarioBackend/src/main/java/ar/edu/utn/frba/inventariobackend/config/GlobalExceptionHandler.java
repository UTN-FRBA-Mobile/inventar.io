package ar.edu.utn.frba.inventariobackend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * <p>
 * This class uses {@link RestControllerAdvice} to act as a centralized point
 * for handling exceptions that occur during request processing. It ensures that
 * unhandled exceptions are caught and formatted into a consistent JSON response
 * with a 500 Internal Server Error status, preventing raw stack traces from
 * being sent to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * Handles any unhandled {@link Exception}, treating it as a server error.
	 * <p>
	 * This method serves as a catch-all for any exception that is not specifically
	 * handled by other handlers. It logs the exception for debugging purposes and
	 * returns a generic, user-friendly JSON response with an HTTP status of 500.
	 *
	 * @param ex
	 *            The exception that was thrown.
	 * @return A {@link ResponseEntity} containing the structured error message and
	 *         a 500 status code.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleAllExceptions(Exception ex) {
		if (ex instanceof ResponseStatusException rsex) {
			return new ResponseEntity<>(rsex.getMessage(), rsex.getStatusCode());
		}

		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
