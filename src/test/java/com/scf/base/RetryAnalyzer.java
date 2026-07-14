package com.scf.base;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Retries a failed test once to absorb environment flakiness. Tune MAX_RETRIES as the suite matures. */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 1;
    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        return attempt++ < MAX_RETRIES;
    }
}
