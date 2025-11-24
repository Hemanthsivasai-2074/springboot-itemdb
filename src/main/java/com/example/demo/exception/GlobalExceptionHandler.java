package com.example.demo.exception;

import com.example.demo.logging.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private LogService logService;

    
    // ✅ This is the helper method you're calling
    private Map<String, Object> buildResponse(HttpStatus status, String message, Object details, String suggestion) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "error");
        body.put("code", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("details", details);
        body.put("suggestion", suggestion);
        body.put("timestamp", LocalDateTime.now().toString());
        return body;
    }

    // ✅ This is your validation error handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        logService.warn("⚠️ Validation failed: " + fieldErrors);

        return ResponseEntity.badRequest().body(buildResponse(
            HttpStatus.BAD_REQUEST,
            "Some fields are invalid. Please correct and try again.",
            fieldErrors,
            "Ensure all required fields are filled with valid values."
        ));
    }







    // ✅ Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        logService.error("❌ Resource not found: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildResponse(
            HttpStatus.NOT_FOUND,
            "The item you're looking for doesn't exist.",
            ex.getMessage(),
            "Double-check the item ID or resource path."
        ));
    }

    // ✅ Invalid input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        logService.warn("⚠️ Invalid input: " + ex.getMessage());
        return ResponseEntity.badRequest().body(buildResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid input provided.",
            ex.getMessage(),
            "Review your request payload and ensure all values are correct."
        ));
    }

    // ✅ Unauthorized access
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurity(SecurityException ex) {
        logService.error("🔒 Security violation: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildResponse(
            HttpStatus.FORBIDDEN,
            "You are not authorized to perform this action.",
            ex.getMessage(),
            "Check your permissions or login credentials."
        ));
    }

    // ✅ Incorrect HTTP method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logService.warn("⚠️ Method not allowed: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(buildResponse(
            HttpStatus.METHOD_NOT_ALLOWED,
            "The HTTP method used is not supported for this endpoint.",
            "Method '" + ex.getMethod() + "' is not allowed here.",
            "Try changing the method to one of: " + Arrays.toString(ex.getSupportedMethods())
        ));
    }

    // ✅ Invalid endpoint
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFound(NoHandlerFoundException ex) {
        logService.warn("⚠️ Invalid endpoint: " + ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildResponse(
            HttpStatus.NOT_FOUND,
            "The endpoint you requested does not exist.",
            "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL(),
            "Please verify the URL and HTTP method."
        ));
    }

    // ✅ Generic runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        logService.error("❌ Unexpected error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Something went wrong. Please try again later.",
            ex.getMessage(),
            "If the issue persists, contact support."
        ));
    }

    // ✅ Catch-all fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        logService.error("❌ Unhandled exception: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred.",
            ex.getMessage(),
            "Please contact support or check your request."
        ));
    }
    
 
}