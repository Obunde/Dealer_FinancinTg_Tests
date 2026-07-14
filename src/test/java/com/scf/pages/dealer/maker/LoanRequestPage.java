package com.scf.pages.dealer.maker;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.framework.models.LoanRequest;
import com.scf.pages.BasePage;

/**
 * Dealer Maker — request a loan against the revolving limit to pay the Anchor
 * (Product Guide §Loan Request). On checker approval the available limit
 * reduces by the loan amount.
 */
public class LoanRequestPage extends BasePage {

    public LoanRequestPage(Page page) {
        super(page);
    }

    public void open() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Loans")).click();
        clickButton("New Loan Request");
    }

    public void fillLoanRequest(LoanRequest loan) {
        fillField("Amount", loan.amount().toPlainString());
        fillField("Invoice Reference", loan.invoiceReference());
        fillField("Purpose", loan.purpose());
    }

    public void submit() {
        clickButton("Submit for Approval");
    }

    public void requestLoan(LoanRequest loan) {
        open();
        fillLoanRequest(loan);
        submit();
    }

    /** Available revolving limit as shown on the loans dashboard. */
    public String getAvailableLimit() {
        return page.locator("[data-testid='available-limit']").textContent().trim();
    }
}
