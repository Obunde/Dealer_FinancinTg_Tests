package com.scf.tests.e2e;

import com.scf.base.BaseTest;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.framework.models.Anchor;
import com.scf.framework.models.Bank;
import com.scf.framework.models.Dealer;
import com.scf.framework.models.LoanRequest;
import com.scf.framework.models.OfferLetter;
import com.scf.framework.utils.TestDataFactory;
import com.scf.pages.anchor.checker.AnchorApprovalQueuePage;
import com.scf.pages.anchor.maker.DealerInvitationPage;
import com.scf.pages.bank.checker.BankApprovalQueuePage;
import com.scf.pages.bank.maker.AnchorOnboardingPage;
import com.scf.pages.bank.maker.DisbursementPage;
import com.scf.pages.bank.maker.OfferLetterPage;
import com.scf.pages.dealer.checker.DealerApprovalQueuePage;
import com.scf.pages.dealer.maker.InvitationAcceptancePage;
import com.scf.pages.dealer.maker.LoanRequestPage;
import com.scf.pages.dealer.maker.OfferAcceptancePage;
import com.scf.pages.dealer.maker.RepaymentPage;
import com.scf.pages.systemadmin.BankBoardingPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Full cross-actor journey (Product Guide §End-to-End Business Flow):
 *
 *   Admin boards Bank -> Bank onboards Anchor -> Anchor invites Dealer ->
 *   Dealer accepts -> Bank sends offer letter -> Dealer approves offer ->
 *   Dealer requests loan (limit reduces) -> Bank disburses to Anchor ->
 *   Dealer repays (limit restores).
 *
 * Every business step is a Maker action followed by a Checker approval.
 * Steps share one entity set (class fields) and are chained with
 * dependsOnMethods so a failure aborts the remaining journey. Runs
 * single-threaded — do not include this class in parallel suites.
 *
 * NOTE: dealer invitation acceptance normally requires following the emailed
 * invite link and registering dealer users. Here we assume pre-provisioned
 * dealer maker/checker accounts (see config.properties); wire in a mailbox
 * helper later if invite-link coverage is needed.
 */
@Test(groups = {"e2e"})
public class EndToEndOnboardingAndLoanCycleTest extends BaseTest {

    private final Bank bank = TestDataFactory.bank();
    private final Anchor anchor = TestDataFactory.anchor();
    private final Dealer dealer = TestDataFactory.dealer();
    private final OfferLetter offer = TestDataFactory.offerLetter(null);
    private final LoanRequest loan = TestDataFactory.loanRequest(null);

    @Test(description = "Step 1 — System Admin boards the Bank")
    public void step1_adminBoardsBank() {
        loginAsSystemAdmin();
        BankBoardingPage boarding = new BankBoardingPage(PlaywrightFactory.getPage());
        boarding.boardBank(bank);
        Assert.assertTrue(boarding.isBankListed(bank.name()));
    }

    @Test(dependsOnMethods = "step1_adminBoardsBank",
            description = "Step 2 — Bank Maker onboards the Anchor, Bank Checker approves")
    public void step2_bankOnboardsAnchor() {
        loginAs(Actor.BANK, Role.MAKER);
        new AnchorOnboardingPage(PlaywrightFactory.getPage()).onboardAnchor(anchor);

        var checkerPage = loginAs(Actor.BANK, Role.CHECKER);
        BankApprovalQueuePage queue = new BankApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        queue.approve(anchor.name(), "E2E — anchor onboarding approved");
        Assert.assertEquals(queue.getRecordStatus(anchor.name()), "Active");
    }

    @Test(dependsOnMethods = "step2_bankOnboardsAnchor",
            description = "Step 3 — Anchor Maker invites the Dealer, Anchor Checker approves")
    public void step3_anchorInvitesDealer() {
        loginAs(Actor.ANCHOR, Role.MAKER);
        new DealerInvitationPage(PlaywrightFactory.getPage()).inviteDealer(dealer);

        var checkerPage = loginAs(Actor.ANCHOR, Role.CHECKER);
        AnchorApprovalQueuePage queue = new AnchorApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        queue.approve(dealer.name(), "E2E — dealer invitation approved");
        Assert.assertEquals(queue.getRecordStatus(dealer.name()), "Invited");
    }

    @Test(dependsOnMethods = "step3_anchorInvitesDealer",
            description = "Step 4 — Dealer Maker accepts the invitation, Dealer Checker approves")
    public void step4_dealerAcceptsInvitation() {
        loginAs(Actor.DEALER, Role.MAKER);
        new InvitationAcceptancePage(PlaywrightFactory.getPage()).acceptInvitation(anchor.name());

        var checkerPage = loginAs(Actor.DEALER, Role.CHECKER);
        DealerApprovalQueuePage queue = new DealerApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        queue.approve(anchor.name(), "E2E — invitation acceptance approved");
        Assert.assertEquals(queue.getRecordStatus(anchor.name()), "Accepted");
    }

