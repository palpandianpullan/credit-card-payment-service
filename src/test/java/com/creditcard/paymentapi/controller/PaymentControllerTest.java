package com.creditcard.paymentapi.controller;

import com.creditcard.paymentapi.model.PaymentStatusResponse;
import com.creditcard.paymentapi.model.PaymentStatusRetrievalRequest;
import com.creditcard.paymentapi.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void paymentStatusPost_WhenServiceReturnsResponse_ReturnsOk() throws Exception {
        PaymentStatusResponse mockResponse = new PaymentStatusResponse();
        mockResponse.setStatus(PaymentStatusResponse.StatusEnum.CONFIRMED);

        when(paymentService.getPaymentStatus(anyString())).thenReturn(mockResponse);

        PaymentStatusRetrievalRequest request = new PaymentStatusRetrievalRequest();
        request.setPaymentReference("REF123");

        mockMvc.perform(post("/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    public void paymentStatusPost_WhenServiceThrowsNotFound_ReturnsNotFound() throws Exception {
        when(paymentService.getPaymentStatus(anyString()))
                .thenThrow(new PaymentService.PaymentNotFoundException("Not found"));

        PaymentStatusRetrievalRequest request = new PaymentStatusRetrievalRequest();
        request.setPaymentReference("REF123");

        mockMvc.perform(post("/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Payment not found"));
    }

    @Test
    public void paymentStatusPost_WhenServiceThrowsGenericException_ReturnsInternalServerError() throws Exception {
        when(paymentService.getPaymentStatus(anyString()))
                .thenThrow(new RuntimeException("Database error"));

        PaymentStatusRetrievalRequest request = new PaymentStatusRetrievalRequest();
        request.setPaymentReference("REF123");

        mockMvc.perform(post("/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }

    @Test
    public void paymentStatusPost_WhenRequestIsInvalid_ReturnsBadRequest() throws Exception {
        PaymentStatusRetrievalRequest request = new PaymentStatusRetrievalRequest();
        request.setPaymentReference(null);

        mockMvc.perform(post("/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input: paymentReference - must not be null; "));
    }

}
