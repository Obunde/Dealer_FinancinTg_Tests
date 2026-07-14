package com.scf.pages.bank.maker;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.framework.models.Anchor;
import com.scf.pages.BasePage;

/** Bank Maker — onboard an Anchor (Product Guide §Anchor Onboarding). */
public class AnchorOnboardingPage extends BasePage {

    public AnchorOnboardingPage(Page page) {
        super(page);
    }

    public void open() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Onboard Anchor")).click();
    }

    public void fillAnchorDetails(Anchor anchor) {
        fillField("Anchor Name", anchor.name());
        fillField("Registration Number", anchor.registrationNumber());
        fillField("Contact Email", anchor.contactEmail());
        fillField("Contact Phone", anchor.contactPhone());
    }

    public void submit() {
        clickButton("Submit for Approval");
    }

    public void onboardAnchor(Anchor anchor) {
        open();
        fillAnchorDetails(anchor);
        submit();
    }
}
