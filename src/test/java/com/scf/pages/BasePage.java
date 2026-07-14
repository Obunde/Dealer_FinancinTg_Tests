package com.scf.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Common behaviour for all page objects.
 *
 * Locator strategy: prefer user-facing locators (getByRole, getByLabel,
 * getByText) and fall back to data-testid. The selectors in the concrete page
 * objects are best-guess placeholders — verify each against the real DOM with
 * `mvn exec:java -Dexec.args="codegen <url>"` and update as needed.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    protected void clickButton(String name) {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name)).click();
    }

    protected void fillField(String label, String value) {
        page.getByLabel(label).fill(value);
    }

    /** The global toast/snackbar used for success and error notifications. */
    public String getToastMessage() {
        Locator toast = page.locator("[role='alert'], .toast, .snackbar").first();
        toast.waitFor();
        return toast.textContent().trim();
    }

    /** Inline validation message attached to a specific form field. */
    public String getFieldError(String fieldLabel) {
        // TODO: adjust once the real DOM structure of field errors is known.
        Locator error = page.locator(
                String.format("//label[contains(., '%s')]/ancestor::*[contains(@class,'form-group')]" +
                        "//*[contains(@class,'error') or contains(@class,'invalid-feedback')]", fieldLabel));
        error.first().waitFor();
        return error.first().textContent().trim();
    }

    /** Status badge/text of a record row identified by a unique reference (name, invoice no...). */
    public String getRecordStatus(String reference) {
        return page.locator("tr", new Page.LocatorOptions().setHasText(reference))
                .locator("[data-testid='status'], .status-badge").first()
                .textContent().trim();
    }
}
