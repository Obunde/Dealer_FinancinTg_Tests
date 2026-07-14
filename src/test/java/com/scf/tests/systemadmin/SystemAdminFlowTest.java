package com.scf.tests.systemadmin;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.models.Bank;
import com.scf.framework.utils.TestDataFactory;
import com.scf.pages.systemadmin.BankBoardingPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Positive flows on the System Admin portal (Product Guide §Bank Boarding). */
public class SystemAdminFlowTest extends BaseTest {

    @Test(groups = {"systemadmin", "flow"},
            description = "Admin boards a new bank and it appears in the banks list as Active")
    public void shouldBoardNewBankSuccessfully() {
        loginAsSystemAdmin();
        BankBoardingPage boarding = new BankBoardingPage(PlaywrightFactory.getPage());
        Bank bank = TestDataFactory.bank();

        boarding.boardBank(bank);

        Assert.assertTrue(boarding.getToastMessage().contains("successfully"),
                "Expected success toast after boarding bank");
        Assert.assertTrue(boarding.isBankListed(bank.name()),
                "Newly boarded bank should appear in the banks list");
    }

    @Test(groups = {"systemadmin", "flow"})
    public void shouldSendCredentialSetupInviteToBankAdminOnBoarding() {
        throw new SkipException("TODO: verify bank user invite/notification is triggered — Product Guide §Bank Boarding");
    }

    @Test(groups = {"systemadmin", "flow"})
    public void shouldDeactivateAndReactivateBank() {
        throw new SkipException("TODO: bank lifecycle management — Product Guide §Bank Administration");
    }
}
