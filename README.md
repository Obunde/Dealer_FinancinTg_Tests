# Faulu Dealer Financing — QA Automation (Playwright Java + TestNG)

UI test automation for the Emtech Supply Chain Finance platform, with **Faulu**
as the FI under test. The platform connects Faulu, an **Anchor** (large buyer)
and **Dealers** (the Anchor's suppliers), and every business action follows the
**Maker-Checker** model: a Maker initiates, a Checker of the same organization
approves via *View → Update Status → Approve/Reject*.

**The authoritative step-by-step flow is in [docs/E2E-FLOW.md](docs/E2E-FLOW.md).** In short:

```
Faulu onboards Anchor (Maker→Checker; Anchor gets first-time creds by email)
  → Anchor Maker (after forced password reset) recommends Dealer
  → Dealer accepts email invitation (consent + form), Faulu Checker approves
  → Faulu creates Program (revolving limit/interest/charges), Faulu Checker approves
  → Dealer Checker approves Program → Program Active
  → Dealer Maker requests financing within Program limit, Dealer Checker approves
  → Faulu Checker approves → funds disbursed to Anchor (verify Payments + Transactions)
  → Faulu Maker uploads repayment batch (loanId + totalRepayableAmount)
  → Faulu Checker approves Batch Draft → loan status: Repayment Confirmed
```

Live app: `https://dealerfinance.emtechhouse.co.ke` (all actors log in at `/login`).

## Tech stack

| Piece | Choice |
|---|---|
| Browser automation | Playwright for Java |
| Test runner | TestNG (groups, retries via `RetryTransformer`, parallel by actor) |
| Build | Maven (Java 21) |
| Design pattern | Page Object Model, one package per actor portal area |
| Reporting | Allure (`mvn allure:serve`) with failure screenshots + Playwright traces |
| Config | `.env` at project root for URLs/credentials (gitignored); non-secret defaults in `config.properties` |

## Feature status

We verify each feature's locators against the live app (via codegen) before its
test is a trustworthy pass/fail signal. Current state:

**✅ Verified against the real app**
- **Login** — all actors (anchor/dealer log in directly; Faulu admin uses an "Admin Sign In" option)
- **Anchor onboarding** (Faulu Maker: Anchors → + Add Anchor) — wired & correct, **red due to Defect #1**
- **Dealer recommendation** (Anchor Maker: Dealers → Recommend Dealer) — wired & correct, **red due to Defect #2**

**🟡 Scaffolded — locators NOT yet verified (fail with misleading timeouts until codegen'd)**
- Bank boarding (System Admin), Program/Offer creation (Faulu Maker),
  Checker approvals (Bank/Anchor/Dealer), invitation acceptance & offer
  acceptance (Dealer Maker), loan request (Dealer Maker), disbursement
  (Faulu Maker), repayment batch upload, and the full E2E journey.

**⚪ Stubbed `@Test` TODOs** — ~54 validation/flow methods throwing `SkipException`,
each naming the Product Guide section it maps to (show as *skipped* in reports).

### Known defects (tests intentionally left RED as markers)
| # | Feature | Symptom |
|---|---|---|
| 1 | Anchor onboarding | Document-upload dialog never attaches files, so the form never validates and **Submit stays disabled**. |
| 2 | Recommend Dealer | With all fields validly filled, an **"Error: Something went wrong"** toast fires and **Save Changes stays disabled**. |

## Folder structure

```
src/main/java/com/scf/framework/     ← reusable framework (no test code)
  config/       ConfigReader (.env + -D + env-var + config.properties resolution)
  driver/       PlaywrightFactory (thread-safe browser lifecycle, tracing, launch flags)
  enums/        Actor (SYSTEM_ADMIN/BANK/ANCHOR/DEALER), Role (MAKER/CHECKER)
  models/       Bank, Anchor(name/email/contactNumber/accountNumber),
                Dealer(name/erpCode/email/contactNumber), OfferLetter, LoanRequest
  utils/        TestDataFactory (unique, re-runnable entities), TestFiles
  api/          ApiHelper (API-level preconditions & state checks, e.g. limits)

src/test/java/com/scf/
  Diagnostic.java  standalone browser-launch smoke check (no TestNG/surefire)
  base/         BaseTest (per-test browser, loginAs(actor, role), failure
                screenshots/traces), RetryAnalyzer, RetryTransformer
  pages/        BasePage + LoginPage + ApprovalQueuePage (shared checker screen)
    systemadmin/  bank/maker/  bank/checker/
    anchor/maker/ anchor/checker/
    dealer/maker/ dealer/checker/
  tests/        mirrors pages/ — per actor: *ValidationsTest + *FlowTest
    e2e/        EndToEndOnboardingAndLoanCycleTest (9-step journey)

src/test/resources/
  config.properties      non-secret defaults (browser, timeouts); creds live in .env
  suites/*.xml           TestNG suites (full, validations, flows, per-actor, e2e,
                         and single-feature *-smoke.xml)
```

Each actor package has two test categories:

- **`*ValidationsTest`** — negative/UI: mandatory fields, formats, boundaries on
  financial fields (zero/negative/over-limit/decimal precision), duplicates,
  maker/checker segregation, tenant isolation, sessions.
- **`*FlowTest`** — positive/business: maker submits → Pending Approval →
  checker approves/rejects → status and limit assertions.

## Setup

1. **Java 21** (installed) and **Maven**. If `mvn` isn't on your PATH, a no-sudo
   install works: unpack Maven under `~/tools/` and symlink it into `~/.local/bin/mvn`.
2. Copy `.env.example` to `.env` and fill in the platform URL and credentials
   (Emtech admin, Faulu maker/checker; Anchor/Dealer once provisioned). `.env`
   is gitignored — never commit real passwords. All actors share `APP_URL`;
   per-actor `*_URL` overrides are optional. Resolution order:
   `-D` system property > shell env var > `.env` > `config.properties`.
3. Download the browser once (cached in `~/.cache/ms-playwright`):
   ```bash
   mvn compile exec:java -Dexec.args="install chromium"
   ```

## Running tests

```bash
# Full regression (default suite: parallel actor suites, then E2E)
mvn test

# By category
mvn test -DsuiteXml=src/test/resources/suites/testng-validations.xml
mvn test -DsuiteXml=src/test/resources/suites/testng-flows.xml

# Single actor
mvn test -DsuiteXml=src/test/resources/suites/testng-bank.xml   # anchor/dealer/systemadmin likewise

# Single feature (smoke) — verified flows
mvn test -DsuiteXml=src/test/resources/suites/testng-anchor-smoke.xml         # Faulu Maker creates an anchor
mvn test -DsuiteXml=src/test/resources/suites/testng-anchor-invite-smoke.xml  # Anchor Maker recommends a dealer

# Full E2E journey only
mvn test -DsuiteXml=src/test/resources/suites/testng-e2e.xml

# Headed + slow-motion for debugging
mvn test -Dheadless=false -Dslowmo.ms=250 -DsuiteXml=src/test/resources/suites/testng-anchor-smoke.xml

# Report
mvn allure:serve
```

> Note: to run one class, use a dedicated `*-smoke.xml` suite. `-Dtest=...` is
> ignored because the pom sets `suiteXmlFiles`, and `-DsuiteXml=` (empty) errors.

## The codegen loop (how we verify a feature)

Page objects for unverified features hold best-guess locators. To make a
feature's test trustworthy, record the real flow and paste the output back in:

```bash
mvn compile exec:java -Dexec.args="codegen https://dealerfinance.emtechhouse.co.ke/login"
```

Click through the feature (login → nav → open/fill form → submit or, for
checkers, View → Update Status → Approve/Reject), note the success toast and
before/after status, then translate the generated Java into the page object.
Prefer stable locators: e.g. the left-nav buttons are `button.side-buttons`
with the section text (`manage_accounts Anchors`, `group Dealers`).

## Diagnosing failures

On any failure, `BaseTest` attaches a **full-page screenshot** to the Allure
results and writes a Playwright **trace**:

```bash
# See the exact UI at failure (PNG attachment)
ls target/allure-results/*-attachment      # copy one out and open it

# Step-by-step trace
mvn exec:java -Dexec.args="show-trace traces/<test>-trace.zip"

# Concise stack/assertion
cat target/surefire-reports/*.txt
```

Reading the screenshot is the fastest way to diagnose headless runs (it's how
Defect #2's "Something went wrong" toast was confirmed).

`Diagnostic.java` isolates browser bring-up from TestNG if a run hangs before any test:
```bash
mvn -q test-compile exec:java -Dexec.mainClass=com.scf.Diagnostic -Dexec.classpathScope=test
```

## Environment notes (Kali / Linux)

Baked into `PlaywrightFactory` / `BaseTest` so runs work out of the box:

- **`PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1`** on `Playwright.create()` — Playwright-Java
  otherwise re-verifies browsers on every launch, which hangs here. (Install
  browsers once with the command in Setup.)
- **`--no-sandbox --disable-dev-shm-usage`** Chromium launch args.
- Navigation waits for **`domcontentloaded`**, not `load` — this SPA's background
  requests keep the `load` event from firing in time.

## Conventions

- **Groups**: every test carries `{actor}` + `{maker|checker}` + `{validation|flow}`
  (e.g. `groups = {"bank", "maker", "validation"}`), so any slice runs via a `<groups>` filter.
- **Test independence**: `TestDataFactory` gives every run unique entity data
  (yopmail emails for Anchor/Dealer so invitation/credential emails can be read later).
- **Locators**: verified features use real app locators; unverified ones still
  carry guesses — run the codegen loop above before trusting their results.
- **E2E** is strictly sequential (`dependsOnMethods`) and must not run in a parallel block.

## Next steps

- [ ] Wire remaining features' locators via the codegen loop (checkers next)
- [ ] Report Defects #1 and #2 to the dev team (screenshots in `target/allure-results/`)
- [ ] Rename OfferLetter → Program concepts to match the app
- [ ] Implement the `SkipException` stubs (each names its Product Guide section)
- [ ] yopmail mailbox helper + forced-password-reset login handling
- [ ] Repayment batch-upload page object (loanId + totalRepayableAmount template)
- [ ] Wire `ApiHelper` to the real API for limit/status assertions
- [ ] CI pipeline (validations + flows on PR, full E2E nightly)
```
