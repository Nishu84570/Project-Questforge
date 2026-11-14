
package questforge.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationErrors_ShouldReturnBadRequestWithFieldErrors() {

        var bindingResult = mock(
                org.springframework.validation.BindingResult.class
        );

        var exception = mock(MethodArgumentNotValidException.class);

        List<FieldError> fieldErrors = List.of(
                new FieldError("user", "email", "Email is required"),
                new FieldError("user", "password", "Password is required")
        );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<Map<String, Object>> response =
                handler.handleValidationErrors(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(400, response.getBody().get("status"));
        assertEquals("Validation failed", response.getBody().get("message"));

        Map<String, String> errors =
                (Map<String, String>) response.getBody().get("errors");

        assertEquals(2, errors.size());
        assertEquals("Email is required", errors.get("email"));
        assertEquals("Password is required", errors.get("password"));
    }

    @Test
    void handleValidationErrors_ShouldUseFallbackMessageWhenDefaultMessageIsNull() {

        var bindingResult = mock(
                org.springframework.validation.BindingResult.class
        );

        var exception = mock(MethodArgumentNotValidException.class);

        List<FieldError> fieldErrors = List.of(
                new FieldError("user", "email", null)
        );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<Map<String, Object>> response =
                handler.handleValidationErrors(exception);

        Map<String, String> errors =
                (Map<String, String>) response.getBody().get("errors");

        assertEquals("Invalid value", errors.get("email"));
    }

    @Test
    void handleValidationErrors_ShouldKeepFirstErrorForDuplicateField() {

        var bindingResult = mock(
                org.springframework.validation.BindingResult.class
        );

        var exception = mock(MethodArgumentNotValidException.class);

        List<FieldError> fieldErrors = List.of(
                new FieldError("user", "email", "Email is invalid"),
                new FieldError("user", "email", "Email is required")
        );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<Map<String, Object>> response =
                handler.handleValidationErrors(exception);

        Map<String, String> errors =
                (Map<String, String>) response.getBody().get("errors");

        assertEquals(1, errors.size());
        assertEquals("Email is invalid", errors.get("email"));
    }

    @Test
    void handleMalformedRequest_ShouldReturnBadRequest() {

        HttpMessageNotReadableException exception =
                mock(HttpMessageNotReadableException.class);

        ResponseEntity<Map<String, Object>> response =
                handler.handleMalformedRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals(
                "Request body is missing or malformed",
                response.getBody().get("message")
        );
    }

    @Test
    void handleResourceNotFound_ShouldReturnNotFound() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException("User not found");

        ResponseEntity<Map<String, Object>> response =
                handler.handleResourceNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("User not found", response.getBody().get("message"));
    }

    @Test
    void handleIllegalArgument_ShouldReturnBadRequest() {

        IllegalArgumentException exception =
                new IllegalArgumentException("Invalid user ID");

        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalArgument(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Invalid user ID", response.getBody().get("message"));
    }

    @Test
    void handleIllegalState_ShouldReturnConflict() {

        IllegalStateException exception =
                new IllegalStateException("User is already registered");

        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalState(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals(
                "User is already registered",
                response.getBody().get("message")
        );
    }

    @Test
    void handleAuthenticationException_ShouldReturnUnauthorized() {

        AuthenticationException exception =
                mock(AuthenticationException.class);

        ResponseEntity<Map<String, Object>> response =
                handler.handleAuthenticationException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().get("status"));
        assertEquals(
                "Invalid email or password",
                response.getBody().get("message")
        );
    }

    @Test
    void handleAccessDenied_ShouldReturnForbidden() {

        AccessDeniedException exception =
                new AccessDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response =
                handler.handleAccessDenied(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().get("status"));
        assertEquals(
                "You do not have permission to perform this action",
                response.getBody().get("message")
        );
    }
}
