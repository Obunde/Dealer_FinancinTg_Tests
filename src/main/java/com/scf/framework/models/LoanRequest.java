package com.scf.framework.models;

import java.math.BigDecimal;

/**
 * Loan requested by the Dealer to pay the Anchor. On checker approval the
 * Dealer's revolving limit is reduced by {@code amount}; a later approved
 * repayment restores it.
 */
public record LoanRequest(String dealerName, BigDecimal amount, String purpose, String invoiceReference) {
}
