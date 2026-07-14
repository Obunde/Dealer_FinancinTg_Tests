package com.scf.framework.enums;

/**
 * Maker-Checker roles. Every business action is initiated by a MAKER and must
 * be approved/rejected by a CHECKER of the same organization before it takes
 * effect (Product Guide — Maker-Checker control model).
 */
public enum Role {
    MAKER("maker"),
    CHECKER("checker");

    private final String configKey;

    Role(String configKey) {
        this.configKey = configKey;
    }

    public String configKey() {
        return configKey;
    }
}
