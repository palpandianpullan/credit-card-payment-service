package com.creditcard.paymentapi.service;

import com.creditcard.paymentapi.model.PaymentStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Service to handle payment status logic
 * This is a mock implementation that returns status based on configured payment
 * references
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final Set<String> confirmedPayments;
    private final Set<String> rejectedPayments;

    public PaymentService(
            @Value("${payment.mock.confirmed:DL123456789}") String confirmedRefs,
            @Value("${payment.mock.rejected:}") String rejectedRefs) {

        this.confirmedPayments = new HashSet<>(Arrays.asList(confirmedRefs.split(",")));
        this.rejectedPayments = new HashSet<>(Arrays.asList(rejectedRefs.split(",")));

        logger.info("Payment Service initialized");
        logger.info("Confirmed payment references: {}", confirmedPayments);
        logger.info("Rejected payment references: {}", rejectedPayments);
    }

    /**
     * Get payment status for a given payment reference
     * 
     * @param paymentReference The payment reference to check
     * @return PaymentStatusResponse with status
     * @throws PaymentNotFoundException if payment reference is not found
     */
    public PaymentStatusResponse getPaymentStatus(String paymentReference) {
        logger.debug("Checking payment status for reference: {}", paymentReference);

        PaymentStatusResponse response = new PaymentStatusResponse();
        response.setLastUpdateDate(java.time.OffsetDateTime.now());

        if (confirmedPayments.contains(paymentReference)) {
            logger.info("Payment {} is CONFIRMED", paymentReference);
            response.setStatus(PaymentStatusResponse.StatusEnum.CONFIRMED);
            return response;
        } else if (rejectedPayments.contains(paymentReference)) {
            logger.info("Payment {} is REJECTED", paymentReference);
            response.setStatus(PaymentStatusResponse.StatusEnum.REJECTED);
            return response;
        } else {
            logger.warn("Payment reference {} not found", paymentReference);
            throw new PaymentNotFoundException("Payment not found for reference: " + paymentReference);
        }
    }

    /**
     * Exception thrown when payment is not found
     */
    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(String message) {
            super(message);
        }
    }
}
