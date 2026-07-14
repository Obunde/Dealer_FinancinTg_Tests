package com.scf.pages.anchor.checker;

import com.microsoft.playwright.Page;
import com.scf.pages.ApprovalQueuePage;

/** Anchor Checker — approve/reject dealer invitations and other anchor-side maker actions. */
public class AnchorApprovalQueuePage extends ApprovalQueuePage {

    public AnchorApprovalQueuePage(Page page) {
        super(page);
    }
}
