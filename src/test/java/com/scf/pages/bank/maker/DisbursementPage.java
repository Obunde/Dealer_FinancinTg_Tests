package com.scf.pages.bank.maker;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.pages.BasePage;

/**
 * Bank Maker — disburse an approved dealer loan to the Anchor
 * (Product Guide §Disbursement). Only loans whose request was
 * checker-approved on the dealer side appear here.
 */
public class DisbursementPage extends BasePage {

    public DisbursementPage(Page page) {
        super(page);
    }

    public void open() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Disbursements")).click();
    }

    public void initiateDisbursement(String invoiceReference) {
        open();
        page.locator("tr", new Page.LocatorOptions().setHasText(invoiceReference))
                .getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                        new com.microsoft.playwright.Locator.GetByRoleOptions().setName("Disburse"))
                .click();
        clickButton("Submit for Approval");
    }

    public boolean isLoanAvailableForDisbursement(String invoiceReference) {
        open();
        return page.locator("tr", new Page.LocatorOptions().setHasText(invoiceReference)).count() > 0;
    }
}
