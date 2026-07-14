package com.scf.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration lookup. Resolution order for a key such as "bank.maker.username":
 * 1. JVM system property        (-Dbank.maker.username=...)
 * 2. Shell environment variable (BANK_MAKER_USERNAME)
 * 3. .env file in project root  (BANK_MAKER_USERNAME=...) — gitignored, holds credentials
 * 4. config.properties          (non-secret defaults: browser, timeouts, URLs)
 */
public final class ConfigReader {

    private static final Properties PROPS = new Properties();
    private static final Map<String, String> DOT_ENV = new HashMap<>();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
        loadDotEnv(Paths.get(".env"));
    }

    private ConfigReader() {
    }

    private static void loadDotEnv(Path path) {
        if (!Files.exists(path)) {
            return; // .env is optional — CI supplies real env vars instead
        }
        try {
            List<String> lines = Files.readAllLines(path);
            for (String raw : lines) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int eq = line.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                if (value.length() >= 2 && (value.startsWith("\"") && value.endsWith("\"")
                        || value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                if (!value.isEmpty()) {
                    DOT_ENV.put(key, value);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read .env file", e);
        }
    }

    /** bank.maker.username -> BANK_MAKER_USERNAME */
    private static String toEnvKey(String key) {
        return key.replace('.', '_').toUpperCase(Locale.ROOT);
    }

    public static String get(String key) {
        String envKey = toEnvKey(key);
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(envKey);
        }
        if (value == null) {
            value = DOT_ENV.get(envKey);
        }
        if (value == null) {
            value = PROPS.getProperty(key);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing configuration key: " + key
                    + " (set it in .env as " + envKey + ", or in config.properties)");
        }
        return value.trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBool(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
