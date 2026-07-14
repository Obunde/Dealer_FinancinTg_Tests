package com.scf.pages.bank.checker;

import com.microsoft.playwright.Page;
import com.scf.pages.ApprovalQueuePage;

/** Bank Checker — approve/reject anchor onboarding, offer letters and disbursements. */
public class BankApprovalQueuePage extends ApprovalQueuePage {

    public BankApprovalQueuePage(Page page) {
        super(page);
    }
}
