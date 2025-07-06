package ar.edu.utn.frba.inventariobackend.controller;

import ar.edu.utn.frba.inventariobackend.auth.JwtUtil;
import ar.edu.utn.frba.inventariobackend.dto.request.LoginRequest;
import ar.edu.utn.frba.inventariobackend.dto.request.TokenResponse;
import ar.edu.utn.frba.inventariobackend.dto.response.LocationResponse;
import ar.edu.utn.frba.inventariobackend.model.User;
import ar.edu.utn.frba.inventariobackend.service.LocationService;
import ar.edu.utn.frba.inventariobackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Controller for authentication-related endpoints.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
	private final JwtUtil jwtUtil;
	private final LocationService locationService;
	private final PasswordEncoder passwordEncoder;
	private final UserService userService;

	/**
	 * Handles user login by validating credentials and returning JWT tokens.
	 *
	 * @param request
	 *            the login request containing username, password, latitude, and
	 *            longitude
	 * @return a {@link ResponseEntity} containing access and refresh tokens, or
	 *         error if login fails
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		Optional<User> possibleUser = userService.findByUsername(request.username());

		if (possibleUser.isEmpty() || !passwordEncoder.matches(request.password(), possibleUser.get().getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("wrong credentials");
		}

		Optional<LocationResponse> location = locationService.getLocationByPosition(request.latitude(),
				request.longitude());

		if (location.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("no location");
		}

		if (!possibleUser.get().getAllowedLocations().contains(location.get().id())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("location unauthorized");
		}

		String accessToken = jwtUtil.generateToken(request.username(), location.get().id(), false);
		String refreshToken = jwtUtil.generateToken(request.username(), location.get().id(), true);

		return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
	}

	/**
	 * Refreshes the access token using a valid refresh token.
	 *
	 * @param originalRefreshToken
	 *            the refresh token provided by the client
	 * @return a {@link ResponseEntity} containing a new access token and the same
	 *         refresh token if valid; otherwise, returns HTTP 401 Unauthorized
	 */
	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(@Valid @RequestBody String originalRefreshToken) {
		// Needs to create a custom payload apparently to support this from working
		// fine.
		String refreshToken = originalRefreshToken.replace("\"", "");
		if (!jwtUtil.isTokenValid(refreshToken, "refresh")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return ResponseEntity
				.ok(new TokenResponse(jwtUtil.getNewAccessTokenFromRefreshToken(refreshToken), refreshToken));
	}
}
