package com.scf.framework.utils;

import com.scf.framework.models.Anchor;
import com.scf.framework.models.Bank;
import com.scf.framework.models.Dealer;
import com.scf.framework.models.LoanRequest;
import com.scf.framework.models.OfferLetter;
import net.datafaker.Faker;

import java.math.BigDecimal;

/**
 * Builds unique, re-runnable entities. Every name/registration number carries
 * a random suffix so a test can run repeatedly against the same environment
 * without tripping duplicate checks (and duplicate-validation tests can
 * deliberately reuse a value).
 */
public final class TestDataFactory {

    private static final Faker FAKER = new Faker();

    private TestDataFactory() {
    }

    private static String suffix() {
        return String.valueOf(System.currentTimeMillis() % 1_000_000);
    }

    public static Bank bank() {
        String tag = suffix();
        return new Bank(
                "QA Bank " + tag,
                "QABK" + tag,
                "qa.bank." + tag + "@scf.test",
                FAKER.phoneNumber().cellPhone());
    }

    public static Anchor anchor() {
        String tag = suffix();
        // yopmail inbox so automation can later read the operator's first-time credentials email
        return new Anchor(
                "QA Anchor " + tag,
                "qa.anchor." + tag + "@yopmail.com",
                "07" + String.format("%08d", Long.parseLong(tag) * 37 % 100_000_000),
                "1" + tag + "00");
    }

    public static Dealer dealer() {
        String tag = suffix();
        return new Dealer(
                "QA Dealer " + tag,
                "DLR-" + tag,
                "qa.dealer." + tag + "@scf.test",
                FAKER.phoneNumber().cellPhone());
    }

    public static OfferLetter offerLetter(String dealerName) {
        return new OfferLetter(dealerName, new BigDecimal("12.5"), new BigDecimal("1000000.00"), 90);
    }

    public static LoanRequest loanRequest(String dealerName) {
        String tag = suffix();
        return new LoanRequest(dealerName, new BigDecimal("250000.00"),
                "Stock purchase from anchor", "INV-" + tag);
    }
}
