package com.amigoscode.testing.payment;

public class CardCharge {

    private final boolean wasCharged;

    public CardCharge(boolean wasCharged) {
        this.wasCharged = wasCharged;
    }

    public boolean isWasCharged() {
        return wasCharged;
    }

    @Override
    public String toString() {
        return "CardCharge{" +
                "wasCharged=" + wasCharged +
                '}';
    }
}
