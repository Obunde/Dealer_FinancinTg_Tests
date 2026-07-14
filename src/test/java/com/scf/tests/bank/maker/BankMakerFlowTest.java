package com.scf.tests.bank.maker;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.framework.models.Anchor;
import com.scf.framework.utils.TestDataFactory;
import com.scf.pages.bank.maker.AnchorOnboardingPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Positive flows for Bank Maker actions (Product Guide §Anchor Onboarding, §Offer Letter, §Disbursement). */
public class BankMakerFlowTest extends BaseTest {

    @Test(groups = {"bank", "maker", "flow"},
            description = "Maker submits anchor onboarding and the record lands in Pending Approval")
    public void shouldSubmitAnchorOnboardingForApproval() {
        loginAs(Actor.BANK, Role.MAKER);
        AnchorOnboardingPage onboarding = new AnchorOnboardingPage(PlaywrightFactory.getPage());
        Anchor anchor = TestDataFactory.anchor();

        onboarding.onboardAnchor(anchor);

        Assert.assertTrue(onboarding.getToastMessage().contains("submitted for approval"),
                "Expected submitted-for-approval confirmation");
        Assert.assertEquals(onboarding.getRecordStatus(anchor.name()), "Pending Approval",
                "Maker submission must sit in Pending Approval until a checker acts");
    }

    @Test(groups = {"bank", "maker", "flow"})
    public void shouldCreateOfferLetterForOnboardedDealer() {
        throw new SkipException("TODO: offer letter happy path -> Pending Approval — Product Guide §Offer Letter");
    }

    @Test(groups = {"bank", "maker", "flow"})
    public void shouldEditAndResubmitRejectedAnchorOnboarding() {
        throw new SkipException("TODO: reject path — checker rejects, maker edits and resubmits with status transitions");
    }

    @Test(groups = {"bank", "maker", "flow"})
    public void shouldInitiateDisbursementForApprovedLoan() {
        throw new SkipException("TODO: disbursement happy path after dealer loan approval — Product Guide §Disbursement");
    }
}
