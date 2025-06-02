package ar.edu.utn.frba.inventariobackend.controller;

import ar.edu.utn.frba.inventariobackend.dto.request.AddUserRequest;
import ar.edu.utn.frba.inventariobackend.dto.response.GetUserResponse;
import ar.edu.utn.frba.inventariobackend.model.User;
import ar.edu.utn.frba.inventariobackend.service.UserService;
import ar.edu.utn.frba.inventariobackend.utils.TokenUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing user-related operations.
 * Provides endpoints for registering and retrieving user information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final TokenUtils tokenUtils;
    private final UserService userService;

    /**
     * Registers a new user with the provided data.
     *
     * @param addUserRequest the request containing user details such as username, password, name, and optional image
     * @return a {@link ResponseEntity} with HTTP 201 Created status if the user was successfully added
     */
    @PostMapping()
    public ResponseEntity<?> addUser(@Valid @RequestBody AddUserRequest addUserRequest) {
        userService.addUser(addUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retrieves the currently authenticated user's information.
     *
     * @return the {@link User} object of the authenticated user
     * @throws ResponseStatusException if the user is not found (HTTP 404 Not Found)
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/self")
    public GetUserResponse getSelf() {
        String username = tokenUtils.getUsernameFromToken();
        return userService.findFullByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
