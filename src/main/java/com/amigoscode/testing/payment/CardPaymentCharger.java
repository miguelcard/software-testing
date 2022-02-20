package com.amigoscode.testing.payment;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

public interface CardPaymentCharger {

    CardCharge chargeCard(BigDecimal amount, Currency currencyString, String source, String description);
}
