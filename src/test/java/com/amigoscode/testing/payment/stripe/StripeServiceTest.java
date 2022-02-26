package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.CardCharge;
import com.amigoscode.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StripeServiceTest {

    private StripeService stripeServiceUnderTest;

    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        stripeServiceUnderTest = new StripeService(stripeApi);
    }

    @Test
    void itShouldChargeCard() throws StripeException {
        // Given
        BigDecimal amount = new BigDecimal(100.00);
        Currency currency = Currency.USD;
        String source = "card123";
        String description = "amazon books";

        Charge stripeCharge = new Charge();
        stripeCharge.setPaid(true);

        given(stripeApi.create(any(), any())).willReturn(stripeCharge);

        // When
        // Never do network call in unit testing, just trust ext. API & MOCK call
        CardCharge charge = stripeServiceUnderTest.chargeCard(amount, currency, source, description); // calls real Stripe API
        // PROBLEM: how to mock static methods,
        // OPTIONS:
        // 1. Framework to mock static methods (powermock),
        // 2. Create class with non static method to mock the other e.g. StripeApi
        // and just call static method from there

        // Then
        // missing assertions, test that method stripeApi.create(arg, arg) takes right args
        ArgumentCaptor<Map<String, Object>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> reqOptionsCaptor = ArgumentCaptor.forClass(RequestOptions.class);

        then(stripeApi).should().create(mapCaptor.capture(), reqOptionsCaptor.capture());

        Map<String, Object> reqMap = mapCaptor.getValue();
        assertThat(reqMap.get("amount")).isEqualTo(amount);
        assertThat(reqMap.get("currency")).isEqualTo(currency);
        assertThat(reqMap.get("source")).isEqualTo(source);
        assertThat(reqMap.get("description")).isEqualTo(description);
        assertThat(reqMap.size()).isEqualTo(4);

        assertThat(reqOptionsCaptor.getValue()).isNotNull();
         // and what i first though
        assertThat(charge.isWasCharged()).isEqualTo(true);
    }

    @Test
    void itShouldThrowExceptionIfNotCharged() throws StripeException {
        // Given
        // Given
        BigDecimal amount = new BigDecimal(100.00);
        Currency currency = Currency.USD;
        String source = "card123";
        String description = "amazon books";

        StripeException ex = mock(StripeException.class); // (i) Mocks exception interface!
        given(stripeApi.create(any(), any())).willThrow(ex);
        // When
        // Then
        assertThatThrownBy(() -> stripeServiceUnderTest.chargeCard(amount, currency, source, description))
                .hasRootCause(ex)
                .isInstanceOf(IllegalStateException.class);
    }
}