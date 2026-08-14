# Version 1.0.1 — Fee-first navigation flow

Working branch: `feature/new-navigation-flow` (base `da4a8d3`)
Current `versionName` in `app/build.gradle.kts` is still `1.0.0` — bump to `1.0.1` before release.

This document is the single source of truth for the 1.0.1 change. It was reconstructed
after a session loss; it carries the agreed design, what has already been implemented,
and what is still pending.

---

## 1. Goal

Force a **fee-first journey**: the broker cannot reach Proposals or Configuration until a
consumption study exists *and* their current fee conditions are complete. Design spec lives
in `navigation-flow-fee-first.pen` (7 flow nodes + 9 phone mockups + navigation map).

```
Login
  → Fetch consumption        (REQUIRED: A = Scan CUPS   B = Upload PDF bill)
  → Current conditions       (fee fields mandatory)
        GATE: Proposals + Configuration LOCKED
  → Fee source               (A = keep bill values   B = copy from proposal)
  → Fee complete             → navigation unlocked
  → Proposals / Configuration
```

### Gate rules (from the `.pen` navigation map)

| Rule | Statement |
|---|---|
| R1 | Start destination is always *Current conditions*. |
| R2 | With no consumption fetched, *Current conditions* shows the empty state and only offers Scan CUPS / Upload PDF. |
| R3 | Proposals and Configuration stay locked until **all** power and energy periods have a valid value greater than zero. |
| R4 | Extra services are **optional** — they do not block the gate (broker's choice). |
| R5 | Landing on a locked route (deep link, state restoration) redirects to *Current conditions*. The guard is re-checked on every state change. |
| R6 | Fetching a new consumption clears conditions and proposals — the gate closes again. |
| R7 | *(amendment agreed in session, not in the original `.pen`)* The scan shortcut appears **only** on the *Consumo requerido* screen, not on every top bar. The drawer entry `Escanear CUPS` is renamed **"Nuevo estudio de propuestas"** and routes to *Fetch consumption*, driving the user into the flow. |

### UX decisions taken during design

1. **Progress affordance** — "2 de 5 campos completados" with a red→green bar, so the lock does not feel arbitrary.
2. **The lock is visible, not hidden** — locked drawer items stay in the menu, greyed with a padlock plus an explanatory note, rather than disappearing.
3. **Extra services labelled `(opcional)`** and excluded from the field count.
4. **Proposals screen keeps its current table**; the "Mejor opción: …" card is added **at the bottom**.

---

## 2. Architecture reset

`AGENTS.md` was rewritten against the official Google Guide to App Architecture and is now
the binding ruleset for this and future changes. Material changes vs. the previous codebase:

- One ViewModel **per screen** — no shared god-ViewModel; shared data comes from a repository (SSOT).
- One immutable `…UiState` per screen, in its own file, `stateIn` + `SharingStarted.WhileSubscribed(5_000)`.
- `collectAsStateWithLifecycle()` instead of `collectAsState()`.
- ViewModels may not touch `Context` / `Toast` / `File`.
- Type-safe routes, a single `NavHost`; screens never receive a `NavController`.
- No global `object` event buses for app state.
- Injected dispatchers; DataStore preferred over SharedPreferences for new state.

---

## 3. Implemented (uncommitted, in the working tree)

**Data / domain**
- `ConsumptionSessionRepository` — SSOT for the active study. This is the keystone that made per-screen ViewModels possible.
- `ConsumptionStudyRepository` — application-scoped submit → poll → result pipeline.
  **Behaviour fix:** polling used to die when navigating away from the screen; it now survives.
- `PriceUpdatesEventBus` global object → injected `PriceUpdatesNotifier`.
- `CrashReporter` / `CrashErrorCategory` and `ProposalCalculationHelper` moved to `:domain`.
- Pricing math centralised in `CalculateComparatorSummaryUseCase`.

**UI**
- **`ComparatorViewModel` (723 lines, 18 `StateFlow`s) deleted**, split into `AppShellViewModel`,
  `FetchConsumptionViewModel`, `CurrentUserConditionsViewModel`, `ProposalsViewModel`, `ConfigurationViewModel`.
- `ui/navigation/BmAppRoute.kt` — type-safe routes, each carrying its `requiredStage`.
- `ui/navigation/BmAppNavHost.kt` — gate enforced in one `LaunchedEffect`, redirects on any state change (R5).
- `MainStructureView`'s 25-parameter hoisting and the `ComparatorDestination` enum are gone.
- New screens: `FetchConsumptionScreen`, `ConfigurationScreen`, `ProposalsScreen`, `FeeFirstGateBanner`.
- Drawer gating with padlocks + explanatory note; `TopActionBar` no longer takes a `Context`.
- 6 new hand-built `ImageVector`s in `ui/components/Icons.kt`: `LockIcon`, `CheckCircleIcon`, `ScanIcon`,
  `FileUploadIcon`, `InfoIcon`, `ChevronRightIcon`. **No new icon dependency added** (convention preserved).
- ~14 new strings, English in `values/` and Spanish in `values-es/`.

**Fixes made after the first device run**
- Mixed-language bug on the fetch screen: the legacy Spanish key `current_user_conditions_empty_state_message`
  was reused; replaced with a proper `fetch_consumption_headline` in EN + ES.
- Drawer lock note was wrong at `CONSUMPTION_REQUIRED` stage (said "complete the prices" when the blocker
  was the missing study) → stage-aware `drawer_locked_hint_consumption`.
- Drawer selected-item highlight was default M3 lavender → now `extendedColors.sectionHighlight`.

**Build state at cut-off:** `app-dev-debug.apk` builds; domain unit tests 7/7 green.

---

## 4. Verified on device (emulator Pixel_9, local backend)

Full flow driven end to end against the local backend with a real Iberdrola 2.0TD bill.

- **DI graph resolves** — no Koin errors; now also covered by an automated test (P2).
- **R1 / R2** — lands on *Fetch consumption*; only *Scan CUPS* / *Upload bill PDF* offered.
- **R7** — scan icon in the top bar only on that screen.
- **New icons render** correctly.
- **Drawer gating** — padlocks on the three blocked destinations, red stage-aware note,
  light-blue selection highlight.
- **Study runs** — PDF uploaded, job polled to completion, result applied.
- **Stage advance** — after the study the app moves to *Current conditions*, prefilled with
  the real customer data (holder, address, CUPS) from the bill.
- **R3 / R4** — progress counter increments live (1/5 → 3/5 → 5/5); banner flips from red
  "Proposals locked" to green "Navigation unlocked"; extra services left empty and did **not**
  block the unlock.
- **Proposals** — existing table unchanged, "Best option" card at the bottom.
- **R6** — "New proposal study" clears the study and the typed prices, and the gate re-closes
  back to *Fetch consumption*.
- **State restoration** — study and unlocked stage survive an app restart.

---

## 5. Pending

### P1 — Manual gate QA — **done**
Driven on the emulator against the local backend (see section 4). Steps 1-5 and 7-9 all pass.
Step 6 (*Copy from proposal* as an alternative to typing) is the only one not exercised.

> Open question on R6: it wipes prices the broker already typed, with no confirmation dialog.
> The design encodes this as-is. **Decide whether 1.0.1 should add a confirm dialog.**

### P2 — Koin verification test — **done**
`app/src/test/java/…/di/KoinModulesTest.kt` resolves every definition in `dataModule + appModule`
at runtime, with the platform singletons (Firebase, `Context`) stubbed. Proven to fail when a
binding is removed, so it is a real check rather than a no-op. `checkModules()` is deprecated in
Koin 4 but `verify()` cannot handle this graph (`buildRepository(...)` and interface-returning
factories defeat reflection).

### P3 — `BestProposalCard` placement — **done**
Verified on device: table keeps `weight(1f)`, card sits below it.

### P4 — `navigation-compose` 2.8.4 → 2.8.5 — **done**
The proxy `407` is gone; bumped and building.

### P5 — `versionName` `1.0.1` — **done**
`app/build.gradle.kts:60`.

### P6 — Commit and open the PR
Still outstanding. The change is uncommitted apart from one staged file
(`domain/…/monitoring/CrashReporter.kt`). Review staging before committing.

---

## 5b. Defects found during QA and fixed

### The gate never pushed the broker forward *(blocker)*
`FeeFirstStage.canReach` was `ordinal >= route.requiredStage.ordinal`, so a *later* stage always
satisfied an *earlier* requirement. `FetchConsumption` therefore stayed "reachable" after a study
was loaded, and the NavHost guard — which only redirects away from **blocked** routes — had no
reason to move. Result: the study completed successfully (HTTP 200, result applied) but the UI
sat on the "Start by loading the bill" empty state forever, with no error. The flow was unusable
past step 2.

Fix: routes now declare `allowedStages: Set<FeeFirstStage>` (an exact set, not a minimum), so
leaving a stage invalidates the screen that belonged to it and the existing guard performs the
forward move as well as the backward one.

### R6 was never wired
"New proposal study" only called `navController.navigate(FetchConsumption)`; nothing reset the
session, so the gate stayed open. With the route fix that entry would have bounced straight back.
It now calls `AppShellViewModel.startNewStudy()`, which clears the session *and* the previous
customer's prices; navigation follows from the stage change. The drawer entry is marked
non-gated so it stays available at every stage.

### R6 left the previous job persisted *(found in review)*
`startNewStudy()` cleared the session and the typed prices, but not `lastCompletedJobId`.
`ConsumptionStudyRepositoryImpl.resetActiveStudy()` — what a *real* study start does — clears
three things; the R6 path only replicated two. The gate closed correctly, but on the next launch
`restoreLastCompletedStudy()` re-fetched the old job and the previous customer's study and
proposals came back.

Root cause was the duplication itself, so the fix removes it: `ConsumptionStudyRepository` now
exposes `discardActiveStudy()` (cancels any in-flight study, then runs the same
`resetActiveStudy()`), and `AppShellViewModel` just delegates to it. The reset now has one
owner, so "what belongs to a job" cannot drift between the two paths again.

### Missing currency symbol
"Estimated saving of 7.11 per year" → `%1$s\u00A0€` in both `values/` and `values-es/`.

---

## 6. Known issues deliberately left out of scope

- **`PriceTableApiTest` fails** (2 tests, coroutine timeout). Verified by stashing all work and
  re-running: it fails identically on the pristine baseline. Pre-existing, untouched.
- **Legacy localisation debt.** ~25 older strings are Spanish sitting in the default `values/`
  file, with `values-es/` only translating the login block. On an English-locale device the app
  reads as half-translated wherever those keys appear. All new 1.0.1 strings are correct in both
  files. A mass fix belongs in its own change.
- **`CupsScannerView` is hardcoded Spanish** ("Volver", "Confirmar"). Same class of debt.
- **FCM `SERVICE_NOT_AVAILABLE`** on the emulator is push-token registration needing full Play
  Services. It does not affect login and is pre-existing.

## 7. Security follow-ups (outside the code)

- Revoke the Figma `figd_` personal access token generated during the lost session — it is live
  with `file_content:read` on the Volvo account and was printed in plaintext.
- Rotate `systemProp.https.proxyPassword` in `~/.gradle/gradle.properties` — also printed in plaintext.
