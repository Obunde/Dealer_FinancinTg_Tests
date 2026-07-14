package com.scf.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Shared behaviour for every Checker's "Pending Approvals" screen.
 * Bank, Anchor and Dealer checkers all review a queue of maker-initiated
 * requests and approve or reject each with a comment.
 */
public abstract class ApprovalQueuePage extends BasePage {

    protected ApprovalQueuePage(Page page) {
        super(page);
    }

    public void openPendingApprovals() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Pending Approvals")).click();
    }

    /** Opens the pending record matching the reference (entity name, invoice no, ...). */
    public void openRequest(String reference) {
        row(reference).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("View")).click();
    }

    public void approve(String reference, String comment) {
        openRequest(reference);
        fillField("Comment", comment);
        clickButton("Approve");
    }

    public void reject(String reference, String reason) {
        openRequest(reference);
        fillField("Comment", reason);
        clickButton("Reject");
    }

    public boolean isRequestListed(String reference) {
        return row(reference).count() > 0;
    }

    /** True when the record was created by the logged-in checker themself — approval must be blocked. */
    public boolean isApproveDisabled(String reference) {
        openRequest(reference);
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Approve")).isDisabled();
    }

    private Locator row(String reference) {
        return page.locator("tr", new Page.LocatorOptions().setHasText(reference));
    }
}
