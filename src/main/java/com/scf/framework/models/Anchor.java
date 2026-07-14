package com.scf.framework.models;

/** A large buyer/corporate onboarded by the Bank (Maker -> Checker). */
public record Anchor(String name, String registrationNumber, String contactEmail, String contactPhone) {
}
