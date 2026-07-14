package com.scf.tests.dealer.checker;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.framework.models.LoanRequest;
import com.scf.framework.utils.TestDataFactory;
import com.scf.pages.dealer.checker.DealerApprovalQueuePage;
import com.scf.pages.dealer.maker.LoanRequestPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/** Positive approval/rejection flows for Dealer Checker, including limit assertions. */
public class DealerCheckerFlowTest extends BaseTest {

    @Test(groups = {"dealer", "checker", "flow"},
            description = "Checker approves a loan request and the revolving limit reduces by exactly the loan amount")
    public void shouldApproveLoanRequestAndReduceAvailableLimit() {
        // Maker submits a loan request; capture the limit before approval
        var makerPage = loginAs(Actor.DEALER, Role.MAKER);
        LoanRequestPage loanPage = new LoanRequestPage(makerPage);
        LoanRequest loan = TestDataFactory.loanRequest("self");
        BigDecimal limitBefore = parseMoney(loanPage.getAvailableLimit());
        loanPage.requestLoan(loan);

        // Checker approves
        var checkerPage = loginAs(Actor.DEALER, Role.CHECKER);
        DealerApprovalQueuePage queue = new DealerApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        queue.approve(loan.invoiceReference(), "Loan within limit — approved by QA automation");
        Assert.assertEquals(queue.getRecordStatus(loan.invoiceReference()), "Approved");

        // Limit must reduce by exactly the loan amount
        loginAs(Actor.DEALER, Role.MAKER);
        BigDecimal limitAfter = parseMoney(new LoanRequestPage(PlaywrightFactory.getPage()).getAvailableLimit());
        Assert.assertEquals(limitBefore.subtract(limitAfter), loan.amount(),
                "Available limit must reduce by exactly the approved loan amount");
    }

    @Test(groups = {"dealer", "checker", "flow"})
    public void shouldApproveRepaymentAndRestoreAvailableLimit() {
        throw new SkipException("TODO: repayment approval restores the limit by the repaid amount — Product Guide §Repayment");
    }

    @Test(groups = {"dealer", "checker", "flow"})
    public void shouldApproveInvitationAcceptanceCompletingDealerOnboarding() {
        throw new SkipException("TODO: invitation-acceptance approval — dealer org becomes Active on platform");
    }

    @Test(groups = {"dealer", "checker", "flow"})
    public void shouldRejectLoanRequestReturningItToMaker() {
        throw new SkipException("TODO: reject path — status Rejected with reason, limit unchanged");
    }

    private static BigDecimal parseMoney(String display) {
        return new BigDecimal(display.replaceAll("[^0-9.]", ""));
    }
}
