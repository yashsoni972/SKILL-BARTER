# 🚀 Skill Barter – Peer-to-Peer Skill Exchange Platform

<div align="center">

  ![Skill Barter Logo](app/src/main/res/drawable/ic_logo.xml)

  ### **"Exchange Skills, Not Money."**

  *A modern, peer-to-peer mobile application built with Android Jetpack Material Design 3 and a Node.js / Express REST API with MongoDB Atlas.*

  [![Live API Demo](https://img.shields.io/badge/Render-Live%20API%20Demo-brightgreen?style=for-the-badge&logo=render)](https://skillbarter-api-m0ev.onrender.com/)
  [![GitHub Repo](https://img.shields.io/badge/GitHub-SKILL--BARTER-blue?style=for-the-badge&logo=github)](https://github.com/yashsoni972/SKILL-BARTER)
  [![Android](https://img.shields.io/badge/Android-Material%203-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com/)
  [![MongoDB Atlas](https://img.shields.io/badge/MongoDB-Atlas%20Cloud-47A248?style=for-the-badge&logo=mongodb)](https://www.mongodb.com/cloud/atlas)

</div>

---

## 📌 Table of Contents
- [Overview](#-overview)
- [Key Features](#-key-features)
- [Live Backend Demo](#-live-backend-demo)
- [System Architecture & User Journey](#-system-architecture--user-journey)
- [Mobile UI Screens Overview](#-mobile-ui-screens-overview)
- [Tech Stack](#-tech-stack)
- [MongoDB Collections & Database Design](#-mongodb-collections--database-design)
- [Backend REST API Endpoints](#-backend-rest-api-endpoints)
- [Getting Started & Installation](#-getting-started--installation)
- [Author & License](#-author--license)

---

## 🌟 Overview

**Skill Barter** is a peer-to-peer mobile platform that allows people to **teach skills they know in exchange for learning skills they want**. 

For example:
* **User A** can teach *JavaScript* but wants to learn *Graphic Design*.
* **User B** can teach *Graphic Design* but wants to learn *JavaScript*.
* **Skill Barter** identifies this mutual match, connects both users, enables real-time messaging, schedules an exchange session, and collects reviews/ratings!

---

## ✨ Key Features

- 📱 **Modern Material 3 UI/UX**: Clean indigo/blue aesthetic, rounded cards, custom buttons, smooth transitions, and responsive empty states.
- 🎨 **Profile Avatar Customization**: 6 vibrant preset vector avatars (`avatar_1` to `avatar_6`) selectable via a custom bottom dialog.
- 🔐 **JWT Authentication & Session Management**: Secure user registration, password hashing with bcrypt, and persistent JWT session storage.
- 🏠 **Dynamic Home Dashboard**: Live platform statistics counter (*Registered Users*, *Active Exchanges*, *Completed Exchanges*), skill chips, and recommended partners.
- 🔍 **Real-Time Skill Search & Matching**: Search partners by skill name, category, or location with instant compatibility matching (`JavaScript ↔ Graphic Design`).
- 📩 **Exchange Request Flow**: Send, receive, accept, or reject skill exchange requests.
- 💬 **Interactive Chat Messaging**: Real-time communication interface with message bubbles and timestamps.
- 📅 **Session Scheduling & Completion**: Arrange session dates/times, location links, and mark exchanges as completed.
- ⭐ **Rating & Review System**: Interactive 5-star rating bar and feedback submission affecting user profile ratings.
- ⚡ **Offline-Ready Architecture**: Hybrid repository with Retrofit 2 REST API client connecting to live Render backend with seamless offline fallback mode.

---

## 🌐 Live Backend Demo

The Node.js/Express REST API backend is deployed live on Render and connected to MongoDB Atlas:

- 🔗 **Live Base URL**: [`https://skillbarter-api-m0ev.onrender.com`](https://skillbarter-api-m0ev.onrender.com)
- 🔗 **Health Check Endpoint**: [`https://skillbarter-api-m0ev.onrender.com/api/health`](https://skillbarter-api-m0ev.onrender.com/api/health)
- 🔗 **Live Platform Stats Endpoint**: [`https://skillbarter-api-m0ev.onrender.com/api/users/stats`](https://skillbarter-api-m0ev.onrender.com/api/users/stats)

---

## 🏗️ System Architecture & User Journey

```
                        ┌─────────────────────────────────────┐
                        │        Skill Barter Android         │
                        │       Material Design 3 App         │
                        └──────────────────┬──────────────────┘
                                           │
                                  Retrofit REST Client
                                (with offline fallback)
                                           │
                        ┌──────────────────▼──────────────────┐
                        │      Node.js / Express Server       │
                        │        (Deployed on Render)         │
                        └──────────────────┬──────────────────┘
                                           │
                                       Mongoose
                                           │
                        ┌──────────────────▼──────────────────┐
                        │        MongoDB Atlas Cloud          │
                        │       Database: "skillbarter"       │
                        └─────────────────────────────────────┘
```

### Complete User Journey Flow
```text
Install App ➔ Splash Screen ➔ Onboarding ➔ Register / Login ➔ Set Profile & Skills ➔ Home Dashboard
                                                                                            │
Rating & Review ◄─ Mark Completed ◄─ Exchange Session ◄─ Chat ◄─ Accept Request ◄─ Send Request
```

---

## 📱 Mobile UI Screens Overview

| Screen | Description & Functionality |
| :--- | :--- |
| **Splash Screen** | Features Skill Barter logo, tagline *"Exchange Skills. Grow Together."*, and auto-routing. |
| **Onboarding** | 3-slide ViewPager showcasing *"Share What You Know"*, *"Discover New Skills"*, *"Grow Together"*. |
| **Login & Register** | Material input fields for Name, Email, Password, Location, Bio with input validation. |
| **Home Dashboard** | Dynamic greeting (*"Hello, Yash 👋"*), Search bar, Hero banner, Live Stats bar, Popular Skill chips, Recommended Partners. |
| **Discover & Search** | Real-time search bar filtering users by offered/wanted skills, category, and city. |
| **Exchange Requests** | Tabbed Received & Sent requests with **Accept** / **Reject** / **Cancel** actions. |
| **Chat Screen** | Active conversation list & interactive messaging with timestamps and status indicators. |
| **User Profile** | Bio, location, stats counters (*3 Skills*, *2 Requests*, *1 Completed*), Offered/Wanted skill chips, Logout. |
| **Avatar Customization** | Tap profile photo to choose from 6 vibrant vector avatars updating across the entire app. |
| **Add / Edit Skills** | Tabbed *"I Can Teach"* vs *"I Want to Learn"* selection with custom skill input field. |
| **Send Exchange Request**| Target user summary, *"You want to learn"* skill card, *"You can teach"* skill card, message box. |
| **Schedule Session** | Exchange pair cards (`Yash Soni ↔ Riya Sharma`), Date, Time, Location, and *"Mark as Completed"*. |
| **Rate & Review** | 5-star rating bar and text review submission updating user average profile rating. |

---

## 🛠️ Tech Stack

### Mobile Application
- **Language**: Java / Android SDK (Min SDK 24, Target/Compile SDK 36)
- **UI Toolkit**: Material Design 3 Components, ConstraintLayout, RelativeLayout, ViewPager2, RecyclerView, ChipGroup
- **Architecture**: Jetpack ViewModel, ViewBinding, Repository Pattern, SessionManager
- **Networking**: Retrofit 2, Gson Converter, OkHttp 4 Logging Interceptor
- **Image Loading**: Glide

### Backend Service
- **Runtime**: Node.js & Express.js
- **Database**: MongoDB Atlas Cloud (`skillbarter` database)
- **Object Data Modeling**: Mongoose 8
- **Authentication & Security**: JSON Web Tokens (JWT), bcryptjs password hashing, CORS, Dotenv
- **Deployment**: Render.com Web Service

---

## 🗄️ MongoDB Collections & Database Design

The cloud database **`skillbarter`** on MongoDB Atlas comprises 7 collections:

```text
skillbarter/
├── users               (name, email, password, location, bio, profileImage, rating, totalExchanges)
├── skills              (userId, skillName, category, type: offer|want, level, description)
├── exchangeRequests    (senderId, receiverId, offeredSkill, requestedSkill, message, status)
├── messages            (senderId, receiverId, message, read, createdAt)
├── sessions            (requestId, hostUserId, partnerUserId, skill, date, time, location, notes, status)
├── reviews             (reviewerId, reviewedUserId, rating, comment, createdAt)
└── notifications       (userId, title, message, type, isRead, createdAt)
```

---

## 📡 Backend REST API Endpoints

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/auth/register` | Register new user account | ❌ |
| `POST` | `/api/auth/login` | Login user & receive JWT token | ❌ |
| `GET` | `/api/users/profile` | Get logged-in user profile | ✅ |
| `PUT` | `/api/users/profile` | Update user profile details | ✅ |
| `GET` | `/api/users/stats` | Get live platform statistics count | ❌ |
| `GET` | `/api/users/search` | Search users by skill or location | ✅ |
| `GET` | `/api/users/recommended` | Get recommended skill partners | ✅ |
| `POST`| `/api/skills/batch` | Add/Update user offered & wanted skills | ✅ |
| `POST`| `/api/requests` | Send a new skill exchange request | ✅ |
| `GET` | `/api/requests/incoming` | Get received exchange requests | ✅ |
| `GET` | `/api/requests/outgoing` | Get sent exchange requests | ✅ |
| `PUT` | `/api/requests/:id/accept` | Accept an exchange request | ✅ |
| `PUT` | `/api/requests/:id/reject` | Reject an exchange request | ✅ |
| `POST`| `/api/messages` | Send chat message to partner | ✅ |
| `GET` | `/api/messages/:userId` | Get chat history with partner | ✅ |
| `POST`| `/api/sessions` | Schedule an exchange session | ✅ |
| `POST`| `/api/reviews` | Submit partner rating and review | ✅ |

---

## ⚙️ Getting Started & Installation

### 1. Running the Android Application in Android Studio
1. Clone this repository:
   ```bash
   git clone https://github.com/yashsoni972/SKILL-BARTER.git
   ```
2. Open the project folder in **Android Studio**.
3. Sync Gradle and click **Run 'app'** (`Shift + F10`) to run on an emulator or physical device.
4. To build a standalone APK for wireless testing:
   - Go to menu: **Build ➔ Build Bundle(s) / APK(s) ➔ Build APK(s)**.
   - Install `app-debug.apk` directly on your phone!

### 2. Running the Node.js Backend Locally
1. Navigate to the `backend/` directory:
   ```bash
   cd backend
   ```
2. Install Node dependencies:
   ```bash
   npm install
   ```
3. Create a `.env` file in `backend/`:
   ```env
   PORT=5001
   MONGO_URI=mongodb+srv://<username>:<password>@cluster0.xxx.mongodb.net/skillbarter?retryWrites=true&w=majority
   JWT_SECRET=skill_barter_super_secret_jwt_key_2026_xyz
   ```
4. Seed initial test data (optional):
   ```bash
   npm run seed
   ```
5. Start local server:
   ```bash
   npm run dev
   ```

---

## 👨‍💻 Author & Credits

Developed by **Yash Soni**  
- 📂 **GitHub**: [@yashsoni972](https://github.com/yashsoni972)
- 📌 **Repository**: [https://github.com/yashsoni972/SKILL-BARTER](https://github.com/yashsoni972/SKILL-BARTER)

---

<div align="center">
  <b>Skill Barter</b> • Exchange Skills. Grow Together.
</div>