    @Test(dependsOnMethods = "step4_dealerAcceptsInvitation",
            description = "Step 5 — Bank Maker sends the offer letter, Bank Checker approves")
    public void step5_bankSendsOfferLetter() {
        loginAs(Actor.BANK, Role.MAKER);
        new OfferLetterPage(PlaywrightFactory.getPage())
                .createOffer(new OfferLetter(dealer.name(), offer.interestRate(),
                        offer.borrowingLimit(), offer.tenorDays()));

        var checkerPage = loginAs(Actor.BANK, Role.CHECKER);
        BankApprovalQueuePage queue = new BankApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        queue.approve(dealer.name(), "E2E — offer letter approved");
        Assert.assertEquals(queue.getRecordStatus(dealer.name()), "Sent");
    }

    @Test(dependsOnMethods = "step5_bankSendsOfferLetter",
            description = "Step 6 — Dealer Maker accepts the offer terms, Dealer Checker approves; limit becomes active")
    public void step6_dealerApprovesOffer() {
        loginAs(Actor.DEALER, Role.MAKER);
        new OfferAcceptancePage(PlaywrightFactory.getPage()).acceptOffer(bank.name());

        var checkerPage = loginAs(Actor.DEALER, Role.CHECKER);
        DealerApprovalQueuePage queue = new DealerApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        queue.approve(bank.name(), "E2E — offer terms approved");

        loginAs(Actor.DEALER, Role.MAKER);
        String limit = new LoanRequestPage(PlaywrightFactory.getPage()).getAvailableLimit();
        Assert.assertTrue(limit.contains(offer.borrowingLimit().toPlainString()),
                "Full borrowing limit must be available once the offer is active");
    }

    @Test(dependsOnMethods = "step6_dealerApprovesOffer",
            description = "Step 7 — Dealer requests a loan; on approval the revolving limit reduces by the loan amount")
    public void step7_dealerRequestsLoan() {
        loginAs(Actor.DEALER, Role.MAKER);
        LoanRequestPage loanPage = new LoanRequestPage(PlaywrightFactory.getPage());
        loanPage.requestLoan(new LoanRequest(dealer.name(), loan.amount(), loan.purpose(),
                loan.invoiceReference()));

        var checkerPage = loginAs(Actor.DEALER, Role.CHECKER);
        DealerApprovalQueuePage queue = new DealerApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        queue.approve(loan.invoiceReference(), "E2E — loan request approved");

        loginAs(Actor.DEALER, Role.MAKER);
        String limitAfter = new LoanRequestPage(PlaywrightFactory.getPage()).getAvailableLimit();
        String expected = offer.borrowingLimit().subtract(loan.amount()).toPlainString();
        Assert.assertTrue(limitAfter.contains(expected),
                "Limit must reduce by exactly the loan amount (expected " + expected + ", saw " + limitAfter + ")");
    }

    @Test(dependsOnMethods = "step7_dealerRequestsLoan",
            description = "Step 8 — Bank disburses the loan amount to the Anchor")
    public void step8_bankDisbursesToAnchor() {
        loginAs(Actor.BANK, Role.MAKER);
        DisbursementPage disbursement = new DisbursementPage(PlaywrightFactory.getPage());
        Assert.assertTrue(disbursement.isLoanAvailableForDisbursement(loan.invoiceReference()),
                "Approved loan must be available for disbursement");
        disbursement.initiateDisbursement(loan.invoiceReference());

        var checkerPage = loginAs(Actor.BANK, Role.CHECKER);
        BankApprovalQueuePage queue = new BankApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        queue.approve(loan.invoiceReference(), "E2E — disbursement approved");
        Assert.assertEquals(queue.getRecordStatus(loan.invoiceReference()), "Disbursed");
    }

    @Test(dependsOnMethods = "step8_bankDisbursesToAnchor",
            description = "Step 9 — Dealer repays the loan; on approval the revolving limit is restored")
    public void step9_dealerRepaysLoan() {
        loginAs(Actor.DEALER, Role.MAKER);
        new RepaymentPage(PlaywrightFactory.getPage())
                .initiateRepayment(loan.invoiceReference(), loan.amount().toPlainString());

        var checkerPage = loginAs(Actor.DEALER, Role.CHECKER);
        DealerApprovalQueuePage queue = new DealerApprovalQueuePage(checkerPage);
        queue.openPendingApprovals();
        queue.approve(loan.invoiceReference(), "E2E — repayment approved");
        Assert.assertEquals(queue.getRecordStatus(loan.invoiceReference()), "Repaid");

        loginAs(Actor.DEALER, Role.MAKER);
        String limitRestored = new LoanRequestPage(PlaywrightFactory.getPage()).getAvailableLimit();
        Assert.assertTrue(limitRestored.contains(offer.borrowingLimit().toPlainString()),
                "Limit must be fully restored after full repayment");
    }
}
