package com.scf.base;

import com.microsoft.playwright.Page;
import com.scf.framework.config.ConfigReader;
import com.scf.framework.driver.PlaywrightFactory;
import com.scf.framework.enums.Actor;
import com.scf.framework.enums.Role;
import com.scf.pages.LoginPage;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

/**
 * Browser/context lifecycle per test method + role-based login helper.
 * Each @Test gets a fresh browser context, so tests stay independent and can
 * run in parallel (parallel="tests" in the suite XMLs groups them by actor).
 */
public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        PlaywrightFactory.initBrowser();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!result.isSuccess()) {
            Page page = PlaywrightFactory.getPage();
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment(result.getName() + " — failure screenshot",
                    new ByteArrayInputStream(screenshot));
            PlaywrightFactory.saveTrace(result.getName());
        }
        PlaywrightFactory.closeBrowser();
    }

    /** Logs into an actor portal as the given maker/checker user and returns the Page. */
    protected Page loginAs(Actor actor, Role role) {
        String credentialPrefix = actor.configKey() + "." + role.configKey();
        Page page = PlaywrightFactory.getPage();
        // Drop any previous actor's session so one @Test can switch users (E2E journey).
        PlaywrightFactory.getContext().clearCookies();
        page.navigate(ConfigReader.get(actor.configKey() + ".url"));
        new LoginPage(page).login(
                ConfigReader.get(credentialPrefix + ".username"),
                ConfigReader.get(credentialPrefix + ".password"));
        return page;
    }

    /** Platform Admin (Emtech) — also a maker/checker pair; defaults to the maker. */
    protected Page loginAsSystemAdmin() {
        return loginAs(Actor.SYSTEM_ADMIN, Role.MAKER);
    }
}
