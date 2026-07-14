package com.scf.framework.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.scf.framework.config.ConfigReader;

import java.nio.file.Paths;

/**
 * Thread-safe Playwright lifecycle manager. Each TestNG thread gets its own
 * Playwright + Browser + Context + Page, which makes parallel execution by
 * actor group safe.
 */
public final class PlaywrightFactory {

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightFactory() {
    }

    public static void initBrowser() {
        // Playwright-Java re-verifies/installs browsers on every create(). On some
        // environments (e.g. Kali here) that step hangs trying to reach the network
        // even though browsers are already cached. Skip it — browsers are installed
        // once via `mvn compile exec:java -Dexec.args="install chromium"`.
        Playwright playwright = Playwright.create(new Playwright.CreateOptions()
                .setEnv(java.util.Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1")));
        PLAYWRIGHT.set(playwright);

        // --no-sandbox / --disable-dev-shm-usage: required on Kali/Linux (and
        // when running as root or in CI) or Chromium hangs at launch.
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(ConfigReader.getBool("headless"))
                .setSlowMo(ConfigReader.getInt("slowmo.ms"))
                .setTimeout(60_000)
                .setArgs(java.util.List.of("--no-sandbox", "--disable-dev-shm-usage"));

        String browserName = ConfigReader.get("browser").toLowerCase();
        Browser browser = switch (browserName) {
            case "firefox" -> playwright.firefox().launch(options);
            case "webkit" -> playwright.webkit().launch(options);
            case "chromium", "chrome" -> playwright.chromium().launch(options);
            default -> throw new IllegalArgumentException("Unsupported browser: " + browserName);
        };
        BROWSER.set(browser);

        BrowserContext context = browser.newContext();
        context.setDefaultTimeout(ConfigReader.getInt("timeout.ms"));
        if (ConfigReader.getBool("trace.on.failure")) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true).setSnapshots(true).setSources(true));
        }
        CONTEXT.set(context);
        PAGE.set(context.newPage());
    }

    public static Page getPage() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("Browser not initialised — call initBrowser() first");
        }
        return page;
    }

    public static BrowserContext getContext() {
        return CONTEXT.get();
    }

    /** Stops tracing, writing a trace.zip for the given test name (viewable with `playwright show-trace`). */
    public static void saveTrace(String testName) {
        BrowserContext context = CONTEXT.get();
        if (context != null && ConfigReader.getBool("trace.on.failure")) {
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("traces", testName + "-trace.zip")));
        }
    }

    public static void closeBrowser() {
        if (CONTEXT.get() != null) {
            CONTEXT.get().close();
            CONTEXT.remove();
        }
        if (BROWSER.get() != null) {
            BROWSER.get().close();
            BROWSER.remove();
        }
        if (PLAYWRIGHT.get() != null) {
            PLAYWRIGHT.get().close();
            PLAYWRIGHT.remove();
        }
        PAGE.remove();
    }
}
