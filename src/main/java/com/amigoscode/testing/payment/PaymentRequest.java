package com.amigoscode.testing.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class PaymentRequest {

    private final Payment payment;

    public PaymentRequest(@JsonProperty("payment") Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "payment=" + payment +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentRequest that = (PaymentRequest) o;
        return Objects.equals(payment, that.payment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payment);
    }
}
