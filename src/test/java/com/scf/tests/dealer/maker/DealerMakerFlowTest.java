package com.scf.tests.dealer.maker;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.framework.models.LoanRequest;
import com.scf.framework.utils.TestDataFactory;
import com.scf.pages.dealer.maker.LoanRequestPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Positive flows for Dealer Maker actions (Product Guide §Loan Request, §Repayment). */
public class DealerMakerFlowTest extends BaseTest {

    @Test(groups = {"dealer", "maker", "flow"},
            description = "Maker submits a loan request within limit and it lands in Pending Approval")
    public void shouldSubmitLoanRequestForApproval() {
        loginAs(Actor.DEALER, Role.MAKER);
        LoanRequestPage loanPage = new LoanRequestPage(PlaywrightFactory.getPage());
        LoanRequest loan = TestDataFactory.loanRequest("self");

        loanPage.requestLoan(loan);

        Assert.assertTrue(loanPage.getToastMessage().contains("submitted for approval"));
        Assert.assertEquals(loanPage.getRecordStatus(loan.invoiceReference()), "Pending Approval");
    }

    @Test(groups = {"dealer", "maker", "flow"})
    public void shouldAcceptAnchorInvitationAndSubmitForApproval() {
        throw new SkipException("TODO: invitation acceptance happy path — Product Guide §Dealer Onboarding");
    }

    @Test(groups = {"dealer", "maker", "flow"})
    public void shouldAcceptOfferLetterTermsAndSubmitForApproval() {
        throw new SkipException("TODO: offer terms acceptance happy path — Product Guide §Offer Letter");
    }

    @Test(groups = {"dealer", "maker", "flow"})
    public void shouldInitiateRepaymentForDisbursedLoan() {
        throw new SkipException("TODO: repayment happy path — Product Guide §Repayment");
    }

    @Test(groups = {"dealer", "maker", "flow"})
    public void shouldEditAndResubmitRejectedLoanRequest() {
        throw new SkipException("TODO: reject path — checker rejects loan, maker adjusts amount and resubmits");
    }
}
