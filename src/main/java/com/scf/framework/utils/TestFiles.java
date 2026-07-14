package com.scf.framework.utils;

import com.microsoft.playwright.options.FilePayload;

import java.util.Base64;

/** In-memory files for upload fields — no fixtures on disk needed. */
public final class TestFiles {

    // Valid 1x1 PNG
    private static final byte[] TINY_PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==");

    private TestFiles() {
    }

    public static FilePayload dummyPng(String fileName) {
        return new FilePayload(fileName, "image/png", TINY_PNG);
    }
}
