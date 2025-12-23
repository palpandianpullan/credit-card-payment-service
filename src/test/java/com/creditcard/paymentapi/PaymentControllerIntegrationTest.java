package com.creditcard.paymentapi;

import com.creditcard.paymentapi.model.PaymentStatusRetrievalRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${payment.mock.confirmed:DL123456789}")
    private String confirmedReference;

    @Value("${payment.mock.rejected:REJECT001}")
    private String rejectedReference;

    @Test
    public void testGetPaymentStatus_Confirmed() throws Exception {
        PaymentStatusRetrievalRequest request = new PaymentStatusRetrievalRequest();
        request.setPaymentReference("DL123456789"); // One of the confirmed ones in application.properties

        mockMvc.perform(post("/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.lastUpdateDate").exists());
    }

    @Test
    public void testGetPaymentStatus_Rejected() throws Exception {
        PaymentStatusRetrievalRequest request = new PaymentStatusRetrievalRequest();
        request.setPaymentReference("REJECT001"); // One of the rejected ones in application.properties

        mockMvc.perform(post("/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")))
                .andExpect(jsonPath("$.lastUpdateDate").exists());
    }

    @Test
    public void testGetPaymentStatus_NotFound() throws Exception {
        PaymentStatusRetrievalRequest request = new PaymentStatusRetrievalRequest();
        request.setPaymentReference("NONEXISTENT");

        mockMvc.perform(post("/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Payment not found")));
    }

    @Test
    public void testGetPaymentStatus_BadRequest() throws Exception {
        PaymentStatusRetrievalRequest request = new PaymentStatusRetrievalRequest();
        request.setPaymentReference(""); // Blank reference should trigger validation error

        mockMvc.perform(post("/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Invalid input")));
    }
}
