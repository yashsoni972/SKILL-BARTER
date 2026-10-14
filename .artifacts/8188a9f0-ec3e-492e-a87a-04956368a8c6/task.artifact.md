# Task List - Bug Fixes & Enhancements

- [ ] Backend fixes
  - [ ] Update `backend/controllers/roadmapController.js` to remove missing `openai` module requirement (fixing 404/500 errors).
- [ ] Android profile & avatar updates
  - [ ] Update `ProfileFragment.java` to sync avatar selection with backend profile update.
  - [ ] Add Bio Edit dialog in `ProfileFragment.java`.
- [ ] App update check enhancement
  - [ ] Enhance update check dialog in `ProfileFragment.java` to show update details and direct APK download link.
- [ ] Verification & Build
  - [ ] Run backend syntax check (`node --check`)
  - [ ] Build Android app (`app:assembleDebug`)
  - [ ] Copy APK to root as `skillbarter.apk`
