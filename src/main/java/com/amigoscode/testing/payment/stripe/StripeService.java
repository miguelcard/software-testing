package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.CardCharge;
import com.amigoscode.testing.payment.CardPaymentCharger;
import com.amigoscode.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@ConditionalOnProperty(value = "stripe.active", havingValue = "true")
public class StripeService implements CardPaymentCharger {

    private static final RequestOptions REQUEST_OPTIONS = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc") // should go in props file
            .build();

    private final StripeApi stripeApi;

    @Autowired
    public StripeService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    @Override
    public CardCharge chargeCard(BigDecimal amount, Currency currencyString, String source, String description) {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currencyString);
        params.put("source", source);
        params.put("description", description);

        try {
            Charge charge = stripeApi.create(params, REQUEST_OPTIONS);  // calls the real Stripe API
            return new CardCharge(charge.getPaid());
        } catch (StripeException e) {
            throw new IllegalStateException(" Error creating Stripe charge", e);
        }
    }
}
