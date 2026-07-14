package com.scf.tests.bank.checker;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.framework.models.Anchor;
import com.scf.framework.utils.TestDataFactory;
import com.scf.pages.bank.checker.BankApprovalQueuePage;
import com.scf.pages.bank.maker.AnchorOnboardingPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Positive approval/rejection flows for Bank Checker. */
public class BankCheckerFlowTest extends BaseTest {

    @Test(groups = {"bank", "checker", "flow"},
            description = "Checker approves a maker's anchor onboarding and the anchor becomes Active")
    public void shouldApproveAnchorOnboarding() {
        // Precondition: maker submits an anchor (same test, own browser context per login)
        loginAs(Actor.BANK, Role.MAKER);
        Anchor anchor = TestDataFactory.anchor();
        new AnchorOnboardingPage(PlaywrightFactory.getPage()).onboardAnchor(anchor);

        // Checker approves
        var checkerPage = loginAs(Actor.BANK, Role.CHECKER);
        BankApprovalQueuePage queue = new BankApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        Assert.assertTrue(queue.isRequestListed(anchor.name()),
                "Maker's submission must be visible in the checker queue");

        queue.approve(anchor.name(), "Verified anchor documents — approved by QA automation");

        Assert.assertTrue(queue.getToastMessage().contains("approved"));
        Assert.assertEquals(queue.getRecordStatus(anchor.name()), "Active",
                "Anchor must be Active after checker approval");
    }

    @Test(groups = {"bank", "checker", "flow"})
    public void shouldRejectAnchorOnboardingWithReasonAndReturnToMaker() {
        throw new SkipException("TODO: reject path — status Rejected, reason visible to maker, maker can edit/resubmit");
    }

    @Test(groups = {"bank", "checker", "flow"})
    public void shouldApproveOfferLetterAndNotifyDealer() {
        throw new SkipException("TODO: offer letter approval + dealer notification — Product Guide §Offer Letter");
    }

    @Test(groups = {"bank", "checker", "flow"})
    public void shouldApproveDisbursementAndMarkLoanDisbursed() {
        throw new SkipException("TODO: disbursement approval — loan status becomes Disbursed, anchor is paid");
    }
}
