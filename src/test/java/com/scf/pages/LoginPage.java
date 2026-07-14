package com.scf.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/** Shared login screen — all four portals use the same authentication UI. */
public class LoginPage extends BasePage {

    public LoginPage(Page page) {
        super(page);
    }

    public void login(String username, String password) {
        page.getByLabel("Username").or(page.getByLabel("Email")).first().fill(username);
        page.getByLabel("Password").fill(password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
    }

    public String getLoginError() {
        return getToastMessage();
    }
}
