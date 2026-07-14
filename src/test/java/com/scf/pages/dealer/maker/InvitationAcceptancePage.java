package com.scf.pages.dealer.maker;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.pages.BasePage;

/** Dealer Maker — accept the Anchor's invitation (Product Guide §Dealer Onboarding). */
public class InvitationAcceptancePage extends BasePage {

    public InvitationAcceptancePage(Page page) {
        super(page);
    }

    public void open() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Invitations")).click();
    }

    public void acceptInvitation(String anchorName) {
        open();
        page.locator("tr", new Page.LocatorOptions().setHasText(anchorName))
                .getByRole(AriaRole.BUTTON,
                        new com.microsoft.playwright.Locator.GetByRoleOptions().setName("Accept"))
                .click();
        clickButton("Submit for Approval");
    }
}
