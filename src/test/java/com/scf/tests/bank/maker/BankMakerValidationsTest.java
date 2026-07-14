package com.scf.tests.bank.maker;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.framework.models.OfferLetter;
import com.scf.pages.bank.maker.OfferLetterPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/** Negative/UI validations for Bank Maker actions (Product Guide §Anchor Onboarding, §Offer Letter). */
public class BankMakerValidationsTest extends BaseTest {

    @Test(groups = {"bank", "maker", "validation"},
            description = "Offer letter rejects a zero borrowing limit (financial boundary)")
    public void shouldRejectZeroBorrowingLimitOnOfferLetter() {
        loginAs(Actor.BANK, Role.MAKER);
        OfferLetterPage offerPage = new OfferLetterPage(PlaywrightFactory.getPage());

        offerPage.open();
        offerPage.fillOffer(new OfferLetter("Any Dealer", new BigDecimal("12.5"), BigDecimal.ZERO, 90));
        offerPage.submit();

        Assert.assertEquals(offerPage.getFieldError("Borrowing Limit"),
                "Borrowing Limit must be greater than zero");
    }

    @Test(groups = {"bank", "maker", "validation"})
    public void shouldRejectNegativeInterestRate() {
        throw new SkipException("TODO: negative rate boundary — Product Guide §Offer Letter field rules");
    }

    @Test(groups = {"bank", "maker", "validation"})
    public void shouldRejectExcessDecimalPrecisionOnRateAndLimit() {
        throw new SkipException("TODO: decimal precision (e.g. 12.5555%) — Product Guide §Offer Letter field rules");
    }

    @Test(groups = {"bank", "maker", "validation"})
    public void shouldShowMandatoryFieldErrorsOnAnchorOnboardingForm() {
        throw new SkipException("TODO: empty-submit anchor onboarding form — Product Guide §Anchor Onboarding");
    }

    @Test(groups = {"bank", "maker", "validation"})
    public void shouldRejectDuplicateAnchorOnboarding() {
        throw new SkipException("TODO: onboard same anchor (same registration number) twice");
    }

    @Test(groups = {"bank", "maker", "validation"})
    public void shouldNotAllowMakerToAccessApprovalQueue() {
        throw new SkipException("TODO: maker must not see/reach the checker approval screens (role segregation)");
    }

    @Test(groups = {"bank", "maker", "validation"})
    public void shouldNotAllowDisbursementBeforeDealerLoanApproval() {
        throw new SkipException("TODO: cross-actor dependency — loan absent from disbursement list until dealer checker approves");
    }
}
