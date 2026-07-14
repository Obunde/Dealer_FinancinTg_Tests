package com.scf.pages.dealer.maker;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.pages.BasePage;

/** Dealer Maker — review and approve the Bank's offer letter terms (Product Guide §Offer Letter). */
public class OfferAcceptancePage extends BasePage {

    public OfferAcceptancePage(Page page) {
        super(page);
    }

    public void open() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Offer Letters")).click();
    }

    public void acceptOffer(String bankName) {
        open();
        page.locator("tr", new Page.LocatorOptions().setHasText(bankName))
                .getByRole(AriaRole.BUTTON,
                        new com.microsoft.playwright.Locator.GetByRoleOptions().setName("Accept Terms"))
                .click();
        clickButton("Submit for Approval");
    }

    public String getBorrowingLimit() {
        return page.locator("[data-testid='borrowing-limit']").textContent().trim();
    }
}
