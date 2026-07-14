package com.scf.tests.anchor.checker;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.framework.models.Dealer;
import com.scf.framework.utils.TestDataFactory;
import com.scf.pages.anchor.checker.AnchorApprovalQueuePage;
import com.scf.pages.anchor.maker.DealerInvitationPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Positive approval/rejection flows for Anchor Checker. */
public class AnchorCheckerFlowTest extends BaseTest {

    @Test(groups = {"anchor", "checker", "flow"},
            description = "Checker approves a dealer invitation and the invite is sent out")
    public void shouldApproveDealerInvitation() {
        // Precondition: maker submits an invitation
        loginAs(Actor.ANCHOR, Role.MAKER);
        Dealer dealer = TestDataFactory.dealer();
        new DealerInvitationPage(PlaywrightFactory.getPage()).inviteDealer(dealer);

        // Checker approves
        var checkerPage = loginAs(Actor.ANCHOR, Role.CHECKER);
        AnchorApprovalQueuePage queue = new AnchorApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        queue.approve(dealer.name(), "Dealer relationship confirmed — approved by QA automation");

        Assert.assertTrue(queue.getToastMessage().contains("approved"));
        Assert.assertEquals(queue.getRecordStatus(dealer.name()), "Invited",
                "Invitation must move to Invited after checker approval");
    }

    @Test(groups = {"anchor", "checker", "flow"})
    public void shouldRejectDealerInvitationWithReason() {
        throw new SkipException("TODO: reject path — invitation returns to maker with reason, no email sent to dealer");
    }
}
