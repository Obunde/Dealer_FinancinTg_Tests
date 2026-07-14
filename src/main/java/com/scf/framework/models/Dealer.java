package com.scf.framework.models;

/** A supplier/distributor of the Anchor, invited by the Anchor (Maker -> Checker). */
public record Dealer(String name, String registrationNumber, String contactEmail, String contactPhone) {
}
