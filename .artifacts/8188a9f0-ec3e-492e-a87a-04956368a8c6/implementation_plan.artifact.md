# AI Learning Roadmap Generator

Provide a backend AI-powered roadmap generator and an Android UI feature that helps users plan what to learn next based on their current skill, goal, and experience level.

## User Review Required

> [!IMPORTANT]
> - **Backend AI Integration**: The backend will use OpenAI or Google Gemini API (or an intelligent structured roadmap generator fallback if no API key is provided) to generate personalized step-by-step roadmaps. API keys will remain strictly on the backend (`.env`), never exposed in the Android app.
> - **Android UI**: A dedicated `RoadmapGeneratorActivity` featuring input fields (Current Skill, Goal, Experience Level), a progress bar, completion checkboxes for each roadmap step, and persistence of progress.

## Open Questions

- Should roadmap progress be saved per user in MongoDB so they can resume later? *(Proposed: Yes, store roadmap in MongoDB user schema or roadmap collection, or local session state).*

## Proposed Changes

### Backend (`backend/`)

#### [NEW] [roadmapController.js](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/backend/controllers/roadmapController.js)
- Endpoint logic to receive `currentSkill`, `goal`, `experience`.
- Calls OpenAI / Gemini API (or generates structured steps) and returns roadmap steps.

#### [NEW] [roadmapRoutes.js](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/backend/routes/roadmapRoutes.js)
- Express router mapping `POST /roadmap/generate`.

#### [MODIFY] [server.js](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/backend/server.js)
- Register `roadmapRoutes` at `/api/roadmap`.

---

### Android App (`app/`)

#### [NEW] [RoadmapStep.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/data/model/RoadmapStep.java)
- Data model representing a step title, description, and completion state.

#### [NEW] [LearningRoadmap.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/data/model/LearningRoadmap.java)
- Data model representing the full roadmap response.

#### [MODIFY] [ApiService.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/api/ApiService.java)
- Add `@POST("roadmap/generate") Call<LearningRoadmap> generateRoadmap(@Body Map<String, String> body);`.

#### [MODIFY] [SkillBarterRepository.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/repository/SkillBarterRepository.java`)
- Add `generateRoadmap` repository method.

#### [NEW] [RoadmapGeneratorActivity.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/ui/roadmap/RoadmapGeneratorActivity.java)
- Activity handling input form, calling API, and displaying roadmap.

#### [NEW] [RoadmapStepAdapter.java](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/java/com/yashsoni/skillbarter/ui/roadmap/RoadmapStepAdapter.java)
- RecyclerView adapter for roadmap steps with interactive checkboxes and progress updates.

#### [NEW] [activity_roadmap_generator.xml](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/res/layout/activity_roadmap_generator.xml)
- UI layout for roadmap generator inputs, progress bar, and list.

#### [NEW] [item_roadmap_step.xml](file:///C:/Users/HP/AndroidStudioProjects/SkillBarter/app/src/main/res/layout/item_roadmap_step.xml)
- RecyclerView item layout for each step with checkbox and title.

## Verification Plan

### Automated Tests
- Gradle build: `.\gradlew.bat app:assembleDebug`
- Backend check: `node --check backend/server.js`

### Manual Verification
- Launch the app, navigate to AI Roadmap Generator, enter Current Skill (e.g., "HTML"), Goal ("React Developer"), and Experience ("Beginner"), tap "Generate Roadmap", and verify that interactive steps and progress indicator load correctly.
