package com.scf.tests.anchor.checker;

import com.scf.base.BaseTest;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.pages.anchor.checker.AnchorApprovalQueuePage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Negative/authorization validations for Anchor Checker. */
public class AnchorCheckerValidationsTest extends BaseTest {

    @Test(groups = {"anchor", "checker", "validation"},
            description = "Checker cannot see maker-only actions on the anchor portal")
    public void shouldNotAllowCheckerToInviteDealers() {
        var page = loginAs(Actor.ANCHOR, Role.CHECKER);
        new AnchorApprovalQueuePage(page).openPendingApprovals();

        Assert.assertFalse(page.getByText("Invite Dealer").isVisible(),
                "Checker must not see the maker's 'Invite Dealer' action");
    }

    @Test(groups = {"anchor", "checker", "validation"})
    public void shouldBlockCheckerFromApprovingOwnSubmission() {
        throw new SkipException("TODO: self-approval must be blocked (Maker-Checker segregation)");
    }

    @Test(groups = {"anchor", "checker", "validation"})
    public void shouldRequireReasonWhenRejectingInvitation() {
        throw new SkipException("TODO: reject without reason blocked with field error");
    }

    @Test(groups = {"anchor", "checker", "validation"})
    public void shouldDenyAccessWithExpiredSession() {
        throw new SkipException("TODO: expired session redirects to login without exposing queue data");
    }
}
