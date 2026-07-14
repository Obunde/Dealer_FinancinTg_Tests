package com.scf.tests.anchor.maker;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.framework.models.Dealer;
import com.scf.framework.utils.TestDataFactory;
import com.scf.pages.anchor.maker.DealerInvitationPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Positive flows for Anchor Maker actions (Product Guide §Dealer Invitation). */
public class AnchorMakerFlowTest extends BaseTest {

    @Test(groups = {"anchor", "maker", "flow"},
            description = "Maker invites a dealer and the invitation lands in Pending Approval")
    public void shouldSubmitDealerInvitationForApproval() {
        loginAs(Actor.ANCHOR, Role.MAKER);
        DealerInvitationPage invitation = new DealerInvitationPage(PlaywrightFactory.getPage());
        Dealer dealer = TestDataFactory.dealer();

        invitation.inviteDealer(dealer);

        Assert.assertTrue(invitation.getToastMessage().contains("submitted for approval"));
        Assert.assertEquals(invitation.getRecordStatus(dealer.name()), "Pending Approval");
    }

    @Test(groups = {"anchor", "maker", "flow"})
    public void shouldResendInvitationToUnresponsiveDealer() {
        throw new SkipException("TODO: re-send invitation flow (if in scope) — Product Guide §Dealer Invitation");
    }

    @Test(groups = {"anchor", "maker", "flow"})
    public void shouldEditAndResubmitRejectedInvitation() {
        throw new SkipException("TODO: reject path — checker rejects invite, maker corrects details and resubmits");
    }
}
