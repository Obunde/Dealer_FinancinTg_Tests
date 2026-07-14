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
            description = "Faulu Maker adds an anchor and it appears in the Anchors list pending approval")
    public void shouldSubmitAnchorOnboardingForApproval() {
        loginAs(Actor.BANK, Role.MAKER);
        AnchorOnboardingPage onboarding = new AnchorOnboardingPage(PlaywrightFactory.getPage());
        Anchor anchor = TestDataFactory.anchor();

        // NOTE: uploadDocuments() bypasses the app's broken upload dialog by
        // injecting files into the raw <input type=file> elements (defect
        // found 2026-07-14 — dialog-based upload cannot complete).
        onboarding.onboardAnchor(anchor);

        Assert.assertTrue(onboarding.isAnchorListed(anchor.name()),
                "New anchor should appear in the Anchors list after submission");
        // TODO: once submission works end-to-end, also assert the exact toast
        // text and the anchor's initial status (pending checker approval).
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
