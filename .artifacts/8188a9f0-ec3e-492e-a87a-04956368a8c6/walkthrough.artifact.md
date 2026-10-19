# Walkthrough - Custom Gallery Avatar Picker Fix

## Summary of Fixes

1. **Custom Gallery Photo Picker**:
   - Wired up the `"Upload Custom Photo"` button in `dialog_avatar_picker.xml` to `ProfileFragment.java` using `ActivityResultLauncher` with `ACTION_OPEN_DOCUMENT` / `ACTION_GET_CONTENT`.
   - Enabled Glide to load custom gallery image URIs directly into the circular profile avatar view (`ivAvatar`).
   - Synced custom photo selections with the backend (`PUT /users/profile`).

2. **Verification & Delivery**:
   - Build succeeded (`app:assembleDebug`).
   - Unit tests passed (9/9) and lint passed with zero errors.
   - Updated deliverable APK at `skillbarter.apk`.
