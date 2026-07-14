package com.scf.pages.anchor.maker;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.framework.models.Dealer;
import com.scf.pages.BasePage;

/** Anchor Maker — invite a Dealer to the platform (Product Guide §Dealer Invitation). */
public class DealerInvitationPage extends BasePage {

    public DealerInvitationPage(Page page) {
        super(page);
    }

    public void open() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Invite Dealer")).click();
    }

    public void fillDealerDetails(Dealer dealer) {
        fillField("Dealer Name", dealer.name());
        fillField("Registration Number", dealer.registrationNumber());
        fillField("Contact Email", dealer.contactEmail());
        fillField("Contact Phone", dealer.contactPhone());
    }

    public void submit() {
        clickButton("Send Invitation");
    }

    public void inviteDealer(Dealer dealer) {
        open();
        fillDealerDetails(dealer);
        submit();
    }
}
