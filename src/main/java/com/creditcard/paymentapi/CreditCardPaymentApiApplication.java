package com.creditcard.paymentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application for Credit Card Payment API
 * This is a mock service that simulates a third-party payment gateway
 */
@SpringBootApplication
public class CreditCardPaymentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditCardPaymentApiApplication.class, args);
    }
}
