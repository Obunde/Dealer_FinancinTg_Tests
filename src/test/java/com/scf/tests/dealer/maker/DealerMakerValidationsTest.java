package com.scf.tests.dealer.maker;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.framework.models.LoanRequest;
import com.scf.pages.dealer.maker.LoanRequestPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/** Negative/UI validations for Dealer Maker actions (Product Guide §Loan Request, §Repayment). */
public class DealerMakerValidationsTest extends BaseTest {

    @Test(groups = {"dealer", "maker", "validation"},
            description = "Loan request exceeding the available revolving limit is rejected")
    public void shouldRejectLoanRequestExceedingAvailableLimit() {
        loginAs(Actor.DEALER, Role.MAKER);
        LoanRequestPage loanPage = new LoanRequestPage(PlaywrightFactory.getPage());

        loanPage.open();
        // Deliberately far above any configured limit
        loanPage.fillLoanRequest(new LoanRequest("self", new BigDecimal("999999999999.00"),
                "Over-limit boundary test", "INV-OVERLIMIT"));
        loanPage.submit();

        Assert.assertEquals(loanPage.getFieldError("Amount"),
                "Amount exceeds your available limit");
    }

    @Test(groups = {"dealer", "maker", "validation"})
    public void shouldRejectZeroAndNegativeLoanAmounts() {
        throw new SkipException("TODO: zero/negative amount boundaries — Product Guide §Loan Request field rules");
    }

    @Test(groups = {"dealer", "maker", "validation"})
    public void shouldRejectLoanRequestBeforeOfferLetterIsActive() {
        throw new SkipException("TODO: cross-actor dependency — loan blocked until offer letter approved by both sides");
    }

    @Test(groups = {"dealer", "maker", "validation"})
    public void shouldRejectRepaymentGreaterThanOutstandingBalance() {
        throw new SkipException("TODO: repayment amount boundary — cannot exceed outstanding loan balance");
    }

    @Test(groups = {"dealer", "maker", "validation"})
    public void shouldRejectNonNumericInputInAmountFields() {
        throw new SkipException("TODO: numeric-only enforcement on Amount/Repayment Amount fields");
    }

    @Test(groups = {"dealer", "maker", "validation"})
    public void shouldNotAllowDealerMakerToApproveOwnLoanRequest() {
        throw new SkipException("TODO: maker cannot access the checker queue for their own submission");
    }
}
