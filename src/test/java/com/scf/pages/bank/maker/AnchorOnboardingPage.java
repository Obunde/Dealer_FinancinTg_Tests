package com.scf.pages.bank.maker;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.scf.framework.models.Anchor;
import com.scf.framework.utils.TestFiles;
import com.scf.pages.BasePage;

import java.util.regex.Pattern;

/**
 * Faulu Maker — Anchors -> + Add Anchor (locators verified via codegen
 * against https://dealerfinance.emtechhouse.co.ke, 2026-07-14).
 */
public class AnchorOnboardingPage extends BasePage {

    public AnchorOnboardingPage(Page page) {
        super(page);
    }

    /**
     * The left-hand nav "Anchors" button. A plain name match is ambiguous —
     * "Manage Anchors" and "Anchors Table" also exist — so target the side-nav
     * button by its class plus text.
     */
    public void openAnchorsList() {
        page.locator("button.side-buttons", new Page.LocatorOptions().setHasText("Anchors")).click();
    }

    public void open() {
        openAnchorsList();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+ Add Anchor")).click();
    }

    public void fillAnchorDetails(Anchor anchor) {
        page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Anchor Buyer...")).fill(anchor.name());

        Locator form = page.locator("#anchorDetailsForm");
        form.getByRole(AriaRole.TEXTBOX,
                new Locator.GetByRoleOptions().setName("Email Address")).fill(anchor.email());
        form.getByRole(AriaRole.TEXTBOX,
                new Locator.GetByRoleOptions().setName("Contact Number")).fill(anchor.contactNumber());
        page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Account Number")).fill(anchor.accountNumber());
    }

    /**
     * Business Photo, Certificate of Incorporation, Business Permit.
     *
     * The app's upload dialog (mat-dialog with file picker + document-name
     * field) is currently BROKEN — defect found during manual walkthrough on
     * 2026-07-14. Workaround: inject files directly into the underlying
     * <input type=file> elements, which worked for businessPhoto in codegen.
     * Revisit once the defect is fixed.
     */
    public void uploadDocuments() {
        page.locator("input[name='businessPhoto']")
                .setInputFiles(TestFiles.dummyPng("business-photo.png"));

        // TODO: confirm the input names/order for Certificate of Incorporation
        // and Business Permit once the upload dialog defect is fixed.
        Locator fileInputs = page.locator("input[type='file']");
        int count = fileInputs.count();
        for (int i = 0; i < count; i++) {
            Locator input = fileInputs.nth(i);
            String name = input.getAttribute("name");
            if (name == null || !name.equals("businessPhoto")) {
                input.setInputFiles(TestFiles.dummyPng("document-" + i + ".png"));
            }
        }
    }

    public void submit() {
        // KNOWN DEFECT (2026-07-14): the Submit button stays disabled because the
        // document-upload dialog never attaches the files, so the form never
        // validates. This test is intentionally left red as a marker until the
        // app defect is fixed. Fail fast with a clear message instead of a 25s
        // "element is not enabled" timeout.
        Locator submit = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
        submit.waitFor();
        if (!submit.isEnabled()) {
            throw new AssertionError("Submit is disabled — anchor onboarding is blocked by the "
                    + "document-upload defect (files uploaded via the dialog do not attach, "
                    + "so the form never becomes valid). Known bug as of 2026-07-14.");
        }
        submit.click();
    }

    public void onboardAnchor(Anchor anchor) {
        open();
        fillAnchorDetails(anchor);
        uploadDocuments();
        submit();
    }

    public boolean isAnchorListed(String anchorName) {
        openAnchorsList();
        return page.locator("tr", new Page.LocatorOptions().setHasText(anchorName)).count() > 0;
    }
}
