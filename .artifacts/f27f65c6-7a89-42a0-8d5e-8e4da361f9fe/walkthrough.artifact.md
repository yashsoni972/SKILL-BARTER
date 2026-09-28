# Walkthrough - Exchange Requests Persisted to MongoDB Atlas

## Changes Made

### [NEW] `data/model/ExchangeRequest.java`
- Added a nested `UserRef` Gson `JsonDeserializer`/`JsonSerializer` and applied it to `senderId`/`receiverId` via `@JsonAdapter`.
- **Why this was required:** Mongoose returns the populated side of a request as a user object (`senderId` for incoming, `receiverId` for outgoing) and the other side as a raw `ObjectId` **string**. Without the adapter Gson throws a `JsonSyntaxException` on the string, so every call surfaced as `onFailure` even when the document had been written to MongoDB Atlas.
- The adapter wraps a bare id string into a `User` carrying only `_id`, keeps populated objects intact, and maps JSON `null` to `null`.

### [MODIFY] `repository/SkillBarterRepository.java`
- `fetchIncomingRequests` / `fetchOutgoingRequests` / `sendExchangeRequest` / `acceptRequestApi` / `rejectRequestApi` now report real failures through `DataCallback.onError` instead of silently substituting the in-memory mock lists. This matches the strict-cloud decision made for auth in the previous task.
- Added `httpError(int)` and `networkError(Throwable)` helpers; a `401` is surfaced as "Session expired. Please log in again."
- Removed the dead mock request path that is no longer reachable from the UI: `getIncomingRequests()`, `getOutgoingRequests()`, `addOutgoingRequest()`, `acceptRequest()`, `rejectRequest()`, and the `mockIncomingRequests` / `mockOutgoingRequests` lists.
- `getStats()` no longer derives the "active" figure from the deleted mock lists.

### [MODIFY] `ui/requests/SendRequestActivity.java`
- Send now calls `repository.sendExchangeRequest(targetUser.getId(), offeredSkill, requestedSkill, message, callback)`, which hits `POST /api/requests` and writes to the `exchangerequests` collection.
- The button is disabled while the request is in flight and re-enabled on failure; the success toast only appears after a `201` from the server.
- Removed the fabricated "Riya Sharma" fallback user, which would have posted a non-ObjectId `receiverId` and produced a server-side `CastError`.
- Added a guard that blocks the send and explains the problem if the partner has no cloud `_id`.

### [MODIFY] `ui/requests/RequestsFragment.java`
- `onResume` now triggers `refreshRequests()`, which fires `GET /api/requests/incoming` and `GET /api/requests/outgoing` in parallel and caches both lists in the fragment; the tab titles ("Received (n)" / "Sent (n)") are derived from those server responses.
- The two lists are fetched once per resume instead of twice (the old code loaded in both `onViewCreated` and `onResume`). Tab switches re-render from the cache and no longer hit the network.
- Accept and Reject call `PUT /api/requests/{id}/accept` and `PUT /api/requests/{id}/reject`; the chat is opened and the list refreshed only after the server confirms, and errors are shown as a Toast.
- All callbacks guard `binding == null` / `isAdded()` so an in-flight response cannot touch a destroyed view.

### [MODIFY] `ui/requests/RequestAdapter.java`
- `createdAt` from MongoDB is an ISO-8601 timestamp; added `formatTime()` so the card shows "Just now", "Nm ago", "Nh ago" or "d MMM yyyy" instead of the raw value, falling back to the raw string if it is not an ISO timestamp.

### [MODIFY] `ui/profile/ProfileFragment.java`
- The "Requests" stat previously summed the two mock lists. It now calls `loadRequestCount()` from `onResume`, which reads the real incoming/outgoing counts from the API (each callback assigns its own counter and re-renders the sum, so repeated resumes cannot double-count).

### [NEW] `app/src/test/java/com/yashsoni/skillbarter/data/model/ExchangeRequestTest.java`
- 3 unit tests covering the deserialization blocker: an incoming request with a populated sender plus a raw `receiverId` string, a send response with both ids as raw strings, and explicit JSON `null` refs.

## Deviations From Plan
- The plan named the new methods `acceptRequest` / `rejectRequest`. They are `acceptRequestApi` / `rejectRequestApi` so they cannot collide with the `ApiService` methods of the same name.
- `ExchangeRequest` and `ProfileFragment` were not in the plan; both were required for the planned work to function.

## Verification Results
- `app:assembleDebug`: **SUCCESS**
- `app:testDebugUnitTest`: **SUCCESS** - 4 tests, 0 failures (`ExchangeRequestTest` 3, `ExampleUnitTest` 1)
- `app:lintDebug`: **SUCCESS** - 0 errors (338 pre-existing warnings, unchanged in kind)

## Manual Verification Outstanding
- Send a request from device A, confirm the document appears in the MongoDB Atlas `exchangerequests` collection, accept it from device B, and confirm the status flips to `accepted` on both devices.
