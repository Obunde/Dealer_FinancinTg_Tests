package com.scf.framework.enums;

/** The four organizations/portals on the SCF platform. */
public enum Actor {
    SYSTEM_ADMIN("systemadmin"),
    BANK("bank"),
    ANCHOR("anchor"),
    DEALER("dealer");

    private final String configKey;

    Actor(String configKey) {
        this.configKey = configKey;
    }

    /** Prefix used in config.properties, e.g. "bank" -> bank.url, bank.maker.username. */
    public String configKey() {
        return configKey;
    }
}
