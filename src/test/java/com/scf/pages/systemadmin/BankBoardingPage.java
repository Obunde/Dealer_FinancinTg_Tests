package com.scf.pages.systemadmin;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.framework.models.Bank;
import com.scf.pages.BasePage;

/** System Admin — board a Financial Institution onto the platform (Product Guide §Bank Boarding). */
public class BankBoardingPage extends BasePage {

    public BankBoardingPage(Page page) {
        super(page);
    }

    public void open() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Board Bank")).click();
    }

    public void fillBankDetails(Bank bank) {
        fillField("Bank Name", bank.name());
        fillField("SWIFT Code", bank.swiftCode());
        fillField("Contact Email", bank.contactEmail());
        fillField("Contact Phone", bank.contactPhone());
    }

    public void submit() {
        clickButton("Submit");
    }

    public void boardBank(Bank bank) {
        open();
        fillBankDetails(bank);
        submit();
    }

    public boolean isBankListed(String bankName) {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Banks")).click();
        return page.locator("tr", new Page.LocatorOptions().setHasText(bankName)).count() > 0;
    }
}
