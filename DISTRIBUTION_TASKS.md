# BmApp Distribution — Task List

Multi-flavor (`local` / `dev` / `prod`) build, distribution, and signing setup. Work top-down; each task is self-contained.

---

## ✅ Done

- [x] Three product flavors (`local`, `dev`, `prod`) defined in `app`, `data`, `domain` modules.
- [x] Per-flavor `applicationIdSuffix` (`.local`, `.dev`) so all three install side-by-side.
- [x] Per-flavor `API_BASE_URL` in `BuildConfig` (data module).
- [x] Firebase apps registered: `PowerApp` (`com.briel.marnisos.brielapp`) + `PowerApp Dev` (`com.briel.marnisos.brielapp.dev`).
- [x] `app/google-services.json` updated with both prod + dev clients.
- [x] CI workflow `firebase-distribution.yml` builds `:app:assembleDevDebug` and uploads to Firebase App Distribution (group `internal-testers`).
- [x] Backend verified: no hardcoded Android app id references; FCM Admin SDK is project-scoped only.

---

## Task 1 — Verify FCM works on both `dev` and `prod` builds ✅ DONE

FCM is already wired (`BrielFirebaseMessagingService` exists, both Firebase apps registered, both clients present in `google-services.json`). This task is **verification only** — no code changes expected.

### FCM behavior per flavor

| Flavor | Firebase app | FCM at runtime | Crashlytics | Analytics |
|---|---|---|---|---|
| `prod` | `PowerApp` (registered) | ✅ Live, receives pushes | ✅ On (release builds only) | ✅ Live |
| `dev` | `PowerApp Dev` (registered) | ✅ Live, receives pushes | ❌ Off (gated to prodRelease) | ✅ Live |
| `local` | **Stub** (`app/src/local/google-services.json`, not registered in Firebase) | ⚠️ **Inert** — token requests fail silently against the prod app id; pushes will not arrive | ❌ Off | ⚠️ Events rejected |

The `local` flavor's `google-services.json` is a placeholder whose only purpose is to satisfy the Google Services plugin's package-name check. It points at the prod Firebase app id, but since the running APK has package `com.briel.marnisos.brielapp.local`, Firebase rejects FCM/Analytics traffic from it. This is intentional — `local` is for emulator development against a localhost backend; FCM isn't needed there.

If you ever need real FCM on `local` builds, register a third Firebase Android app for `com.briel.marnisos.brielapp.local` and replace the stub with a real config (or extend the root `google-services.json` to include all three clients).

### 1.1 — Install both flavors side-by-side on a device
```bash
./gradlew :app:installDevDebug :app:installProdDebug
```

### 1.2 — Verify each app reports the correct Firebase app id at runtime
Launch each app, then read its FCM token from logcat:
```bash
adb logcat -s "BrielFirebaseMessagingService:*" "FirebaseMessaging:*" "BrielApp:*" | head -50
```
Expected: token line is logged once per flavor. Each token is bound to a different Firebase app id (`…b7cf73f4…` for prod, `…0b3d49a9…` for dev).

### 1.3 — Send a test push from Firebase console
For each app (`PowerApp` and `PowerApp Dev`):
- Firebase console → Cloud Messaging → "Send your first message" → use the FCM token captured in 1.2 → send.
- Confirm both devices receive the notification.

### 1.4 — Verify backend topic broadcast reaches both
Trigger a price update from the Web admin (which calls the backend, which fires `price_updates` topic). Both dev and prod builds (subscribed to that topic) should receive it because both Firebase apps live in the same project (`brielmarnysos-1dc68`) and topic subscriptions are project-scoped.

If a build does **not** receive notifications, check that it subscribes to the topic on startup. Grep:
```bash
grep -rn "subscribeToTopic" /Users/victor/Documents/Personal/Projects/BM/BmApp
```

### 1.5 — (Optional) Re-test after first prod release
The original prod Firebase app was deleted and recreated, so any existing prod token on a real device is stale. Tokens auto-refresh on next launch — no action needed unless a specific tester reports silence.

---

## Task 2 — Automatic `versionCode` for Play Store releases ✅ DONE

Play Console requires a **strictly increasing** `versionCode` per upload. Manual bumping is error-prone. Recommendation: derive it from CI.

### 2.1 — Pick a strategy (choose one)
- **A. Git commit count** (`git rev-list --count HEAD`): deterministic, monotonic, no state needed. **Recommended.**
- **B. GitHub Actions run number** (`${{ github.run_number }}`): also monotonic, but resets if you migrate CI providers.
- **C. Timestamp** (`yyMMddHHmm` as int): always grows, but big jumps look ugly in Play Console.

