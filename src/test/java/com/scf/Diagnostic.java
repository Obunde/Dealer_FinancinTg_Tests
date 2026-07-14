package com.scf;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.util.List;

/** Standalone browser-launch smoke check — no TestNG, no surefire. */
public class Diagnostic {
    public static void main(String[] args) {
        System.out.println(">>> creating Playwright");
        try (Playwright playwright = Playwright.create(new Playwright.CreateOptions()
                .setEnv(java.util.Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1")))) {
            System.out.println(">>> Playwright created; launching chromium");
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setTimeout(60_000)
                    .setArgs(List.of("--no-sandbox", "--disable-dev-shm-usage")));
            System.out.println(">>> browser launched; opening page");
            Page page = browser.newContext().newPage();
            System.out.println(">>> navigating to login");
            page.navigate("https://dealerfinance.emtechhouse.co.ke/login");
            System.out.println(">>> navigated. title = " + page.title());
            browser.close();
            System.out.println(">>> DONE OK");
        } catch (Throwable t) {
            System.out.println(">>> FAILED: " + t);
            t.printStackTrace();
        }
    }
}
