package com.scf.framework.models;

/**
 * A supplier/distributor of the Anchor, recommended by the Anchor (Maker -> Checker).
 * Fields mirror the real "Recommend Dealer" form: dealer name, ERP code,
 * email address, contact number.
 */
public record Dealer(String name, String erpCode, String email, String contactNumber) {
}
