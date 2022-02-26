package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest  // loads whole SB context
@AutoConfigureMockMvc
public class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepo; // bad practice, ideally call endpoint for allpayments and compare from there

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "Pepito",  "6667");
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        //
        ResultActions customerRegResultActions = mockMvc.perform(put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(ObjectToJson(request)))
        );

        long paymentId = 1L;
        Payment payment = new Payment(
                paymentId,
                customerId,
                new BigDecimal("100.00"),
                Currency.USD,
                "card123",
                "a description"
        );
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        ResultActions paymentResultActions = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(ObjectToJson(paymentRequest)))
        );

        //Then
        // test registration
        customerRegResultActions.andExpect(status().isOk());
        // test payment
        paymentResultActions.andExpect(status().isOk());
        // compare against DB
        // TODO: this assertion is to be done by other enpoint like .../payments/
        assertThat(paymentRepo.findById(paymentId)).isPresent().hasValueSatisfying(  //Asserting POST like methods
                p -> assertThat(p).isEqualToComparingFieldByField(payment)
        );

        // TODO: Ensure sms is delivered
    }

    private String ObjectToJson(Object request) {
        try {
            return new ObjectMapper().writeValueAsString(request);
        } catch (Exception e) {
            fail("Json object conversion not successfully: " + e.getMessage());
            return null;
        }
    }
}
