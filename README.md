# 🚀 Skill Barter – Peer-to-Peer Skill Exchange Platform

<div align="center">

<svg xmlns="http://www.w3.org/2000/svg" width="120" height="120" viewBox="0 0 108 108">
  <circle cx="54" cy="54" r="44" fill="url(#g1)"/>
  <defs>
    <linearGradient id="g1" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#7C3AED"/>
      <stop offset="50%" stop-color="#6C63FF"/>
      <stop offset="100%" stop-color="#0EA5E9"/>
    </linearGradient>
  </defs>
  <path fill="#fff" d="M32,38 C32,33.6 35.6,30 40,30 L57,30 L50,23 L55,17.3 L72,34.3 L55,51.3 L50,45.7 L57,38.7 L57,42 L40,42 C38.9,42 38,42.9 38,44 L38,52 L32,52 Z"/>
  <path fill="#E0F2FE" fill-opacity="0.92" d="M76,70 C76,74.4 72.4,78 68,78 L51,78 L58,85 L53,90.7 L36,73.7 L53,56.7 L58,62.3 L51,69.3 L51,66 L68,66 C69.1,66 70,65.1 70,64 L70,56 L76,56 Z"/>
  <path fill="#FCD34D" d="M54,47 L56.5,53.5 L63,56 L56.5,58.5 L54,65 L51.5,58.5 L45,56 L51.5,53.5 Z"/>
</svg>

### **"Exchange Skills, Not Money."**

*A modern, peer-to-peer mobile application built with Android Material Design 3 and a Node.js / Express REST API with MongoDB Atlas, Socket.IO real-time chat, GridFS file storage, and Gemini AI–powered learning roadmaps.*

