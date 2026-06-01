# CUPS OCR Screen - Implementation Notes

## 1) Objective

Implement a new camera-based OCR flow so users can scan a CUPS code from an electricity bill and fetch price proposals through the same async job pipeline already used by the PDF upload flow.

Business goal:
- Reduce friction vs. PDF-only flow.
- Keep user trust with real-time visual confirmation of detected CUPS.
- Reuse existing proposal generation backend process to avoid duplicating pricing logic.

---

## 2) Functional Scope Delivered

- New top action CTA behavior:
  - Short button label (`Comparar`).
  - Opens bottom sheet with:
    - `Seleccionar una factura`
    - `Escanear CUPS`
- New scanner screen:
  - Camera preview (CameraX).
  - OCR on live frames (ML Kit Text Recognition).
  - Real-time detected CUPS display.
  - Confirm action enabled when a valid CUPS is detected.
- New app-to-backend flow for CUPS:
  - Submit CUPS as async job.
  - Poll status.
  - Fetch result.
  - Render proposals using existing UI/state flow.
- Backend endpoint to process CUPS code and return job-based response.

---

## 3) UX Decisions

### 3.1 Entry Point

Decision:
- Replace direct PDF picker click with a 2-option chooser (bottom sheet).

Reason:
- Keeps existing PDF path intact while introducing scan path without adding clutter.

### 3.2 Real-Time Confirmation

Decision:
- Show detected CUPS in real-time and let user explicitly confirm.

Reason:
- Increases confidence and reduces false positives.
- Matches proven fintech/document-scanning UX patterns.

### 3.3 Navigation Pattern

Decision:
- Follow existing app architecture (state-based destination enum), no NavHost migration.

Reason:
- Minimal change, lower risk, consistent with current implementation.

---

## 4) OCR & Validation Decisions

### 4.1 OCR Engine

Chosen:
- Google ML Kit Text Recognition (Play Services version).

Why:
- Good Latin-script real-time performance.
- Tight Android integration.
- On-device processing.

### 4.2 Camera Stack

Chosen:
- CameraX (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`).

Why:
- Lifecycle-aware and stable for Compose + AndroidView embedding.

### 4.3 CUPS Parsing Rules

Parser implemented in `CupsCodeParser`:
- Normalize: uppercase + keep alphanumeric only.
- Final validity regex: `^ES[A-Z0-9]{18}([A-Z0-9]{2})?$`
  - Supports total length 20 or 22 chars.

Business rationale:
- Enforces required CUPS structure while tolerating OCR spacing/hyphen noise.

---

## 5) Backend Decisions

### 5.1 Endpoint Added

`POST /fetch-user-consumption-report-by-cups`

Payload:
```json
{
  "cupsCode": "ES..."
}
```

Behavior:
- Validates CUPS format.
- Creates async job.
- Processes in background.
- Exposes results through existing status/result endpoints.

### 5.2 Processing Strategy

Decision:
- For CUPS flow, call the existing n8n webhook pipeline using `DoclingExtractedData(cups_code=...)` shape.

Reason:
- Reuses already integrated consumption/proposal generation pipeline.
- Avoids introducing a second pricing engine path.

### 5.3 Job Reuse / Freshness

Clarification aligned with latest discussion:
- App persistence of last completed job remains enabled (for resume/background reliability).
- Backend still computes via job execution per submission path.
- If stricter "always recalculate from latest sources on every request" backend policy is required, that should be enforced explicitly in backend service rules (next iteration).

---

## 6) App Architecture Decisions

### 6.1 Clean Architecture Integration

Added new use case instead of overloading existing PDF use case:
- `SubmitConsumptionReportByCupsJobUseCase`

Why:
- Keeps intent clear.
- Preserves SRP across use cases.

### 6.2 ViewModel Integration

Added:
- `uploadConsumptionReportByCups(cupsCode: String)`

Behavior mirrors PDF path:
- set loading states
- submit job
- poll status
- fetch results
- map into existing proposal/customer state

### 6.3 Persistence Behavior

Final state:
- App-side persistence/restoration of last completed job is active (as requested).

---

## 7) Files Added/Updated

### BmApp

Added:
- `app/src/main/java/com/briel/marnisos/brielapp/ui/views/scanner/CupsScannerView.kt`
- `app/src/main/java/com/briel/marnisos/brielapp/ui/views/scanner/CupsCodeParser.kt`
- `data/src/main/java/com/briel/marnisos/brielapp/data/model/prices/FetchConsumptionReportByCupsRequest.kt`
- `domain/src/main/java/com/briel/marnisos/brielapp/domain/usecases/SubmitConsumptionReportByCupsJobUseCase.kt`
- `data/src/main/java/com/briel/marnisos/brielapp/data/usecases/SubmitConsumptionReportByCupsJobUseCase.kt`

Updated:
- `app/src/main/java/com/briel/marnisos/brielapp/ui/views/common/SelectFileButton.kt`
- `app/src/main/java/com/briel/marnisos/brielapp/ui/views/common/TopActionBar.kt`
- `app/src/main/java/com/briel/marnisos/brielapp/ui/views/MainStructureView.kt`
- `app/src/main/java/com/briel/marnisos/brielapp/ui/views/drawer/DrawerContent.kt`
- `app/src/main/java/com/briel/marnisos/brielapp/ui/models/ComparatorDestination.kt`
- `app/src/main/java/com/briel/marnisos/brielapp/ui/views/pricetable/ComparatorViewModel.kt`
- `app/src/main/java/com/briel/marnisos/brielapp/di/AppModule.kt`
- `data/src/main/java/com/briel/marnisos/brielapp/data/di/DataModule.kt`
- `data/src/main/java/com/briel/marnisos/brielapp/data/network/PriceTableApi.kt`
- `data/src/main/java/com/briel/marnisos/brielapp/data/repository/Repository.kt`
- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`

### Backend

Added:
- `src/main/kotlin/com/bm/backend/models/FetchConsumptionReportByCupsRequest.kt`

Updated:
- `src/main/kotlin/com/bm/backend/routes/UserConsumptionRoutes.kt`
- `src/main/kotlin/com/bm/backend/services/UserConsumptionService.kt`

---

## 8) Dependency & Permission Changes

Added app dependencies:
- CameraX 1.4.2
- ML Kit text recognition (Play Services) 19.0.1

Added manifest permission:
- `android.permission.CAMERA`

---

## 9) Validation & Build Checks Performed

BmApp:
- `./gradlew :app:compileProdDebugKotlin`
- `./gradlew :app:testProdDebugUnitTest`

Backend:
- `./gradlew compileKotlin`
- `./gradlew test`

All commands passed successfully during implementation.

---

## 10) Risks / Follow-Ups

1. n8n contract dependency:
   - CUPS endpoint expects n8n to handle `cups_code`-driven requests correctly.
2. OCR false positives on low-quality frames:
   - Current parser is robust to separators but still depends on image quality.
3. Strict backend freshness policy:
   - If required, enforce explicit "no stale reuse" rules in backend job/result lifecycle.

---

## 11) Design Artifact

UX flow mockups created in:
- `pencil-new.pen`

Includes:
- main screen CTA update
- bottom sheet options
- scanner screen
- loading/fetching state
