# UI/UX & Feature Refinement Plan

Refine app-wide UI/UX to ensure a clean, professional, WhatsApp-like chat experience, proper link clickability, professional attachment rendering, rating visibility rules, skill wrapping in cards, avatar picker styling, and removal of toyish emojis/badges.

## User Review Required

> [!IMPORTANT]
> - **Chat WhatsApp Style**: Upgrade `item_message_bubble.xml` and `MessageAdapter.java` to support link auto-linking (clickable links opening instantly) and proper WhatsApp-like attachment/file preview cells.
> - **Ratings & Badges**: Hide rating stars and match/fire percentages when users have zero ratings or matches.
> - **Skills Wrapping**: Ensure user cards and profile cards handle 2+ skills gracefully without clipping or breaking layout.
> - **Avatar Picker & Profile Bio**: Redesign `dialog_avatar_picker.xml` to look professional and modern, and fix bio layout formatting.
> - **Professional Polish**: Remove emojis/toyish icons across cards for a clean, professional look.

## Proposed Changes

### Chat
#### [MODIFY] [item_message_bubble.xml](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/res/layout/item_message_bubble.xml)
- Add file/attachment preview container inside the chat bubble layout.
- Add `android:autoLink="web"` to message text view.

#### [MODIFY] [MessageAdapter.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/ui/chat/MessageAdapter.java)
- Enable link movement method for direct clickability.
- Render attachment previews (file name / icon) if message has an attachment.

---

### User Cards & Discovery
#### [MODIFY] [item_user_card.xml](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/res/layout/item_user_card.xml) & [UserAdapter.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/ui/home/UserAdapter.java)
- Remove fire/match % badges.
- Hide rating stars if rating is 0.
- Support multiple skill chips wrapping cleanly.

---

### Profile & Avatar Picker
#### [MODIFY] [dialog_avatar_picker.xml](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/res/layout/dialog_avatar_picker.xml)
- Modernize avatar grid with clean styling and professional colors.

#### [MODIFY] [fragment_profile.xml](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/res/layout/fragment_profile.xml) & [ProfileFragment.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/ui/profile/ProfileFragment.java)
- Clean up bio formatting and layout breakage.

## Verification Plan

### Automated Tests
- Gradle build: `.\gradlew.bat app:assembleDebug`

### Manual Verification
- Deploy and verify WhatsApp-style chat links and attachments.
- Verify user cards show skills cleanly and hide unearned ratings/fire badges.
- Verify avatar picker and profile bio layout.