[![Live API](https://img.shields.io/badge/Render-Live%20API-brightgreen?style=for-the-badge&logo=render)](https://skillbarter-api-m0ev.onrender.com/)
[![GitHub](https://img.shields.io/badge/GitHub-SKILL--BARTER-181717?style=for-the-badge&logo=github)](https://github.com/yashsoni972/SKILL-BARTER)
[![Android](https://img.shields.io/badge/Android-Material%203-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com/)
[![Material 3](https://img.shields.io/badge/Material-Design%203-6750A4?style=for-the-badge&logo=material-design)](https://m3.material.io/)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas%20Cloud-47A248?style=for-the-badge&logo=mongodb)](https://www.mongodb.com/cloud/atlas)

</div>

---

## 📌 Table of Contents
- [Overview](#-overview)
- [Key Features](#-key-features)
- [Live Backend](#-live-backend)
- [System Architecture](#-system-architecture--user-journey)
- [Mobile Screens](#-mobile-ui-screens)
- [Tech Stack](#-tech-stack)
- [MongoDB Collections](#-mongodb-collections--database-design)
- [Backend REST API Endpoints](#-backend-rest-api-endpoints)
- [Getting Started](#-getting-started--installation)
- [Author](#-author)

---

## 🌟 Overview

**Skill Barter** is a peer-to-peer mobile platform where people **teach skills they know in exchange for skills they want to learn** — with zero money involved.

**Example exchange:**
- **User A** teaches *JavaScript* → wants to learn *Graphic Design*
- **User B** teaches *Graphic Design* → wants to learn *JavaScript*
- SkillBarter matches them, enables real-time chat, schedules a Google Meet session, tracks credits, and collects reviews.

---

## ✨ Key Features

| Feature | Details |
| :--- | :--- |
| 🎨 **Animated Splash Screen** | Deep purple-to-navy gradient background, animated logo with pulse rings and staggered orb entrance animations |
| 🔐 **JWT Auth** | Register / Login with bcrypt hashing, 30-day JWT stored in SharedPreferences, auto-refreshed on every request |
| 🏠 **Home Dashboard** | Live stats (users / active / completed), recommended partners with skill chips and MaterialCardView |
| 🔍 **Discover & Match** | Filter users by skill, category, or location; compatibility-based match results |
| 📩 **Exchange Requests** | Send / receive / accept / reject requests; one exchange per member pair enforced (409 on duplicate) |
| 💬 **Real-Time Chat** | Socket.IO messaging, typing indicators, file attachments (PDF, Word, Excel, Images via GridFS) |
| 📁 **File Attachments** | Upload up to 10 MB; stored in MongoDB GridFS; signed 15-min download tokens for secure viewing |
| 📅 **Session Scheduling** | Google Meet–only delivery; schedule date/time/link; mark as completed |
| 💰 **Credits Economy** | 40 credits on registration; teacher earns +10/hr, learner pays −10/hr; full transaction history |
| 🔔 **Notifications** | Push-style in-app notifications with unread badge counter |
| ⭐ **Rating & Reviews** | 5-star rating + comment; affects partner's profile average |
| 🏅 **Badges** | Achievement badges unlocked by platform milestones |
| 📈 **Progress Dashboard** | Hours taught/learned per skill, visual progress bars |
| 🤖 **AI Learning Roadmap** | Gemini AI–powered (smart fallback if no key); live step-by-step streaming animation; estimated time per step; mini practice tasks; YouTube search links; curated resource links; progress saved locally; share via WhatsApp/Telegram |
| 🛒 **Marketplace** | Browse and post skill listings |
| 🆘 **Help Requests** | Post community help requests |
| 🗓️ **Availability** | Add/delete weekly availability slots |
| 👤 **Profile** | Gradient hero banner, avatar overlap, 6 preset vector avatars OR custom gallery photo, bio editor |
| 📱 **Android 15 Edge-to-Edge** | `SystemBars.apply()` on every Activity — handles status bar, cutout, nav bar, and IME |

---

## 🌐 Live Backend

| Resource | URL |
| :--- | :--- |
| Base URL | `https://skillbarter-api-m0ev.onrender.com` |
| Health Check | [`/api/health`](https://skillbarter-api-m0ev.onrender.com/api/health) |
| Platform Stats | [`/api/users/stats`](https://skillbarter-api-m0ev.onrender.com/api/users/stats) |
| APK Download | [`/app/download`](https://skillbarter-api-m0ev.onrender.com/app/download) |

> ⚠️ Render free tier sleeps after inactivity. First request may take 30–90 s (the app pre-warms it during the splash screen).

---

## 🏗️ System Architecture & User Journey

```
┌──────────────────────────────────────────┐
│       Skill Barter Android App           │
│  Java · ViewBinding · Material Design 3  │
└────────────────┬─────────────────────────┘
                 │  Retrofit 2 + OkHttp (JWT interceptor)
                 │  Socket.IO (real-time chat)
┌────────────────▼─────────────────────────┐
│     Node.js / Express REST Server        │
│         Deployed on Render               │
│  Socket.IO · JWT · bcrypt · Multer       │
└────────────────┬─────────────────────────┘
                 │  Mongoose ODM
┌────────────────▼─────────────────────────┐
│         MongoDB Atlas Cloud              │
│   Database: "skillbarter"  (15 cols)     │
│   GridFS: file attachments               │
└──────────────────────────────────────────┘
                 │  Gemini 1.5 Flash API
┌────────────────▼─────────────────────────┐
│   Google Gemini AI (Roadmap Generator)   │
│   Smart fallback if no API key set       │
└──────────────────────────────────────────┘
```

### Complete User Journey
```
Install → Splash (animated) → Onboarding → Register/Login
  → Add Skills → Home Dashboard → Discover Partners
  → Send Request → Chat (Socket.IO) → Schedule Session (Meet)
  → Complete Session → Credits Updated → Rate & Review
  → Check Progress → Generate AI Roadmap → Share Roadmap
```

---

## 📱 Mobile UI Screens

| Screen | Description |
| :--- | :--- |
| **Splash Screen** | Deep purple gradient, animated SVG logo with pulse rings, staggered orb entrance, feature pills (Barter / AI Roadmap / Chat) |
| **Onboarding** | 3-slide ViewPager2: Share What You Know · Discover Skills · Grow Together |
| **Login / Register** | Email + password with show/hide eye toggle (`PasswordToggle`); edge-to-edge keyboard handling |
| **Home Dashboard** | Greeting, live stats bar, recommended partners as MaterialCardViews with skill chips |
| **Discover** | Filter by skill / category / location; match result cards with compatibility |
| **Exchange Requests** | Incoming + Outgoing tabs; Accept / Reject actions; deduped by partner |
| **Chat List** | Conversations list with last-message preview; 📎 Attachment preview |
| **Chat Screen** | Real-time Socket.IO messaging; file upload (image/PDF/Word/Excel); in-app PDF viewer; signed download links |
| **Profile** | Gradient banner, avatar overlap, 6 preset avatars or custom gallery photo, bio editor, stats, skills, action rows with icons |
| **Avatar Picker** | Full-width upload button + 3×2 avatar grid dialog |
| **Add / Edit Skills** | Tabbed "I Can Teach" vs "I Want to Learn" |
| **Send Request** | Pre-checks `/requests/with/:userId`; never shows button to already-accepted pairs |
| **Exchange Details** | Date, time, Google Meet link for an accepted exchange |
| **My Exchanges** | Exchange history with partner avatar, status badge, Chat + Rate buttons |
| **Rate & Review** | 5-star interactive bar + comment |
| **Schedule Session** | Book Google Meet session; date/time picker |
| **Notifications** | List + mark-all-read; unread badge on bell icon |
| **Credits** | Balance display, earned/spent totals, transaction history |
| **Progress** | Hours taught/learned; per-skill progress bars |
| **Marketplace** | Browse + post skill listings |
| **Help Requests** | Community help posts |
| **Availability** | Add/delete weekly time slots |
| **AI Learning Roadmap** | Generate path (Gemini AI or smart fallback); live streaming step animation; ⏱ time badge; 🎯 mini task; 📚 resources; ▶ YouTube; 📤 Share; progress saved locally |

---

## 🛠️ Tech Stack

### Android App
| Component | Technology |
| :--- | :--- |
| Language | Java 11 |
| Min / Target / Compile SDK | 24 / 35 / 36 |
| UI | Material Design 3, ViewBinding, ConstraintLayout, RecyclerView, ViewPager2, ChipGroup |
| Architecture | Repository pattern, Singleton, `DataCallback<T>` |
| Networking | Retrofit 2.11, OkHttp 4.12, Gson converter |
| Real-time | Socket.IO Android client |
| Image loading | Glide 4.16 |
| Local storage | SharedPreferences (JWT, user, roadmap progress) |
| Edge-to-edge | `WindowInsetsCompat` via `SystemBars.apply()` |

### Backend
| Component | Technology |
| :--- | :--- |
| Runtime | Node.js + Express 4 |
| Database | MongoDB Atlas (Mongoose 8) |
| File storage | MongoDB GridFS (via Multer) |
| Real-time | Socket.IO 4 |
| Auth | JWT (30-day tokens), bcryptjs |
| AI | Google Gemini 1.5 Flash API |
| Deployment | Render.com |

---

## 🗄️ MongoDB Collections & Database Design

The `skillbarter` database on MongoDB Atlas has **15 collections**:

```
skillbarter/
├── users               – name, email, password(bcrypt), location, bio, profileImage, rating, totalExchanges
├── skills              – userId, skillName, type(offer|want)
├── exchangerequests    – senderId, receiverId, offeredSkill, requestedSkill, message, status(pending|accepted|rejected|completed)
├── messages            – senderId, receiverId, message(optional), attachmentId, read, createdAt
├── attachments         – fileId(GridFS), filename, mimeType, size, uploadedBy, sharedWith, requestId
├── sessions            – requestId, hostUserId, partnerUserId, skill, date, time, meetLink, durationHours, status
├── skillcredits        – userId(unique), balance, earnedTotal, spentTotal
├── credittransactions  – userId, amount, type(earn|spend), description, createdAt
├── reviews             – reviewerId, reviewedUserId, rating, comment, createdAt
├── notifications       – userId, title, message, type, read, createdAt
├── badges              – name, description, icon, criteria
├── userbadges          – userId, badgeId, awardedAt
├── availabilities      – userId, day, startTime, endTime, mode
├── skilllistings       – userId, skill, title, description, createdAt
└── helprequests        – userId, title, description, status, createdAt
```

---

## 📡 Backend REST API Endpoints

### 🔐 Authentication
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/auth/register` | Register new user (grants 40 welcome credits) | ❌ |
| `POST` | `/api/auth/login` | Login and receive 30-day JWT | ❌ |

### 👤 Users
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/users/profile` | Get logged-in user profile + skills | ✅ |
| `PUT` | `/api/users/profile` | Update name, location, bio, profileImage | ✅ |
| `GET` | `/api/users/stats` | Live platform stats (users / active / completed) | ❌ |
| `GET` | `/api/users/search` | Search users by skill or location | ✅ |
| `GET` | `/api/users/recommended` | Get AI-matched recommended partners | ✅ |
| `GET` | `/api/users/discover` | Filter users by skill, category, location | ✅ |
| `GET` | `/api/users/progress` | Hours taught/learned per skill | ✅ |
| `GET` | `/api/users/badges` | Get user's earned badges | ✅ |

### 🛠️ Skills
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/skills/batch` | Add/update offered and wanted skills | ✅ |

### 📩 Exchange Requests
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/requests` | Send new exchange request (`receiverId`, `offeredSkill`, `requestedSkill`, `message`) | ✅ |
| `GET` | `/api/requests/incoming` | Get received requests (deduped by partner) | ✅ |
| `GET` | `/api/requests/outgoing` | Get sent requests (deduped by partner) | ✅ |
| `GET` | `/api/requests/exchanges` | Get all accepted exchanges (deduped by partner) | ✅ |
| `GET` | `/api/requests/with/:userId` | Check relationship with a user (`status`, `canChat`, `canRequest`) | ✅ |
| `PUT` | `/api/requests/:id/accept` | Accept an exchange request | ✅ |
| `PUT` | `/api/requests/:id/reject` | Reject an exchange request | ✅ |
| `PUT` | `/api/requests/:id/complete` | Mark exchange as completed | ✅ |

### 💬 Messages
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/messages` | Send a message (text or attachment) | ✅ |
| `GET` | `/api/messages/conversations` | Get all conversations (deduped by partner) | ✅ |
| `GET` | `/api/messages/:userId` | Get chat history with a specific user | ✅ |

### 📁 Attachments (GridFS)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/attachments` | Upload file (PDF/Word/Excel/Image, max 10 MB) | ✅ |
| `GET` | `/api/attachments/:id` | Download file (JWT header OR `?t=` signed token) | ✅ |
| `GET` | `/api/attachments/:id/link` | Mint a fresh 15-minute signed download link | ✅ |

### 📅 Sessions
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/sessions` | Get all sessions for current user | ✅ |
| `POST` | `/api/sessions` | Schedule a new Google Meet session | ✅ |
| `POST` | `/api/sessions/complete` | Complete session (`requestId`, `durationHours`, `skill`, `iTaught`) — moves credits | ✅ |

### 💰 Credits
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/credits` | Get balance, earned total, spent total + transactions | ✅ |

### 🔔 Notifications
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/notifications` | Get all notifications | ✅ |
| `GET` | `/api/notifications/unread-count` | Get count of unread notifications | ✅ |
| `PUT` | `/api/notifications/read-all` | Mark all notifications as read | ✅ |

### ⭐ Reviews
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/reviews/add` | Submit review (`reviewedUserId`, `rating`, `comment`) | ✅ |
| `GET` | `/api/reviews/mine` | Get reviews written by current user | ✅ |
| `GET` | `/api/reviews/:userId` | Get all reviews for a specific user | ✅ |

### 🗓️ Availability
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/availability/me` | Get current user's availability slots | ✅ |
| `POST` | `/api/availability` | Add a new availability slot | ✅ |
| `DELETE` | `/api/availability/:id` | Delete an availability slot | ✅ |
| `GET` | `/api/availability/:userId` | Get availability for a specific user | ✅ |

### 🛒 Marketplace
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/marketplace` | Browse all skill listings | ✅ |
| `POST` | `/api/marketplace/listings` | Create a new skill listing | ✅ |

### 🆘 Help Requests
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/help-requests` | Browse all help requests | ✅ |
| `POST` | `/api/help-requests` | Post a new help request | ✅ |

### 🏅 Badges
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/badges` | Get all available badges and user's earned ones | ✅ |

### 🤖 AI Roadmap
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/roadmap/generate` | Generate AI learning roadmap (`currentSkill`, `goal`, `experience`) — uses Gemini AI or smart fallback | ✅ |

### 📱 App Version
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/app/version` | Get latest app version for update check | ❌ |
| `GET` | `/api/health` | Server health check | ❌ |
| `GET` | `/app/download` | Download latest `skillbarter.apk` | ❌ |

---

## 💰 Credit Economy

| Event | Credit Change |
| :--- | :--- |
| Account Registration | **+40** (welcome bonus) |
| Teaching a session (per hour) | **+10** |
| Learning in a session (per hour) | **−10** |
| Minimum balance | **0** (never goes negative) |

Every credit movement writes a `CreditTransaction` document for full audit history.

---

## ⚙️ Getting Started & Installation

### 1. Android App (Android Studio)
```bash
git clone https://github.com/yashsoni972/SKILL-BARTER.git
```
1. Open the project in **Android Studio**
2. Sync Gradle (JDK 21 required — pinned in `gradle.properties`)
3. Click **▶ Run** or install `skillbarter.apk` directly on your device

> **Build command (Windows PowerShell):**
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\Android\Android Studio1\jbr"
> .\gradlew.bat --stop
> .\gradlew.bat app:assembleDebug --no-configuration-cache
> ```

### 2. Node.js Backend (Local)
```bash
cd backend
npm install
```

Create `backend/.env`:
```env
PORT=5001
MONGO_URI=mongodb+srv://<user>:<password>@cluster0.xxx.mongodb.net/skillbarter
JWT_SECRET=your_jwt_secret_here
GEMINI_API_KEY=your_gemini_api_key_here   # optional — AI roadmap uses smart fallback without it
CLIENT_ORIGIN=http://localhost:3000
```

```bash
npm run dev
```

> ⚠️ Local server cannot connect to MongoDB Atlas from restricted networks. Use the live Render API for end-to-end testing.

---

## 👨‍💻 Author

Developed by **Yash Soni**

- 📂 **GitHub**: [@yashsoni972](https://github.com/yashsoni972)
- 📌 **Repository**: [https://github.com/yashsoni972/SKILL-BARTER](https://github.com/yashsoni972/SKILL-BARTER)

---

<div align="center">
  <b>Skill Barter</b> · Exchange Skills. Grow Together. 🚀
</div>
