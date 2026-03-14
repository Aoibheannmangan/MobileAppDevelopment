# Mobile App Dev Project - Spec & Assessment

## Project Overview
**App:** Word Logger — a vocabulary-tracking Android app where users sign up/login via Supabase, log words they encounter while reading (with auto-looked-up meanings from a dictionary API), view their saved words, and monitor device stats.

**Tech Stack:** Kotlin, XML Views (not Compose), Supabase (Postgrest + Auth), Ktor HTTP client, RecyclerView

**Screens:**
1. **MainActivity** — Login (email/password via Supabase Auth)
2. **SignupActivity** — Registration (Supabase Auth)
3. **HomeActivity** — Hub with 4 navigation buttons (Add Word, View Words, Quiz, Device Stats)
4. **AddWordActivity** — Enter a word + book name, fetches meaning from Dictionary API, saves to Supabase
5. **ViewWordActivity** — RecyclerView list of user's saved words from Supabase
6. **QuizActivity** — Empty stub (not implemented)
7. **DeviceStatsActivity** — Collects battery/network/RAM/storage info, uploads to Supabase every 30s

---

## Assessment Against Rubric

### 1. FUNCTIONAL COMPLETENESS & NAVIGATION FLOW (10%)
**Current Grade: UNSATISFACTORY (25%)**

- Uses separate Activities with `startActivity()` + explicit Intents — NOT Compose Navigation as required.
- No argument passing between destinations.
- No proper back stack handling (uses `finish()` in some places but mostly creates new Activity instances for "Home" buttons, which stacks duplicate HomeActivities).
- QuizActivity is completely empty/unimplemented.
- Navigation is simplistic: Home -> screen, screen -> Home. No non-trivial flow.

**To reach Excellent:**
- Migrate to Jetpack Compose UI with `NavHost` / `NavController`.
- Pass arguments between screens (e.g., word ID to a detail screen).
- Handle back stack properly (e.g., `popUpTo`, `launchSingleTop`).
- Implement QuizActivity as a functional screen.

---

### 2. USE OF LAZY LISTS / GRIDS (5%)
**Current Grade: UNSATISFACTORY-SATISFACTORY (~35%)**

- ViewWordActivity uses `RecyclerView` with `LinearLayoutManager` — this is the XML/Views equivalent, NOT Compose's `LazyColumn`/`LazyGrid` as required.
- The list does display data, but is not reactive to state changes (loaded once in `onCreate`, never updates if data changes).
- No item detail screen on tap.

**To reach Excellent:**
- Replace RecyclerView with Compose `LazyColumn` or `LazyGrid`.
- Make it reactive to state via `StateFlow` or `mutableStateOf`.
- Add an item detail view on click.

---

### 3. LOCAL PERSISTENCE WITH ROOM & LAYERING (10%)
**Current Grade: GOOD (~80%) ✅ Completed on architecture-refactor branch**

- Room database added: `WordEntity` (@Entity), `WordDao` (@Dao with Flow queries), `AppDatabase` (@Database singleton).
- Repository layer added: `WordRepository` (Room + Supabase + Dictionary API), `AuthRepository`, `DeviceStatsRepository`.
- ViewModels added for all screens using `AndroidViewModel` + `viewModelScope` + `StateFlow`.
- Words saved locally to Room first (offline-first), then synced to Supabase.
- Data survives app restarts via Room SQLite storage.
- Remaining gap: on first install, local Room DB is empty — could seed from Supabase on login.

---

### 4. CONCURRENCY & BACKGROUND WORK (10%)
**Current Grade: SATISFACTORY (50%)**

- Uses `lifecycleScope.launch` correctly in all Activities — lifecycle-aware, cancels on destroy.
- Network calls (Supabase, Dictionary API) are off the main thread via coroutines.
- DeviceStatsActivity uses a coroutine loop with `delay(30_000)` for periodic collection.
- However: no structured concurrency patterns, no error recovery, no `Dispatchers.IO` usage.
- **No WorkManager or foreground Service** — the device stats collection dies when the activity is destroyed. The spec requires appropriate background mechanisms.
- The `while(true)` loop in `startCollecting()` is not the recommended approach.

