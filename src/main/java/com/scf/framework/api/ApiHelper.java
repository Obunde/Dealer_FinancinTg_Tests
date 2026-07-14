package com.scf.framework.api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.scf.framework.config.ConfigReader;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Thin API client for test preconditions and state verification that should
 * not rely purely on UI assertions — e.g. reading a dealer's revolving limit
 * before/after a loan approval.
 *
 * TODO: point the endpoints below at the platform's real API once the QA
 * environment's API docs are available; they are placeholders establishing
 * the pattern.
 */
public class ApiHelper implements AutoCloseable {

    private final Playwright playwright;
    private final APIRequestContext request;

    public ApiHelper() {
        this.playwright = Playwright.create();
        this.request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(ConfigReader.get("api.base.url"))
                .setExtraHTTPHeaders(Map.of(
                        "Authorization", "Bearer " + ConfigReader.get("api.token"),
                        "Accept", "application/json")));
    }

    /** Current available (revolving) limit for a dealer. */
    public BigDecimal getDealerAvailableLimit(String dealerRegistrationNumber) {
        APIResponse response = request.get("/dealers/" + dealerRegistrationNumber + "/limit");
        assertOk(response);
        // TODO: parse the actual JSON shape, e.g. {"availableLimit": "750000.00"}
        String body = response.text();
        String value = body.replaceAll(".*\"availableLimit\"\\s*:\\s*\"?([0-9.]+)\"?.*", "$1");
        return new BigDecimal(value);
    }

    /** Current status of a loan request (PENDING_APPROVAL, APPROVED, REJECTED, DISBURSED, REPAID...). */
    public String getLoanStatus(String loanReference) {
        APIResponse response = request.get("/loans/" + loanReference);
        assertOk(response);
        return response.text().replaceAll(".*\"status\"\\s*:\\s*\"([A-Z_]+)\".*", "$1");
    }

    private void assertOk(APIResponse response) {
        if (!response.ok()) {
            throw new AssertionError("API call failed: " + response.status() + " " + response.url()
                    + "\n" + response.text());
        }
    }

    @Override
    public void close() {
        request.dispose();
        playwright.close();
    }
}
