package com.scf.framework.models;

import java.math.BigDecimal;

/**
 * Offer letter sent by the Bank to a Dealer: interest rate, revolving
 * borrowing limit and tenor. Becomes active once the Dealer (Maker -> Checker)
 * approves it.
 */
public record OfferLetter(String dealerName, BigDecimal interestRate, BigDecimal borrowingLimit, int tenorDays) {
}
