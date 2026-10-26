# Skill Barter — Peer-to-Peer Skill Exchange Platform

<div align="center">

<svg xmlns="http://www.w3.org/2000/svg" width="110" height="110" viewBox="0 0 108 108">
  <defs>
    <linearGradient id="lg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#7C3AED"/>
      <stop offset="50%" stop-color="#6C63FF"/>
      <stop offset="100%" stop-color="#0EA5E9"/>
    </linearGradient>
  </defs>
  <circle cx="54" cy="54" r="46" fill="url(#lg)"/>
  <path fill="#fff" d="M32,38C32,33.6,35.6,30,40,30L57,30L50,23L55,17.3L72,34.3L55,51.3L50,45.7L57,38.7L57,42L40,42C38.9,42,38,42.9,38,44L38,52L32,52Z"/>
  <path fill="#E0F2FE" fill-opacity="0.9" d="M76,70C76,74.4,72.4,78,68,78L51,78L58,85L53,90.7L36,73.7L53,56.7L58,62.3L51,69.3L51,66L68,66C69.1,66,70,65.1,70,64L70,56L76,56Z"/>
  <path fill="#FCD34D" d="M54,47L56.5,53.5L63,56L56.5,58.5L54,65L51.5,58.5L45,56L51.5,53.5Z"/>
</svg>

**Exchange Skills, Not Money.**

*A full-stack, peer-to-peer mobile platform — Android (Java, Material Design 3) + Node.js/Express REST API + MongoDB Atlas + Socket.IO real-time chat + Gemini AI learning roadmaps.*

