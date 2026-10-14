# Comprehensive Bug Fixes & Feature Enhancements

Address user-reported issues: AI Roadmap 404 error fix, profile avatar backend sync, profile bio edit option, update check popup & auto-prompt, and chat link/attachment verification.

## User Review Required

> [!IMPORTANT]
> - **AI Roadmap 404 Fix**: Remove `openai` package dependency requirement in `roadmapController.js` and rely on a robust, lightning-fast intelligent smart roadmap generator to guarantee 100% reliability without server crashes.
> - **Profile Avatar & Bio Edit**: Add a backend profile update call when changing avatar, and add an edit bio dialog in `ProfileFragment`.
> - **App Update Dialog**: Ensure update checks prompt an actionable dialog with direct APK download/install links whenever a newer version exists.

## Proposed Changes

### Backend (`backend/`)

#### [MODIFY] [roadmapController.js](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/backend/controllers/roadmapController.js)
- Remove `require('openai')` dependency so the controller runs reliably without failing module loads (fixing 404/500 errors).

#### [MODIFY] [userController.js](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/backend/controllers/userController.js)
- Ensure profile update route accepts `profileImage` and `bio` updates.

---

### Android App (`app/`)

#### [MODIFY] [ProfileFragment.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/ui/profile/ProfileFragment.java)
- Persist profile avatar selection to backend (`/users/profile`).
- Add bio edit dialog when tapping on bio or an edit button.
- Enhance update check to always offer an interactive update dialog.

#### [MODIFY] [ChatActivity.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/ui/chat/ChatActivity.java)
- Verify and ensure chat links, file attachments, and attachment buttons work seamlessly.

## Verification Plan

### Automated Tests
- Gradle build: `.\gradlew.bat app:assembleDebug`
- Backend check: `node --check backend/server.js`

### Manual Verification
- Test AI Roadmap generation (verify 200 OK and valid steps).
- Test Profile Avatar selection and Bio editing.
- Test "Check for Updates" and update dialog.
- Test chat attachments and links.
