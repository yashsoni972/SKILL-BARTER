# SkillBarter — Agent Memory

Persistent working notes for this repo. Read this file first in every new session.

## Project

Peer-to-peer skill exchange app.
- `app/` — Android app (Java, ViewBinding, Retrofit, Gson, Socket.IO, Room).
- `backend/` — Node.js + Express + Mongoose, deployed on Render, DB is MongoDB Atlas.
- Live API base URL (already wired in `ApiClient.java`): `https://skillbarter-api-m0ev.onrender.com/api/`

Backend `.env` holds `MONGODB_URI`, `JWT_SECRET`, `CLIENT_ORIGIN`. Never commit real secrets; `.env.example` is the committed template.

## Build / verify (Windows)

```
.\gradlew.bat app:assembleDebug
.\gradlew.bat app:testDebugUnitTest
.\gradlew.bat app:lintDebug
```

`JAVA_HOME` is empty on this machine, so Gradle would auto-detect the VS Code (redhat.java) JRE which has no `jlink.exe` and breaks the `:app:androidJdkImage` transform. `gradle.properties` pins `org.gradle.java.home=C:\Program Files\Android\Android Studio1\jbr` (JDK 21, has `jlink.exe`). Do not remove that line; `--no-configuration-cache` is not needed anymore.

## APK deliverable

Build output: `app/build/outputs/apk/debug/app-debug.apk` (debug-signed, versionName 1.0, versionCode 1, minSdk 24, targetSdk 35).
Copy it to the repo root as `skillbarter.apk` after every successful build — that is the file handed to the user.

## Architecture rules already established

- **Strict cloud:** no mock/in-memory fallback lists in `SkillBarterRepository`. Failures surface through `DataCallback.onError` using the `httpError(int)` / `networkError(Throwable)` helpers. A `401` renders as "Session expired. Please log in again."
- Repository is a singleton (`getInstance`) wrapping `ApiService`; UI calls `XxxRepository.DataCallback<T>`.
- Mongoose returns one side of a relation populated (object) and the other as a raw ObjectId **string**. Gson chokes on the string, so populated-or-string refs need a `JsonDeserializer`/`JsonSerializer` adapter — see `ExchangeRequest.UserRef`.
- UI callbacks must guard `binding == null` / `isAdded()` so in-flight responses cannot touch a destroyed view.
- Timestamps from Mongo are ISO-8601; format them in the adapter (e.g. `RequestAdapter.formatTime()`).

## Wired to the live API

Auth (register/login/profile), stats, recommended partners, discover matches, exchange requests (send/incoming/outgoing/accept/reject/complete), chat messages + conversations, availability (list/add/delete), progress dashboard, badges, marketplace listings, help requests, session completion.

## Backend route map (must match the app, verify with `node --check`)

- `auth` `/auth/register` `/auth/login` `/auth/profile`
- `requests` `/requests/send` `/requests/incoming` `/requests/outgoing` `/requests/exchanges` `/requests/with/:userId` `/requests/:id/accept|reject|complete`
- `messages` `/messages/conversations` `/messages/:userId` `/messages/send`
- `users` `/users/progress` `/users/stats` `/users/badges` `/users/match-suggestions` `/users/discover`
- `availability` `/availability/me` `/availability` `/availability/:day`
- `sessions` `/sessions` `/sessions/schedule` `/sessions/:id/complete`
- `credits` `/credits/me` `/credits/transactions`
- `notifications` `/notifications` `/notifications/unread-count` `/notifications/read-all`
- `reviews` `/reviews/add` `/reviews/mine` `/reviews/user/:userId`

## Credit economy

- 40 credits on registration.
- Session completion: teacher `+10` per hour, learner `-10` per hour, balance clamped at `0`.
- Google Meet is the only delivery medium. Every credit movement writes a `CreditTransaction`.

## One exchange per member pair

A member pair shares a single exchange. `GET /requests/with/:userId` tells the app
whether to show "Send Request", "Awaiting reply", or "Chat". `SendRequestActivity`
calls this before showing the form, so an accepted pair never sees a send button
again. `ExchangeRelationship` models the response (`status`, `requestId`, `canChat`,
`canRequest`, `direction`).

## Screens now API-backed

Credits, Notifications (with unread badge), Exchange history (`ui/exchanges`),
Rate & Review, Exchange details date/time/Google Meet, Profile credit balance.
Room/local DAO code was deleted as unused — do not reintroduce it.

## Known environment note

Local `node server.js` cannot reach MongoDB Atlas from this machine
(`querySrv ECONNREFUSED ... _mongodb._tcp.cluster0...`), so end-to-end DB tests must
run against Render after push. Auth-free route checks still return 401, which proves
route wiring.

## Commit style

Short imperative subject, one feature per commit, e.g. `Connect exchange requests to MongoDB Atlas and fix live API connectivity`. Never commit secrets or the APK binary without asking.
