package com.creditcard.paymentapi.service;

import com.creditcard.paymentapi.model.PaymentStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        // Initialize with known test values
        // confirmed: "CONF-1" and "CONF-2"
        // rejected: "REJ-1"
        String confirmedRefs = "CONF-1,CONF-2";
        String rejectedRefs = "REJ-1";
        paymentService = new PaymentService(confirmedRefs, rejectedRefs);
    }

    @Test
    void getPaymentStatus_WhenReferenceIsConfirmed_ReturnsConfirmedStatus() {
        PaymentStatusResponse response = paymentService.getPaymentStatus("CONF-1");
        assertNotNull(response);
        assertEquals(PaymentStatusResponse.StatusEnum.CONFIRMED, response.getStatus());
        assertNotNull(response.getLastUpdateDate());
    }

    @Test
    void getPaymentStatus_WhenSecondConfirmedReference_ReturnsConfirmedStatus() {
        PaymentStatusResponse response = paymentService.getPaymentStatus("CONF-2");
        assertNotNull(response);
        assertEquals(PaymentStatusResponse.StatusEnum.CONFIRMED, response.getStatus());
    }

    @Test
    void getPaymentStatus_WhenReferenceIsRejected_ReturnsRejectedStatus() {
        PaymentStatusResponse response = paymentService.getPaymentStatus("REJ-1");
        assertNotNull(response);
        assertEquals(PaymentStatusResponse.StatusEnum.REJECTED, response.getStatus());
        assertNotNull(response.getLastUpdateDate());
    }

    @Test
    void getPaymentStatus_WhenReferenceIsUnknown_ThrowsPaymentNotFoundException() {
        assertThrows(PaymentService.PaymentNotFoundException.class, () -> {
            paymentService.getPaymentStatus("UNKNOWN-123");
        });
    }

    @Test
    void getPaymentStatus_WhenReferenceEmpty_ThrowsPaymentNotFoundException() {
        // Since empty string is not in our confirmed or rejected lists (unless
        // configured as such),
        // it should throw exception for this specific configuration
        assertThrows(PaymentService.PaymentNotFoundException.class, () -> {
            paymentService.getPaymentStatus("");
        });
    }
}
