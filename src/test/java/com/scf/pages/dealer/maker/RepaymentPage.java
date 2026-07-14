package com.scf.pages.dealer.maker;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.pages.BasePage;

/**
 * Dealer Maker — repay an outstanding loan (Product Guide §Repayment).
 * On checker approval the revolving limit is restored by the repaid amount.
 */
public class RepaymentPage extends BasePage {

    public RepaymentPage(Page page) {
        super(page);
    }

    public void open() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Repayments")).click();
    }

    public void initiateRepayment(String invoiceReference, String amount) {
        open();
        page.locator("tr", new Page.LocatorOptions().setHasText(invoiceReference))
                .getByRole(AriaRole.BUTTON,
                        new com.microsoft.playwright.Locator.GetByRoleOptions().setName("Repay"))
                .click();
        fillField("Repayment Amount", amount);
        clickButton("Submit for Approval");
    }
}
