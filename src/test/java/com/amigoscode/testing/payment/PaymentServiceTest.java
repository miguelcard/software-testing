package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Autowired
    private PaymentService paymentServiceUnderTest;

    @Mock
    private PaymentRepository paymentRepo;
    @Mock
    private CustomerRepository customerRepo;
    @Mock
    private CardPaymentCharger paymentCharger;

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentServiceUnderTest = new PaymentService(paymentRepo, customerRepo, paymentCharger);
    }

    @Test
    void chargeCardShouldSavePayment() {
        // Given
        UUID customerId = UUID.randomUUID();
        Payment payment = new Payment(null,
                null,
                new BigDecimal(100),
                Currency.USD,
                "card 123",
                "a description"
        );

        PaymentRequest request = new PaymentRequest(payment);

        given(customerRepo.findById(customerId)).willReturn(Optional.of(mock(Customer.class))); //Just Mock customer! (i)

        given(paymentCharger.chargeCard(        //here we need to pass everything? (i)
                payment.getAmount(),
                payment.getCurrency(),
                payment.getSource(),
                payment.getDescription()
        )).willReturn(new CardCharge(true));

        // When
        paymentServiceUnderTest.chargeCard(request, customerId);

        // Then
//        then(paymentRepo).should().save(eq(payment)); // success, payment obj already has updated customerId (i)
        //or specify ignoring id
        then(paymentRepo).should().save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue()).isEqualToIgnoringGivenFields(payment, "customerId");
        assertThat(paymentCaptor.getValue().getCustomerId()).isEqualTo(customerId);

        // you can also define Argument captor in method:___________________________
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
    }

    @Test
    void itShouldThrowExceptionIfCardWasNotCharged() {
        // Given
        UUID customerId = UUID.randomUUID();
        Payment payment = new Payment(null,
                null,
                new BigDecimal(100),
                Currency.USD,
                "card 123",
                "a description"
        );

        PaymentRequest request = new PaymentRequest(payment);

        given(customerRepo.findById(any())).willReturn(Optional.of(mock(Customer.class))); // (i)

        given(paymentCharger.chargeCard(        // (i)
                payment.getAmount(),
                payment.getCurrency(),
                payment.getSource(),
                payment.getDescription()
        )).willReturn(new CardCharge(false));

        // When
        // Then
        assertThatThrownBy(() -> paymentServiceUnderTest.chargeCard(request, customerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Card was not charged!");
        then(paymentRepo).should(never()).save(any());
    }

    @Test
    void itShouldThrowExceptionAndNotChargeWhenCurrencyNotSupported() {
        // Given bla
        UUID customerId = UUID.randomUUID();
        Payment payment = new Payment(null,
                null,
                new BigDecimal(100),
                Currency.PESO,
                "card 123",
                "a description"
        );

        PaymentRequest request = new PaymentRequest(payment);

        given(customerRepo.findById(customerId)).willReturn(Optional.of(mock(Customer.class))); //Just Mock customer! (i)
        // When
        // Then
        assertThatThrownBy(() -> paymentServiceUnderTest.chargeCard(request, customerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Currency not supported [%s] ", payment.getCurrency().toString()));
        then(paymentCharger).shouldHaveNoInteractions();
        then(paymentRepo).shouldHaveNoInteractions();
    }

    @Test
    void itShouldThrowExceptionIfCustomerNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();

        given(customerRepo.findById(customerId)).willReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> paymentServiceUnderTest.chargeCard(new PaymentRequest(new Payment()), customerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("User with id [%s] not found!", customerId));
        then(paymentCharger).shouldHaveNoInteractions();
        then(paymentRepo).shouldHaveNoInteractions();
    }
}