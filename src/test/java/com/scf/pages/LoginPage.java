package com.scf.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;

/**
 * Login screen at /login — one page for all actors; the role determines what
 * appears after login. Flow: email address -> password -> Continue.
 */
public class LoginPage extends BasePage {

    public LoginPage(Page page) {
        super(page);
    }

    public void login(String username, String password) {
        Locator email = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Email address"));
        try {
            email.waitFor(new Locator.WaitForOptions().setTimeout(3000));
        } catch (TimeoutError e) {
            // Landing page shows sign-in options first ("Admin Sign In" / "Sign in with ...")
            // TODO: confirm this is the right option for all actors
            page.getByText("Admin Sign In").first().click();
            email.waitFor();
        }
        email.fill(username);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill(password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
    }

    public String getLoginError() {
        return getToastMessage();
    }
}
