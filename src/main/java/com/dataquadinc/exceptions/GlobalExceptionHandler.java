package com.dataquadinc.exceptions;

import com.dataquadinc.dto.ErrorResponseBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Helper method to build ErrorResponseBean
    private <T> ResponseEntity<ErrorResponseBean<T>> buildErrorResponse(boolean success, String message, T data, Map<String, String> errorDetails, HttpStatus status) {
        // Using Lombok's builder method directly
        ErrorResponseBean<T> errorResponse = new  ErrorResponseBean.Builder<T>()
                .success(success)
                .message(message)
                .data(data)
                .error(errorDetails)
                .build();

        // Return the ResponseEntity with appropriate status
        return new ResponseEntity<>(errorResponse, status);
    }

    // Handle custom ValidationException
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseBean<Map<String, String>>> handleValidationException(ValidationException ex) {
        // Combining errors from ValidationException into a single message
        String errorMessage = String.join(", ", ex.getErrors().values());

        // Build error details with error code and message
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("errorcode", "300");
        errorDetails.put("errormessage", errorMessage);

        // Return a response with a BAD_REQUEST status
        return buildErrorResponse(false, "Validation failed", null, errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handle MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBean<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Collecting all field validation errors
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = "Invalid value for field: " + fieldName + ". Please check and provide the correct information.";
            errors.put(fieldName, message);
        });

        // Returning a BAD_REQUEST status with the validation errors
        return buildErrorResponse(false, "Validation error", errors, null, HttpStatus.BAD_REQUEST);
    }

    // Handle custom InvalidUserException
    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorResponseBean<Map<String, String>>> handleInvalidUserException(InvalidUserException ex) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("userId", ex.getMessage()); // Accessing the message from InvalidUserException

        return buildErrorResponse(false, "User not found", null, errorDetails, HttpStatus.NOT_FOUND);
    }
}
