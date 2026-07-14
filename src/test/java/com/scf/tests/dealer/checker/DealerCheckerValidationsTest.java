package com.scf.tests.dealer.checker;

import com.scf.base.BaseTest;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.pages.dealer.checker.DealerApprovalQueuePage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Negative/authorization validations for Dealer Checker. */
public class DealerCheckerValidationsTest extends BaseTest {

    @Test(groups = {"dealer", "checker", "validation"},
            description = "Checker cannot initiate loan requests or repayments")
    public void shouldNotAllowCheckerToInitiateLoanOrRepayment() {
        var page = loginAs(Actor.DEALER, Role.CHECKER);
        new DealerApprovalQueuePage(page).openPendingApprovals();

        Assert.assertFalse(page.getByText("New Loan Request").isVisible(),
                "Checker must not see the maker's 'New Loan Request' action");
        Assert.assertFalse(page.getByText("Repay").isVisible(),
                "Checker must not see the maker's 'Repay' action");
    }

    @Test(groups = {"dealer", "checker", "validation"})
    public void shouldBlockCheckerFromApprovingOwnSubmission() {
        throw new SkipException("TODO: self-approval must be blocked (Maker-Checker segregation)");
    }

    @Test(groups = {"dealer", "checker", "validation"})
    public void shouldRequireReasonWhenRejectingLoanRequest() {
        throw new SkipException("TODO: reject without reason blocked with field error");
    }

    @Test(groups = {"dealer", "checker", "validation"})
    public void shouldNotSeeOtherDealersRequestsInQueue() {
        throw new SkipException("TODO: tenant isolation — another dealer org's pending items never visible");
    }
}
