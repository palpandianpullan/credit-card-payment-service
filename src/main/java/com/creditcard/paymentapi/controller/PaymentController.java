package com.creditcard.paymentapi.controller;

import com.creditcard.paymentapi.api.PaymentStatusApi;
import com.creditcard.paymentapi.model.ErrorResponse;
import com.creditcard.paymentapi.model.PaymentStatusRetrievalRequest;
import com.creditcard.paymentapi.model.PaymentStatusResponse;
import com.creditcard.paymentapi.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Payment Status API
 * Implements the endpoint generated from creditcardpayment_api.yaml
 */
@RestController
public class PaymentController implements PaymentStatusApi {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * POST /payment-status
     * Retrieve payment status for a given payment reference
     * 
     * @param request Payment status request containing payment reference
     * @return Payment status response
     */
    @Override
    public ResponseEntity<PaymentStatusResponse> paymentStatusPost(
            @Valid @RequestBody PaymentStatusRetrievalRequest request) {

        logger.info("Received payment status request: {}", request);

        try {
            PaymentStatusResponse response = paymentService.getPaymentStatus(request.getPaymentReference());
            logger.info("Returning payment status: {}", response);
            return ResponseEntity.ok(response);
        } catch (PaymentService.PaymentNotFoundException e) {
            logger.error("Payment not found: {}", e.getMessage());
            throw e; // Will be handled by exception handler
        } catch (Exception e) {
            logger.error("Unexpected error processing payment status: {}", e.getMessage(), e);
            throw new RuntimeException("Internal server error", e);
        }
    }

    /**
     * Exception handler for PaymentNotFoundException
     */
    @ExceptionHandler(PaymentService.PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFound(PaymentService.PaymentNotFoundException ex) {
        logger.warn("Payment not found exception: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Payment not found");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Invalid input: ");
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessage.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }
        logger.warn("Validation error: {}", errorMessage);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(errorMessage.toString());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Exception handler for JSON parse errors
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("JSON parse error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Invalid JSON format: " + ex.getMostSpecificCause().getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Exception handler for general errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Internal server error: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Internal server error");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
