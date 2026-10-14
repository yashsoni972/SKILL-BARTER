# Task List - AI Learning Roadmap Generator

- [x] Backend implementation
  - [x] Create `backend/controllers/roadmapController.js`
  - [x] Create `backend/routes/roadmapRoutes.js`
  - [x] Register roadmap route in `backend/server.js`
- [x] Android Models & Networking
  - [x] Create `RoadmapStep.java` and `LearningRoadmap.java`
  - [x] Update `ApiService.java` with `generateRoadmap`
  - [x] Update `SkillBarterRepository.java` with `generateRoadmap`
- [x] Android UI Implementation
  - [x] Create `item_roadmap_step.xml` and `activity_roadmap_generator.xml`
  - [x] Create `RoadmapStepAdapter.java`
  - [x] Create `RoadmapGeneratorActivity.java`
  - [x] Register `RoadmapGeneratorActivity` in `AndroidManifest.xml`
- [x] Verification & Build
  - [x] Run backend syntax check (`node --check`)
  - [x] Build Android app (`app:assembleDebug`)
  - [x] Copy APK to root as `skillbarter.apk`