[![Live API](https://img.shields.io/badge/Render-Live%20API-00C853?style=for-the-badge&logo=render&logoColor=white)](https://skillbarter-api-m0ev.onrender.com/)
[![GitHub](https://img.shields.io/badge/GitHub-SKILL--BARTER-181717?style=for-the-badge&logo=github)](https://github.com/yashsoni972/SKILL-BARTER)
[![Android](https://img.shields.io/badge/Android-SDK%2024--36-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Material 3](https://img.shields.io/badge/Material-Design%203-6750A4?style=for-the-badge&logo=material-design&logoColor=white)](https://m3.material.io/)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-47A248?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/cloud/atlas)

</div>

---

## Table of Contents

1. [Overview](#overview)
2. [Key Features](#key-features)
3. [System Architecture](#system-architecture)
4. [User Journey](#user-journey)
5. [Mobile Screens](#mobile-screens)
6. [Tech Stack](#tech-stack)
7. [Database Design](#database-design)
8. [REST API Reference](#rest-api-reference)
9. [Credit Economy](#credit-economy)
10. [Getting Started](#getting-started)
11. [Author](#author)

---

## Overview

**Skill Barter** eliminates money from skill learning. Users teach what they know and learn what they want — matched by the platform, communicated over real-time chat, and tracked through a built-in credit economy.

**Example:**
> User A knows JavaScript and wants to learn Graphic Design.  
> User B knows Graphic Design and wants to learn JavaScript.  
> Skill Barter detects the mutual match, connects them, facilitates a Google Meet session, and automatically adjusts both their credit balances when the session completes.

The platform is fully live — the Android app talks to a production REST API deployed on Render with a MongoDB Atlas cloud database.

---

## Key Features

### Authentication & Onboarding
- Secure registration with bcrypt password hashing
- 30-day JWT tokens stored in SharedPreferences and auto-attached to every API request
- 3-slide onboarding ViewPager2 on first launch
- Animated splash screen with deep purple gradient, pulse-ring logo animation, and staggered orb entrance

### Home & Discovery
- Live platform statistics (registered users, active exchanges, completed sessions)
- Recommended partner cards with offered/wanted skill chips
- Discover screen — filter users by skill, category, or location
- Real-time search across the user base

### Exchange Flow
- Send, receive, accept, or reject skill exchange requests
- One active exchange per member pair — the app pre-checks relationship status before showing any action button
- Full exchange history with partner avatar, status badge, and action buttons

### Real-Time Chat
- Socket.IO messaging with typing indicators
- File attachments — images, PDF, Word, Excel — up to 10 MB
- Files stored in MongoDB GridFS with 15-minute signed download tokens (works in PDF viewers and browsers without Authorization headers)
- In-app PDF rendering via Android `PdfRenderer`

### Session & Credits
- Schedule Google Meet sessions with date, time, and link
- On session completion: teacher receives **+10 credits/hour**, learner is charged **−10 credits/hour**
- Every new account starts with **40 welcome credits**
- Balance is clamped at zero — it never goes negative
- Full transaction history with earn/spend ledger

### AI Learning Roadmap
- Powered by Google Gemini 1.5 Flash — with a domain-aware smart fallback that works without an API key
- Live streaming animation: steps appear one by one as if generated in real time
- Each step includes: estimated time, a concrete mini practice task, curated resource links, and a direct YouTube search button
- Progress saved locally — survives app close and reopen
- Share any roadmap as formatted text via WhatsApp, Telegram, or any sharing app

### Profile & Customisation
- Gradient banner hero card with avatar overlap
- 6 built-in vector avatars or upload any photo from the gallery
- Bio editor, skill chips (I Can Teach / I Want to Learn), stats counters
- Profile photo loads across the whole app — home cards, exchange cards, chat list

### Notifications & Badges
- In-app notification centre with unread badge counter on the bell icon
- Achievement badges unlocked by platform milestones

### Additional Screens
- Progress dashboard — hours taught and learned per skill
- Marketplace — browse and post skill listings
- Help Requests — community help posts
- Availability manager — add and delete weekly time slots

---

## System Architecture

```
┌─────────────────────────────────────────────┐
│          Android App  (Java)                │
│  Material Design 3 · ViewBinding · Retrofit │
│  OkHttp (JWT interceptor) · Socket.IO       │
│  Glide · SharedPreferences                  │
└──────────────────┬──────────────────────────┘
                   │  HTTPS REST  +  WebSocket
┌──────────────────▼──────────────────────────┐
│     Node.js / Express  (Render.com)         │
│  JWT · bcryptjs · Multer · Socket.IO        │
│  Mongoose · Gemini AI API                   │
└──────────────────┬──────────────────────────┘
                   │  Mongoose ODM
┌──────────────────▼──────────────────────────┐
│         MongoDB Atlas  (Cloud)              │
│  15 collections · GridFS file storage       │
└─────────────────────────────────────────────┘
```

---

## User Journey

```
Install
  └─► Splash (animated logo)
        └─► Onboarding (first launch)
              └─► Register / Login
                    └─► Add Skills (Teach + Learn)
                          └─► Home Dashboard
                                ├─► Discover Partners
                                │     └─► Send Exchange Request
                                │           └─► Accept / Reject
                                │                 └─► Chat (Socket.IO)
                                │                       └─► Schedule Session (Google Meet)
                                │                             └─► Complete Session
                                │                                   ├─► Credits Updated
                                │                                   └─► Rate & Review
                                ├─► AI Learning Roadmap
                                └─► Profile → Credits / Progress / Exchanges / Availability
```

---

## Mobile Screens

| Screen | Purpose |
|--------|---------|
| Splash | Animated gradient background, pulsing logo, auto-routes based on session state |
| Onboarding | 3-slide introduction shown once on first install |
| Login / Register | Email + password; show/hide toggle; edge-to-edge keyboard handling |
| Home Dashboard | Greeting, live stats bar, recommended partner cards with skill chips |
| Discover | Filter partners by skill / category / location |
| Exchange Requests | Incoming and outgoing tabs; Accept / Reject actions |
| Chat List | All conversations with last-message preview; 📎 shown for attachments |
| Chat | Real-time messages; file upload; in-app PDF viewer; signed file download |
| Profile | Gradient hero, avatar customisation, bio, skills, stats, all action rows |
| Avatar Picker | Upload from gallery or choose one of 6 built-in vector avatars |
| Add / Edit Skills | Tabbed "I Can Teach" and "I Want to Learn" |
| Send Request | Shows relationship pre-check; never shown to already-connected pairs |
| Exchange Details | Date, time, Google Meet link for an accepted exchange |
| My Exchanges | Full history with partner avatar, status badge, Chat and Rate buttons |
| Rate & Review | 5-star interactive bar + optional comment |
| Schedule Session | Book Google Meet with date/time picker |
| Notifications | List + mark-all-read; unread badge on bell |
| Credits | Balance card, earned/spent totals, full transaction history |
| Progress | Per-skill hours taught and learned with progress bars |
| AI Learning Roadmap | Live step streaming, time estimates, mini tasks, YouTube links, share |
| Marketplace | Browse and post skill listings |
| Help Requests | Community assistance posts |
| Availability | Weekly slot management |

---

## Tech Stack

### Android Application

| Component | Details |
|-----------|---------|
| Language | Java 11 |
| SDK | minSdk 24 · targetSdk 35 · compileSdk 36 |
| UI | Material Design 3 · ViewBinding · RecyclerView · ViewPager2 · ChipGroup |
| Architecture | Repository pattern · Singleton · `DataCallback<T>` interface |
| Networking | Retrofit 2.11 · OkHttp 4.12 · Gson converter |
| Real-time | Socket.IO Android client |
| Image loading | Glide 4.16 |
| File viewing | Android `PdfRenderer` |
| Local storage | SharedPreferences (JWT · User · Roadmap progress) |
| Edge-to-edge | `WindowInsetsCompat` via `SystemBars.apply()` on every Activity |

### Backend Service

| Component | Details |
|-----------|---------|
| Runtime | Node.js + Express 4 |
| Database | MongoDB Atlas · Mongoose 8 |
| File storage | MongoDB GridFS via Multer |
| Real-time | Socket.IO 4 |
| Authentication | JWT (30-day expiry) · bcryptjs |
| AI integration | Google Gemini 1.5 Flash API |
| Deployment | Render.com (free tier) |

---

## Database Design

The `skillbarter` MongoDB Atlas database contains **15 collections**:

```
skillbarter/
│
├── users                 name, email, passwordHash, location, bio, profileImage,
│                         rating, totalExchanges
│
├── skills                userId, skillName, type (offer | want)
│
├── exchangerequests      senderId, receiverId, offeredSkill, requestedSkill,
│                         message, status (pending | accepted | rejected | completed)
│
├── messages              senderId, receiverId, message (optional), attachmentId,
│                         read, createdAt
│
├── attachments           fileId (GridFS), filename, mimeType, size,
│                         uploadedBy, sharedWith, requestId
│
├── sessions              requestId, hostUserId, partnerUserId, skill,
│                         date, time, meetLink, durationHours, status
│
├── skillcredits          userId (unique), balance, earnedTotal, spentTotal
│
├── credittransactions    userId, amount, type (earn | spend), description, createdAt
│
├── reviews               reviewerId, reviewedUserId, rating, comment, createdAt
│
├── notifications         userId, title, message, type, read, createdAt
│
├── badges                name, description, icon, criteria
│
├── userbadges            userId, badgeId, awardedAt
│
├── availabilities        userId, day, startTime, endTime, mode
│
├── skilllistings         userId, skill, title, description, createdAt
│
└── helprequests          userId, title, description, status, createdAt
```

---

## REST API Reference

**Base URL:** `https://skillbarter-api-m0ev.onrender.com/api`

> ✅ = requires `Authorization: Bearer <JWT>` header

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `POST` | `/auth/register` | Register a new account — grants 40 welcome credits | — |
| `POST` | `/auth/login` | Login and receive a 30-day JWT | — |

### Users

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `GET` | `/users/profile` | Get the logged-in user's profile and skills | ✅ |
| `PUT` | `/users/profile` | Update name, location, bio, profileImage | ✅ |
| `GET` | `/users/stats` | Live platform statistics | — |
| `GET` | `/users/search` | Search users by skill or location | ✅ |
| `GET` | `/users/recommended` | Get skill-matched recommended partners | ✅ |
| `GET` | `/users/discover` | Filter users by skill, category, location | ✅ |
| `GET` | `/users/progress` | Hours taught and learned per skill | ✅ |
| `GET` | `/users/badges` | User's earned badges | ✅ |

### Skills

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `POST` | `/skills/batch` | Batch update offered and wanted skills | ✅ |

### Exchange Requests

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `POST` | `/requests` | Send a new exchange request | ✅ |
| `GET` | `/requests/incoming` | Get received requests (newest per partner) | ✅ |
| `GET` | `/requests/outgoing` | Get sent requests (newest per partner) | ✅ |
| `GET` | `/requests/exchanges` | Get all accepted exchanges | ✅ |
| `GET` | `/requests/with/:userId` | Check relationship status with a user | ✅ |
| `PUT` | `/requests/:id/accept` | Accept a request | ✅ |
| `PUT` | `/requests/:id/reject` | Reject a request | ✅ |
| `PUT` | `/requests/:id/complete` | Mark an exchange as completed | ✅ |

### Messages

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `POST` | `/messages` | Send a message (text or attachment) | ✅ |
| `GET` | `/messages/conversations` | All conversations (newest per partner) | ✅ |
| `GET` | `/messages/:userId` | Chat history with a specific user | ✅ |

### File Attachments (GridFS)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `POST` | `/attachments` | Upload a file (max 10 MB — PDF, Word, Excel, Image) | ✅ |
| `GET` | `/attachments/:id` | Download file — accepts JWT header or `?t=` signed token | ✅ |
| `GET` | `/attachments/:id/link` | Mint a fresh 15-minute signed download link | ✅ |

### Sessions

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `GET` | `/sessions` | Get all sessions for the current user | ✅ |
| `POST` | `/sessions` | Schedule a Google Meet session | ✅ |
| `POST` | `/sessions/complete` | Complete session and move credits (`requestId`, `durationHours`, `skill`, `iTaught`) | ✅ |

### Credits

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `GET` | `/credits` | Balance, totals, and full transaction history | ✅ |

### Notifications

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `GET` | `/notifications` | All notifications for current user | ✅ |
| `GET` | `/notifications/unread-count` | Count of unread notifications | ✅ |
| `PUT` | `/notifications/read-all` | Mark all notifications as read | ✅ |

### Reviews

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `POST` | `/reviews/add` | Submit a review (`reviewedUserId`, `rating`, `comment`) | ✅ |
| `GET` | `/reviews/mine` | Reviews written by the current user | ✅ |
| `GET` | `/reviews/:userId` | All reviews for a specific user | ✅ |

### Availability

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `GET` | `/availability/me` | Current user's availability slots | ✅ |
| `POST` | `/availability` | Add a new availability slot | ✅ |
| `DELETE` | `/availability/:id` | Remove an availability slot | ✅ |
| `GET` | `/availability/:userId` | Availability for any user | ✅ |

### Marketplace

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `GET` | `/marketplace` | Browse all skill listings | ✅ |
| `POST` | `/marketplace/listings` | Post a new skill listing | ✅ |

### Help Requests

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `GET` | `/help-requests` | Browse all help requests | ✅ |
| `POST` | `/help-requests` | Post a new help request | ✅ |

### Badges

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `GET` | `/badges` | All badges and user's earned ones | ✅ |

### AI Roadmap

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `POST` | `/roadmap/generate` | Generate a personalised learning roadmap (`currentSkill`, `goal`, `experience`) | ✅ |

### App & Health

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|:----:|
| `GET` | `/app/version` | Latest app version for update check | — |
| `GET` | `/api/health` | Server health check | — |
| `GET` | `/app/download` | Download latest `skillbarter.apk` | — |

---

## Credit Economy

Every credit movement is recorded in the `credittransactions` collection for a full audit trail.

| Event | Change |
|-------|--------|
| New account registration | +40 (welcome bonus) |
| Teaching a session (per hour) | +10 |
| Learning in a session (per hour) | −10 |
| Minimum balance | 0 (never negative) |

Credits are visible in the Credits screen which shows current balance, lifetime earned, lifetime spent, and the full transaction ledger.

---

## Getting Started

### Android App

```bash
git clone https://github.com/yashsoni972/SKILL-BARTER.git
```

Open in **Android Studio**, sync Gradle, and click **Run**.

> The app connects to the live production API by default. No local backend setup is needed to run the app.

**Build from command line (Windows):**

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio1\jbr"
.\gradlew.bat --stop
.\gradlew.bat app:assembleDebug --no-configuration-cache
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Backend (Local Development)

```bash
cd backend
npm install
```

Create `backend/.env`:

```env
PORT=5001
MONGO_URI=mongodb+srv://<user>:<password>@cluster0.mongodb.net/skillbarter
JWT_SECRET=your_jwt_secret
GEMINI_API_KEY=your_gemini_key   # optional — smart fallback used if absent
CLIENT_ORIGIN=http://localhost:3000
```

```bash
npm run dev
```

To point the Android app at your local server, build with:

```powershell
.\gradlew.bat app:assembleDebug -PapiBaseUrl=http://10.0.2.2:5001/api/
```

---

## Live Endpoints

| Resource | URL |
|----------|-----|
| API root | https://skillbarter-api-m0ev.onrender.com |
| Health check | https://skillbarter-api-m0ev.onrender.com/api/health |
| Platform stats | https://skillbarter-api-m0ev.onrender.com/api/users/stats |
| APK download | https://skillbarter-api-m0ev.onrender.com/app/download |

---

## Author

**Yash Soni**

- GitHub: [@yashsoni972](https://github.com/yashsoni972)
- Repository: [github.com/yashsoni972/SKILL-BARTER](https://github.com/yashsoni972/SKILL-BARTER)

---

<div align="center">
Skill Barter &nbsp;·&nbsp; Exchange Skills. Grow Together.
</div>
