package com.scf.pages.bank.maker;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.framework.models.OfferLetter;
import com.scf.pages.BasePage;

/** Bank Maker — create an offer letter (rate/limit/tenor) for an onboarded Dealer (Product Guide §Offer Letter). */
public class OfferLetterPage extends BasePage {

    public OfferLetterPage(Page page) {
        super(page);
    }

    public void open() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Offer Letters")).click();
        clickButton("New Offer Letter");
    }

    public void fillOffer(OfferLetter offer) {
        page.getByLabel("Dealer").selectOption(offer.dealerName());
        fillField("Interest Rate", offer.interestRate().toPlainString());
        fillField("Borrowing Limit", offer.borrowingLimit().toPlainString());
        fillField("Tenor (days)", String.valueOf(offer.tenorDays()));
    }

    public void submit() {
        clickButton("Submit for Approval");
    }

    public void createOffer(OfferLetter offer) {
        open();
        fillOffer(offer);
        submit();
    }
}
