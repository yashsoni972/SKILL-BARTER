# Connect Exchange Requests to MongoDB Atlas Backend Plan

Refactor `SkillBarterRepository` and `RequestsFragment` to fetch, send, accept, and reject exchange requests via the live backend API and MongoDB Atlas (`exchangerequests` collection) instead of in-memory mock lists.

## User Review Required

> [!IMPORTANT]
> This change connects the **Exchange Requests** feature (Sending, Receiving, Accepting, Rejecting) directly to MongoDB Atlas via Retrofit API calls, ensuring requests persist correctly and sync across multiple devices (such as between your phone and your mother's phone).

## Open Questions

- None. The backend REST endpoints (`POST /api/requests`, `GET /api/requests/incoming`, `GET /api/requests/outgoing`, `PUT /api/requests/:id/accept`, `PUT /api/requests/:id/reject`) are already fully implemented in the Node.js server.

## Proposed Changes

### Repository Refactoring

#### [MODIFY] [SkillBarterRepository.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/repository/SkillBarterRepository.java)
- Update `fetchIncomingRequests(DataCallback<List<ExchangeRequest>> callback)` to call `apiService.getIncomingRequests()`.
- Update `fetchOutgoingRequests(DataCallback<List<ExchangeRequest>> callback)` to call `apiService.getOutgoingRequests()`.
- Update `sendExchangeRequest(...)` to call `apiService.sendRequest(...)`.
- Update `acceptRequest(...)` to call `apiService.acceptRequest(...)`.
- Update `rejectRequest(...)` to call `apiService.rejectRequest(...)`.

### UI Integration

#### [MODIFY] [RequestsFragment.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/ui/requests/RequestsFragment.java)
- Update `loadRequests()` and `updateTabTitles()` to use asynchronous API callbacks (`DataCallback`) when fetching incoming and outgoing requests from MongoDB Atlas.

#### [MODIFY] [SendRequestActivity.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/ui/requests/SendRequestActivity.java)
- Ensure sending a request uses `apiService.sendRequest(...)` and saves to MongoDB Atlas.

## Verification Plan

### Automated Tests
- Run `./gradlew app:assembleDebug`
- Run `./gradlew app:testDebugUnitTest`
- Run `./gradlew app:lintDebug`

### Manual Verification
- Send an exchange request from the app, check MongoDB Atlas `exchangerequests` collection to verify the document is saved in the cloud database, and view it in the Requests tab.
