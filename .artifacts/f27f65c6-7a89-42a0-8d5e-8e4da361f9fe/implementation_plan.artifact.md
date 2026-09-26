# Offline Caching with Room Implementation Plan

Implement local database caching using **Android Room Persistence Library** to enable offline support and seamless fallback for users, profiles, and messages in Skill Barter.

## User Review Required

> [!NOTE]
> This addition introduces Room database dependencies and local DAOs/entities to cache user profiles, skills, and messages locally, allowing the app to function smoothly even when offline or when the backend server is unreachable.

## Open Questions

- None. Room 2.6.1 / 2.7.x libraries will be added to `app/build.gradle.kts`.

## Proposed Changes

### Dependencies & Setup

#### [MODIFY] [build.gradle.kts](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/build.gradle.kts)
- Add Room dependencies (`androidx.room:room-runtime`, `androidx.room:room-compiler`) and annotation processor.

### Database Entities & DAOs

#### [NEW] [UserEntity.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/data/local/UserEntity.java)
- Room entity for cached users.

#### [NEW] [MessageEntity.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/data/local/MessageEntity.java)
- Room entity for cached chat messages.

#### [NEW] [SkillBarterDao.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/data/local/SkillBarterDao.java)
- Data Access Object for users and messages.

#### [NEW] [SkillBarterDatabase.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/data/local/SkillBarterDatabase.java)
- Room Database abstract class singleton.

### Repository Integration

#### [MODIFY] [SkillBarterRepository.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/repository/SkillBarterRepository.java)
- Integrate Room database access as offline fallback alongside Retrofit API calls.

## Verification Plan

### Automated Tests
- Run `./gradlew app:assembleDebug`
- Run `./gradlew app:testDebugUnitTest`
- Run `./gradlew app:lintDebug`

### Manual Verification
- Deploy app to emulator/device, verify offline loading of cached profiles and messages.
