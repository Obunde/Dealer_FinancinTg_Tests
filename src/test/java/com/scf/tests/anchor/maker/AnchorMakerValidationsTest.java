package com.scf.tests.anchor.maker;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.pages.anchor.maker.DealerInvitationPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Negative/UI validations for Anchor Maker actions (Product Guide §Dealer Invitation). */
public class AnchorMakerValidationsTest extends BaseTest {

    @Test(groups = {"anchor", "maker", "validation"},
            description = "Dealer invitation form rejects submission when mandatory fields are empty")
    public void shouldShowErrorsForMandatoryFieldsOnDealerInvitationForm() {
        loginAs(Actor.ANCHOR, Role.MAKER);
        DealerInvitationPage invitation = new DealerInvitationPage(PlaywrightFactory.getPage());

        invitation.open();
        invitation.submit(); // every field empty

        Assert.assertEquals(invitation.getFieldError("Dealer Name"), "Dealer Name is required");
        Assert.assertEquals(invitation.getFieldError("Contact Email"), "Contact Email is required");
    }

    @Test(groups = {"anchor", "maker", "validation"})
    public void shouldRejectInvitingSameDealerTwice() {
        throw new SkipException("TODO: duplicate invitation (same registration number/email) must be blocked");
    }

    @Test(groups = {"anchor", "maker", "validation"})
    public void shouldRejectInvalidDealerEmailAndPhoneFormats() {
        throw new SkipException("TODO: email/phone format validation — Product Guide §Dealer Invitation field rules");
    }

    @Test(groups = {"anchor", "maker", "validation"})
    public void shouldNotAllowAnchorMakerToAccessBankScreens() {
        throw new SkipException("TODO: cross-actor access control — anchor user cannot open bank portal routes");
    }

    @Test(groups = {"anchor", "maker", "validation"})
    public void shouldEnforceCharacterLimitsOnDealerNameField() {
        throw new SkipException("TODO: min/max length and special-character handling on Dealer Name");
    }
}
