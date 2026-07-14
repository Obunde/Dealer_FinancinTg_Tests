package com.scf.pages.anchor.maker;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.framework.models.Dealer;
import com.scf.pages.BasePage;

/**
 * Anchor Maker — Dealers -> Recommend Dealer (locators verified via codegen
 * against https://dealerfinance.emtechhouse.co.ke, 2026-07-14).
 */
public class DealerInvitationPage extends BasePage {

    public DealerInvitationPage(Page page) {
        super(page);
    }

    /** Left-nav "Dealers" button (label carries a material icon, e.g. "group Dealers"). */
    public void openDealersList() {
        page.locator("button.side-buttons", new Page.LocatorOptions().setHasText("Dealers")).click();
    }

    public void open() {
        openDealersList();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Recommend Dealer")).click();
    }

    public void fillDealerDetails(Dealer dealer) {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Dealer Name")).fill(dealer.name());
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Dealer ERP Code")).fill(dealer.erpCode());
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email Address")).fill(dealer.email());
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Contact Number")).fill(dealer.contactNumber());
    }

    /**
     * Submits the Recommend Dealer form via "Save Changes". Fails fast with a
     * clear message if the button never enables (form invalid / possible defect)
     * rather than a 30s timeout.
     */
    public void submit() {
        var save = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save Changes"));
        save.waitFor();
        if (!save.isEnabled()) {
            // KNOWN DEFECT (2026-07-14): with all fields validly filled, the form
            // raises an "Error: Something went wrong" toast and Save Changes stays
            // disabled, so a dealer cannot be recommended. Reproduces manually.
            String toast = readErrorToast();
            throw new AssertionError("'Save Changes' is disabled on the Recommend Dealer form even "
                    + "though all fields are filled" + (toast.isEmpty() ? "" : " — app shows: \"" + toast + "\"")
                    + ". Dealer recommendation is blocked (known defect, 2026-07-14).");
        }
        save.click();
    }

    /** Best-effort read of a visible error/toast; empty string if none appears quickly. */
    private String readErrorToast() {
        try {
            Locator toast = page.locator("[role='alert'], .toast, .snackbar, mat-snack-bar-container").first();
            toast.waitFor(new Locator.WaitForOptions().setTimeout(2000));
            return toast.textContent().trim().replaceAll("\\s+", " ");
        } catch (RuntimeException e) {
            return "";
        }
    }

    public void inviteDealer(Dealer dealer) {
        open();
        fillDealerDetails(dealer);
        submit();
    }

    public boolean isDealerListed(String dealerName) {
        openDealersList();
        return page.locator("tr", new Page.LocatorOptions().setHasText(dealerName)).count() > 0;
    }
}
