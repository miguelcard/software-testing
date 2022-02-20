package com.amigoscode.testing.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest( // drops all tables before if exists and creates H2 DB and at end drops table again -> clean DB for tests
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"  //Entity annotations will be treated when we run unit tests (e.g. NN constraints, unique constraints)
        }
)
class PaymentRepositoryTest {
    
    @Autowired
    private PaymentRepository paymentRepoUnderTest;

    @Test
    void itShouldInsertPayment() {
        // Given a payment
        Long id = 1L;
        Payment payment = new Payment(id, UUID.randomUUID(), new BigDecimal(100.00), Currency.USD, "card 123", "payment to adidas");
        // When saved
        paymentRepoUnderTest.save(payment);
        // Then verify is saved
        Optional<Payment> retrievedPaymentOptional = paymentRepoUnderTest.findById(id);
        assertThat(retrievedPaymentOptional).isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualTo(payment));
    }
}