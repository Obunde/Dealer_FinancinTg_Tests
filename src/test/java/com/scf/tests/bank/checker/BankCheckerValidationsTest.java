package com.scf.tests.bank.checker;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.pages.bank.checker.BankApprovalQueuePage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Negative/authorization validations for Bank Checker (Maker-Checker segregation of duties). */
public class BankCheckerValidationsTest extends BaseTest {

    @Test(groups = {"bank", "checker", "validation"},
            description = "Checker portal exposes no create/initiate actions — checkers can only review")
    public void shouldNotAllowCheckerToInitiateNewRequests() {
        var page = loginAs(Actor.BANK, Role.CHECKER);
        new BankApprovalQueuePage(page).openPendingApprovals();

        Assert.assertFalse(page.getByText("Onboard Anchor").isVisible(),
                "Checker must not see the maker's 'Onboard Anchor' action");
        Assert.assertFalse(page.getByText("New Offer Letter").isVisible(),
                "Checker must not see the maker's 'New Offer Letter' action");
    }

    @Test(groups = {"bank", "checker", "validation"})
    public void shouldBlockCheckerFromApprovingOwnSubmission() {
        throw new SkipException("TODO: a user holding both roles cannot approve their own record (self-approval block)");
    }

    @Test(groups = {"bank", "checker", "validation"})
    public void shouldRequireCommentWhenRejecting() {
        throw new SkipException("TODO: reject without a comment/reason must be blocked with a field error");
    }

    @Test(groups = {"bank", "checker", "validation"})
    public void shouldNotShowOtherOrganizationsRequestsInQueue() {
        throw new SkipException("TODO: tenant isolation — anchor/dealer pending items never appear in bank queue");
    }
}