### 2.2 — Wire it in `app/build.gradle.kts`
Read `versionCode` from env var with sane fallback for local builds. (I'll implement when you confirm strategy.)
```kotlin
val ciVersionCode = System.getenv("APP_VERSION_CODE")?.toIntOrNull() ?: 1
// defaultConfig { versionCode = ciVersionCode }
```

### 2.3 — Produce the value in CI
For strategy A (recommended):
```bash
echo "APP_VERSION_CODE=$(git rev-list --count HEAD)" >> "$GITHUB_ENV"
```

### 2.4 — `versionName` policy
Keep `versionName` semantic (`1.0.0`, `1.1.0`, …) and bump manually for user-facing releases. The `-dev` / `-local` suffix is already appended via `versionNameSuffix`.

### 2.5 — Guardrail
Add a CI check that the computed `versionCode` is greater than the last one published to Play. Easiest: read it from Play via the Google Play Developer API in a pre-upload step, or trust the monotonic source.

---

## Task 3 — `prodRelease` signing config ✅ DONE

Required before any Play Store upload. Blocked on inputs from you.

### 3.1 — Inputs needed
- Absolute path to your release `.jks`.
- Key alias inside the JKS.
- Preferred secret storage: `keystore.properties` (local), env vars (CI), or both (recommended).

### 3.2 — Wiring (after inputs received)
- Add `signingConfigs.release` in `app/build.gradle.kts`, reading from env vars with fallback to `keystore.properties`.
- Apply to `buildTypes.release { signingConfig = signingConfigs.getByName("release") }`.
- Add `keystore.properties` to `.gitignore` (verify).
- Document the env vars in this file once added.

### 3.3 — Get SHA fingerprints for Firebase
Replace placeholders:
```bash
keytool -list -v -keystore /path/to/your-release.jks -alias your-key-alias
```
Copy the `SHA1` and `SHA256` lines.

Add SHA-1 to **both** Firebase apps if you plan to install release-signed APKs of either flavor (only `prod` will be release-signed in this setup, so add it to `PowerApp` only):
- Firebase console → Project settings → `PowerApp` → "Add fingerprint" → paste SHA-1.

For the debug keystore (used by `localDebug` and `devDebug` on local machine):
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```
Add that SHA-1 to `PowerApp Dev` if you ever need Google Sign-In or other SHA-gated services on dev builds.

---

## Task 4 — Google Play upload workflow (CI)

Optional but recommended once Task 2 and Task 3 are done.

### 4.1 — Service account
- Google Play Console → Setup → API access → link a Google Cloud project → create a service account with "Release manager" role.
- Download the service account JSON.
- Store its contents as GitHub secret `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON`.

### 4.2 — Workflow file
Add `.github/workflows/play-distribution.yml`:
- Trigger: `workflow_dispatch` (manual) + optionally `push` on tags matching `v*`.
- Steps: checkout → setup JDK 17 → setup Android SDK → set `APP_VERSION_CODE` → `./gradlew :app:bundleProdRelease` → upload via `r0adkll/upload-google-play@v1` to the `internal` track.

### 4.3 — Promote between tracks
Internal → Closed (alpha/beta) → Open (production) is done in the Play Console UI. Don't fully automate prod promotion; keep it human-gated.

---

## Task 5 — Backend environment per flavor 🅿️ DEFERRED

Will be addressed during Docker containerization rollout (`MULTI_ENV_DEPLOYMENT.md` Phases 1–2).

Current state:
- `prod` → `http://217.154.181.175:8081/api/v1` ✅
- `dev` → `http://217.154.181.175:8081/api/v1` ⚠️ **TEMP, points at prod** (QA backend on 9081 not deployed yet)
- `local` → `http://10.0.2.2:8081/api/v1` ✅

### 5.1 — Deploy QA backend (deferred)
Follow `MULTI_ENV_DEPLOYMENT.md` Phase 1 (local Docker Compose) and Phase 2 (VPS rollout). Verify with:
```bash
curl -fsS http://217.154.181.175:9081/health
```

### 5.2 — Switch dev flavor to QA backend (deferred)
Once 9081 is healthy, change `data/build.gradle.kts` `dev` flavor `API_BASE_URL` back to `http://217.154.181.175:9081/api/v1` and remove the `TEMP` comment.

---

## Task 6 — (Nice-to-have) Distinct app icon for `dev` builds

Lets testers visually distinguish dev from prod on the same device.

### 6.1 — Add a flavor-specific icon
Place a tinted variant under `app/src/dev/res/mipmap-*/` and a custom `app/src/dev/res/values/strings.xml` overriding `app_name` to e.g. `Briel Dev`.

(Optional — flag if you want me to wire it.)

---

## Task 7 — (Nice-to-have) Stable CI debug keystore

If Google Sign-In or any SHA-gated Firebase service is used on `devDebug` builds distributed via Firebase App Distribution, the CI runner's auto-generated debug keystore breaks SHA-1 matching every run.

Fix: commit an **encrypted** shared debug keystore (or store base64'd in a secret) and have the workflow decode it before build. Skip unless an issue arises.

---

## Task 8 — Documentation

Once Tasks 1–4 are validated, update:
- `BmApp/AGENTS.md` — add a short "Build flavors" section referencing this file.
- Root `MULTI_ENV_DEPLOYMENT.md` — note that the Android app's `dev` flavor talks to the QA backend.

---

## Quick command reference

```bash
# Install all three side-by-side
./gradlew :app:installLocalDebug :app:installDevDebug :app:installProdDebug

# Build dev APK for manual Firebase upload (CI does this automatically on push to main)
./gradlew :app:assembleDevDebug

# Build prod AAB for Play Store
./gradlew :app:bundleProdRelease

# List flavor-specific tasks
./gradlew :app:tasks --all | grep -E "(Dev|Prod|Local)"
```