**To reach Excellent:**
- Use `Dispatchers.IO` for database/network calls.
- Implement WorkManager for periodic device stats collection.
- Add proper error handling and retry logic.
- Use structured concurrency with `supervisorScope` where appropriate.

---

### 5. FIREBASE (OR CLOUD DB) INTEGRATION (5%)
**Current Grade: GOOD-EXCELLENT (~85%)**

- Uses **Supabase** (Postgrest) as cloud database — this is explicitly allowed by the rubric ("Firestore, Realtime DB, Supabase, etc.").
- Two tables: `words` and `device_stats`.
- Auth integration via Supabase Auth (email/password signup + login).
- RLS (Row Level Security) filtering by user_id.
- Missing: offline data / local caching (no offline support).

**To reach Excellent:**
- Add offline data support (cache via Room, sync with Supabase).

---

### 6. ARCHITECTURE & CODE QUALITY (5%)
**Current Grade: GOOD-EXCELLENT (~85%) ✅ Completed on architecture-refactor branch**

- MVVM architecture implemented: ViewModel per screen, Repository layer, model layer.
- Separated into packages: `ui/` (Activities + ViewModels), `data/local/`, `data/remote/`, `data/repository/`, `model/`.
- `parseMeaning()` moved to `WordRepository` where it belongs.
- `DeviceStats` extracted to `model/DeviceStats.kt`.
- Activities are now thin UI layers — all logic in ViewModels and Repositories.
- Remaining gap: no dependency injection (Hilt) — repositories are manually instantiated in ViewModels.

---

### 7. DOCUMENTATION & GENAI TRANSPARENCY (5%)
**Current Grade: SATISFACTORY (50%)**

- Code has some comments/KDoc explaining what classes and methods do.
- No report submitted yet (as far as we can see here).
- No GenAI usage citations in the code (no `// Generated by AI` comments).
- Comments are informal but present ("cause thats what makes sense", "We pumping adrenaline").

**To reach Excellent:**
- Write the 2-4 page report.
- Add GenAI citations in code comments where applicable.
- Clean up informal comments.

---

## Summary Score Estimate

| Criterion | Weight | Estimated Grade | Weighted Score |
|---|---|---|---|
| Navigation & Flow | 10% | 25% (Unsatisfactory) | 2.5% |
| Lazy Lists/Grids | 5% | 35% | 1.75% |
| Room & Layering | 10% | 80% (Good) ✅ | 8.0% |
| Concurrency | 10% | 50% (Satisfactory) | 5.0% |
| Cloud DB (Supabase) | 5% | 85% (Good-Excellent) | 4.25% |
| Architecture | 5% | 85% (Good-Excellent) ✅ | 4.25% |
| Documentation | 5% | 50% (Satisfactory) | 2.5% |
| **TOTAL (App Quality)** | **50%** | | **~29/50** |

**Estimated App Quality Score: ~58% (up from ~39.5%)**

---

## Critical Gaps (Priority Order)

1. ~~**No Room database**~~ ✅ Room implemented with WordEntity, WordDao, AppDatabase.
2. **No Jetpack Compose** — The entire UI uses XML Views. The spec requires Compose with Compose Navigation.
3. ~~**No ViewModel/Repository**~~ ✅ MVVM + Repository fully implemented.
4. ~~**QuizActivity is empty**~~ ✅ Implemented — shows a random saved word's meaning, user types the word, checks answer.
5. **No WorkManager** — Background work requirement not met.
6. **RecyclerView instead of LazyColumn** — Wrong toolkit for the list requirement.
7. **No offline support** — Data doesn't persist locally at all.

## What's Working Well

- Supabase integration is solid (auth + data storage + RLS).
- Coroutines are used correctly with lifecycle scope.
- Dictionary API integration is a nice feature.
- Device stats collection is creative and functional.
- Code comments show understanding of what the code does.
