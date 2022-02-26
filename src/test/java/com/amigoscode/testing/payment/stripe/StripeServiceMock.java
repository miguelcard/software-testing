package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.CardCharge;
import com.amigoscode.testing.payment.CardPaymentCharger;
import com.amigoscode.testing.payment.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(value = "stripe.active", havingValue = "false")
public class StripeServiceMock implements CardPaymentCharger {
    @Override
    public CardCharge chargeCard(BigDecimal amount, Currency currencyString, String source, String description) {
        return new CardCharge(true);
    }
}
