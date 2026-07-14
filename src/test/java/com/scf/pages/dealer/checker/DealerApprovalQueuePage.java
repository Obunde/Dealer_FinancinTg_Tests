package com.scf.pages.dealer.checker;

import com.microsoft.playwright.Page;
import com.scf.pages.ApprovalQueuePage;

/** Dealer Checker — approve/reject invitation acceptance, offer approval, loan requests and repayments. */
public class DealerApprovalQueuePage extends ApprovalQueuePage {

    public DealerApprovalQueuePage(Page page) {
        super(page);
    }
}
