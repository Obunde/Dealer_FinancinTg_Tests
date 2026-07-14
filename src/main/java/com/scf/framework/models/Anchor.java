package com.scf.framework.models;

/**
 * A corporate buyer onboarded by Faulu (Maker -> Checker).
 * Fields mirror the real "+ Add Anchor" form (#anchorDetailsForm):
 * anchor/business name, email, contact number, account number, plus
 * document uploads handled by the page object.
 */
public record Anchor(String name, String email, String contactNumber, String accountNumber) {
}
