package com.scf.framework.models;

/** A Financial Institution boarded onto the platform by the System Admin. */
public record Bank(String name, String swiftCode, String contactEmail, String contactPhone) {
}
