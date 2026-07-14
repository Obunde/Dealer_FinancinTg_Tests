package com.scf.tests.systemadmin;

import com.scf.base.BaseTest;
import com.scf.framework.models.Bank;
import com.scf.framework.utils.TestDataFactory;
import com.scf.pages.systemadmin.BankBoardingPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Negative/UI validations on the System Admin portal (Product Guide §Bank Boarding). */
public class SystemAdminValidationsTest extends BaseTest {

    @Test(groups = {"systemadmin", "validation"},
            description = "Bank boarding form rejects submission when mandatory fields are empty")
    public void shouldShowErrorsForMandatoryFieldsOnBankBoardingForm() {
        loginAsSystemAdmin();
        BankBoardingPage boarding = new BankBoardingPage(com.scf.framework.driver.PlaywrightFactory.getPage());

        boarding.open();
        boarding.submit(); // submit with every field empty

        Assert.assertEquals(boarding.getFieldError("Bank Name"), "Bank Name is required");
        Assert.assertEquals(boarding.getFieldError("SWIFT Code"), "SWIFT Code is required");
        Assert.assertEquals(boarding.getFieldError("Contact Email"), "Contact Email is required");
    }

    @Test(groups = {"systemadmin", "validation"},
            description = "Boarding the same bank (same SWIFT code) twice is rejected")
    public void shouldRejectDuplicateBankBoarding() {
        loginAsSystemAdmin();
        BankBoardingPage boarding = new BankBoardingPage(com.scf.framework.driver.PlaywrightFactory.getPage());
        Bank bank = TestDataFactory.bank();

        boarding.boardBank(bank);
        boarding.boardBank(bank); // second attempt with identical details

        Assert.assertTrue(boarding.getToastMessage().contains("already exists"),
                "Expected duplicate-bank error toast");
    }

    @Test(groups = {"systemadmin", "validation"})
    public void shouldRejectInvalidEmailFormatOnBankBoardingForm() {
        throw new SkipException("TODO: invalid email format — Product Guide §Bank Boarding field rules");
    }

    @Test(groups = {"systemadmin", "validation"})
    public void shouldRejectInvalidSwiftCodeFormat() {
        throw new SkipException("TODO: SWIFT code format/length boundary — Product Guide §Bank Boarding field rules");
    }

    @Test(groups = {"systemadmin", "validation"})
    public void shouldBlockNonAdminUserFromAdminPortal() {
        throw new SkipException("TODO: login to admin portal with bank/anchor/dealer credentials — access must be denied");
    }

    @Test(groups = {"systemadmin", "validation"})
    public void shouldExpireIdleSessionAndRedirectToLogin() {
        throw new SkipException("TODO: session timeout behaviour — Product Guide §Security/Sessions");
    }
}
