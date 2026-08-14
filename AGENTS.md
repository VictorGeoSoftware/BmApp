# BmApp Project Instructions

## Architecture

BmApp follows the **official Google Guide to App Architecture**
(https://developer.android.com/topic/architecture). Where this document and older
code disagree, this document wins — migrate the code, don't copy the old pattern.

### Layers

```
ui (app module)  ->  domain  ->  data
```

- **UI layer** (`:app`) — Compose UI elements + state holders (ViewModels). Renders
  state, forwards user events. Never touches DTOs, Ktor, or SharedPreferences directly.
- **Domain layer** (`:domain`) — Use cases and domain models. Pure Kotlin. No Android
  framework types, no `Context`, no Compose.
- **Data layer** (`:data`) — Repositories (the single source of truth per data type)
  and data sources (network, local). Owns DTOs and mapping.

Dependencies point inward only. `:domain` must not know `:data` or `:app`.

### The five principles

1. **Separation of concerns** — UI code does UI. Business rules live in the domain
   layer, not in composables and not in ViewModels.
2. **Drive the UI from data models** — preferably from a repository-backed flow, so
   state survives configuration changes and process death.
3. **Single source of truth (SSOT)** — each piece of data has exactly one owner.
   Mutations to it go through that owner only, exposed as a `Flow`.
4. **Unidirectional data flow (UDF)** — state flows *down* (repository → ViewModel →
   composable), events flow *up* (composable → ViewModel → repository). Never sideways.
5. **Single-activity, Compose-first** — one `Activity`, one `NavHost`, no Fragments.

## UI layer

### UI state

- One **immutable** `data class …UiState` per screen, in **its own file**.
- Model mutually-exclusive screen states as a `sealed interface` when that is the
  truth (e.g. `Loading` / `Empty` / `Content`), rather than a bag of booleans.
- Never expose `MutableStateFlow`. Expose `StateFlow` / `val` only.

### ViewModels

- **One ViewModel per screen.** Do not grow a shared god-ViewModel. If two screens
  need the same data, both observe the same repository — that is what SSOT is for.
- ViewModels expose exactly one `uiState: StateFlow<…UiState>`. Prefer deriving it:

  ```kotlin
  val uiState: StateFlow<FooUiState> =
      combine(repoFlowA, repoFlowB, ::buildState)
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5_000),
              initialValue = FooUiState(),
          )
  ```

- ViewModels must not reference `Context`, `Activity`, `View`, resources, `Toast`,
  `File` pickers, or any other Android UI type. Pass primitives/domain models in,
  emit domain models/state out. Resource resolution happens in the composable.
- Never hold navigation state in a ViewModel. The `NavController` owns it.

### Composables

- Structure per feature folder `ui/<feature>/`:
  1. `…Screen.kt` — **stateful** entry point: `viewModel: …ViewModel = koinViewModel()`,
     collects state, delegates. Handles navigation callbacks.
  2. a **stateless** `private fun …Content(uiState, onEvent…)` that takes plain values
     and lambdas only. This is the one that gets `@Preview`ed.
  3. `@Preview` for each meaningful state (empty / loading / content / error), light+dark.
- Collect with `collectAsStateWithLifecycle()`, **not** `collectAsState()`.
- Hoist state; stateless composables must be trivially previewable and testable.
- Pass **lambdas**, not the ViewModel, into stateless composables.
- Do not pass more than a handful of parameters — if a stateless composable needs 15
  arguments, it needs a UiState parameter instead.

### Navigation

- Single `NavHost`, **type-safe routes** (`@Serializable` objects/data classes).
  No string routes, no enum-and-`when` navigation.
- Routes live in `ui/navigation/`. Arguments travel as route properties.
- Navigation is triggered by lambdas passed down from the NavHost — a screen composable
  never receives a `NavController`.
- Guarded/gated destinations are enforced in the NavHost, and the guard must be
  re-checked on state change (deep links and state restoration can land on a
  locked route).

## Domain layer

- One use case per file, named `VerbNounUseCase`.
- A use case has a **single** public `operator fun invoke(...)`.
- Use cases are stateless and hold no mutable state.
- Business rules (validation, gating, completeness, calculations) belong here — not in
  a ViewModel and never in a composable.
- Use cases return `Result<T>` for fallible operations, or a plain value/`Flow` otherwise.

## Data layer

- One repository per data type; it is the SSOT for that data.
- Repositories expose `Flow` for observable data and `suspend` functions for one-shots.
- Repositories map DTO → domain model. **DTOs must never leave `:data`.**
- Prefer **DataStore** over `SharedPreferences` for new persisted state.
- No global mutable singletons / `object` event buses for app state. Observable state
  belongs in a repository flow.

## Models

- Every model, UiState, and sealed hierarchy goes in **its own dedicated file**.
- Never append a new model class to an already crowded file.

## Dependency injection

- Koin. Register ViewModels with `viewModel { }` and explicit named `get()` arguments.
- Use cases are registered as `factory<XUseCase> { XUseCase.Factory.create(get()) }`.

## Concurrency

- Suspend functions must be main-safe.
- Inject dispatchers; never hardcode `Dispatchers.IO` inside a ViewModel or use case.
- Work is launched in `viewModelScope`; nothing outlives its scope silently.

## Resources and localisation

- **No hardcoded user-facing strings.** Always `stringResource(R.string.…)`.
- New strings go in `values/strings.xml` in **English** (default) *and*
  `values-es/strings.xml` in **Spanish**. Both, in the same change.
- Note: some legacy strings in `values/` are Spanish. That is a known defect — do not
  imitate it, and do not mass-fix it outside a dedicated change.
- Icons are hand-built `ImageVector`s in `ui/components/Icons.kt` (no
  `material-icons-extended` dependency).

## Testing

- Business rules in the domain layer must have unit tests.
- Test ViewModels against their `uiState` flow, not internals.

## Change scope

- Keep changes minimal and feature-focused.
- Preserve current UX behaviour unless the feature explicitly changes it.
- When touching a file that still uses a legacy pattern, migrate that file to the
  rules above rather than extending the legacy pattern.
