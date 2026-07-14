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

## Tech stack

| Piece | Choice |
|---|---|
| Browser automation | Playwright for Java |
| Test runner | TestNG (groups, retries via `RetryTransformer`, parallel by actor) |
| Build | Maven (Java 21) |
| Design pattern | Page Object Model, one package per actor portal area |
| Reporting | Allure (`mvn allure:serve`) with failure screenshots + Playwright traces |
| Config | `src/test/resources/config.properties`, overridable by `-D` system properties or env vars |

## Folder structure

```
src/main/java/com/scf/framework/     ← reusable framework (no test code)
  config/       ConfigReader (properties + -D + env-var resolution)
  driver/       PlaywrightFactory (thread-safe browser lifecycle, tracing)
  enums/        Actor (SYSTEM_ADMIN/BANK/ANCHOR/DEALER), Role (MAKER/CHECKER)
  models/       Bank, Anchor, Dealer, OfferLetter, LoanRequest records
  utils/        TestDataFactory (unique, re-runnable entities)
  api/          ApiHelper (API-level preconditions & state checks, e.g. limits)

src/test/java/com/scf/
  base/         BaseTest (per-test browser, loginAs(actor, role), failure
                screenshots/traces), RetryAnalyzer, RetryTransformer
  pages/        BasePage + LoginPage + ApprovalQueuePage (shared checker screen)
    systemadmin/  bank/maker/  bank/checker/
    anchor/maker/ anchor/checker/
    dealer/maker/ dealer/checker/
  tests/        mirrors pages/ — per actor: *ValidationsTest + *FlowTest
    e2e/        EndToEndOnboardingAndLoanCycleTest (9-step journey)

src/test/resources/
  config.properties      environment URLs + credentials (placeholders!)
  suites/*.xml           TestNG suites (full, validations, flows, per-actor, e2e)
```

Each actor package has two test categories:

- **`*ValidationsTest`** — negative/UI: mandatory fields, formats, boundaries on
  financial fields (zero/negative/over-limit/decimal precision), duplicates,
  maker/checker segregation, tenant isolation, sessions.
- **`*FlowTest`** — positive/business: maker submits → Pending Approval →
  checker approves/rejects → status and limit assertions.

One test per class is fully implemented to establish the pattern; the rest are
stubs throwing `SkipException` with a TODO referencing the Product Guide
section — they show up as *skipped* in reports so remaining coverage is visible.

## Setup

1. **Java 21** (installed) and **Maven**: `sudo apt install maven`
2. Copy `.env.example` to `.env` and fill in the platform URL and credentials
   (Emtech admin, Faulu maker/checker; Anchor/Dealer once provisioned).
   `.env` is gitignored — never commit real passwords. Resolution order:
   `-D` system property > shell env var > `.env` > `config.properties`.
3. Download browsers once:
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
mvn test -DsuiteXml=src/test/resources/suites/testng-bank.xml      # anchor/dealer/systemadmin likewise

# Full E2E journey only
mvn test -DsuiteXml=src/test/resources/suites/testng-e2e.xml

# Headed + slow-motion for debugging
mvn test -Dheadless=false -Dslowmo.ms=250 -DsuiteXml=src/test/resources/suites/testng-e2e.xml

# Report
mvn allure:serve
```

Failed tests attach a full-page screenshot to Allure and write a Playwright
trace to `traces/` — open with:
```bash
mvn exec:java -Dexec.args="show-trace traces/<test>-trace.zip"
```

## Conventions

- **Groups**: every test carries `{actor}` + `{maker|checker}` + `{validation|flow}`
  (e.g. `groups = {"bank", "maker", "validation"}`), so any slice can be run
  with a `<groups>` filter.
- **Test independence**: `TestDataFactory` gives every run unique entity names/
  registration numbers, so suites are re-runnable without cleanup.
- **Locators are placeholders**: page objects use role/label-based locators
  guessed from the Product Guide. Verify against the real app with
  `mvn compile exec:java -Dexec.args="codegen https://<portal-url>"` and adjust.
- **E2E** is strictly sequential (`dependsOnMethods`) and must not run in a
  parallel test block.

## Next steps

- [ ] Fill in real portal URLs and QA credentials
- [ ] Walk each page object against the live app and fix locators
- [ ] Implement the `SkipException` stubs (each names its Product Guide section)
- [ ] Wire `ApiHelper` to the platform's real API for limit/status assertions
- [ ] Add a mailbox helper if dealer-invite email links are in scope
- [ ] CI pipeline (run validations + flows on PR, full E2E nightly)
