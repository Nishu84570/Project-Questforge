# 🚀 QuestForge

> **A full-stack gamified coding challenge platform built with Spring Boot and React.**

QuestForge is a full-stack web application designed around coding challenges, user progress, XP, achievements, quests, streaks, leaderboards, submissions, and administrative management.

The application uses **Java Spring Boot** for the backend REST API and **React + Vite** for the frontend.

---

## ✨ Features

### 👤 User Features

* 🔐 User registration and login
* 🛡️ JWT-based authentication
* 👤 User profile management
* 🎯 Coding challenges
* 🧩 Questions and multiple-choice options
* 📝 Challenge attempts and submissions
* 📊 Submission results and status tracking
* ⭐ XP and progression system
* 🔥 User streak tracking
* 🏆 Achievements
* 📅 Daily quests
* 📆 Weekly quests
* 🥇 Leaderboards
* 🔔 Notifications
* 🌙 Theme toggle

---

### 🏆 Gamification

QuestForge includes several gamification systems designed to make solving challenges more engaging:

* ⭐ XP system
* 🔥 Streak system
* 🏅 Achievements
* 🎯 Daily quests
* 📅 Weekly quests
* 🥇 Leaderboards
* 📈 Progress tracking

---

### 🛡️ Authentication & Security

The backend includes:

* JWT authentication
* JWT authentication filter
* Spring Security configuration
* Protected routes
* Admin route protection
* Custom user details service
* Role-based access control

---

### 👨‍💼 Admin Features

QuestForge includes a dedicated administration system with:

* 📊 Admin dashboard
* 👥 User management
* 🧩 Challenge management
* 📝 Submission management
* 🔔 Notification management
* 📈 Challenge analytics
* 📊 Dashboard statistics
* ⚡ Admin quick links
* 👤 User role management
* ⭐ User XP management

---

# 🛠️ Tech Stack

## Backend

| Technology         | Purpose                        |
| ------------------ | ------------------------------ |
| ☕ Java             | Backend programming language   |
| 🌱 Spring Boot     | Backend framework              |
| 🔐 Spring Security | Authentication & authorization |
| 🔑 JWT             | Token-based authentication     |
| 📦 Maven           | Dependency management & build  |
| 🌐 REST API        | Frontend-backend communication |

## Frontend

| Technology | Purpose             |
| ---------- | ------------------- |
| ⚛️ React   | UI library          |
| ⚡ Vite     | Frontend build tool |
| 📦 npm     | Package management  |
| 🎨 CSS     | Styling             |

---

# 🏗️ Architecture

QuestForge follows a **full-stack client-server architecture**.

```text
┌─────────────────────────────────────────────┐
│                  Frontend                   │
│                                             │
│              React + Vite                   │
│                                             │
│  Pages → Components → API Service           │
└──────────────────────┬──────────────────────┘
                       │
                       │ HTTP / REST API
                       │
                       ▼
┌─────────────────────────────────────────────┐
│                  Backend                    │
│                                             │
│               Spring Boot                  │
│                                             │
│ Controller → Service → Repository           │
│                  ↓                          │
│                Models                       │
│                  ↓                          │
│               Database                      │
└─────────────────────────────────────────────┘
```

---

# 📁 Project Structure

```text
QuestForge/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── questforge/
│   │   │       │
│   │   │       ├── config/
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   ├── OpenAPIConfig.java
│   │   │       │   └── SecurityConfig.java
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── AchievementController.java
│   │   │       │   ├── AdminChallengeController.java
│   │   │       │   ├── AdminDashboardController.java
│   │   │       │   ├── AdminSubmissionController.java
│   │   │       │   ├── AdminUserController.java
│   │   │       │   ├── AnswerController.java
│   │   │       │   ├── AttemptController.java
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── ChallengeAnswerController.java
│   │   │       │   ├── ChallengeController.java
│   │   │       │   ├── DailyQuestController.java
│   │   │       │   ├── LeaderboardController.java
│   │   │       │   ├── NotificationController.java
│   │   │       │   ├── OptionController.java
│   │   │       │   ├── ProfileController.java
│   │   │       │   ├── QuestionController.java
│   │   │       │   ├── StreakController.java
│   │   │       │   ├── SubmissionController.java
│   │   │       │   ├── UserController.java
│   │   │       │   ├── UserProfileController.java
│   │   │       │   └── WeeklyQuestController.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   └── ... DTO classes
│   │   │       │
│   │   │       ├── exception/
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── ResourceNotFoundException.java
│   │   │       │
│   │   │       ├── model/
│   │   │       │   ├── Achievement.java
│   │   │       │   ├── Answer.java
│   │   │       │   ├── Attempt.java
│   │   │       │   ├── Challenge.java
│   │   │       │   ├── DailyQuest.java
│   │   │       │   ├── Notification.java
│   │   │       │   ├── Option.java
│   │   │       │   ├── Question.java
│   │   │       │   ├── Submission.java
│   │   │       │   ├── User.java
│   │   │       │   ├── UserStreak.java
│   │   │       │   └── WeeklyQuest.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   └── ... Repository classes
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── AchievementService.java
│   │   │       │   ├── AdminChallengeService.java
│   │   │       │   ├── AdminDashboardService.java
│   │   │       │   ├── AdminSubmissionService.java
│   │   │       │   ├── AdminUserService.java
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── ChallengeService.java
│   │   │       │   ├── DailyQuestService.java
│   │   │       │   ├── JwtService.java
│   │   │       │   ├── LeaderboardService.java
│   │   │       │   ├── NotificationService.java
│   │   │       │   ├── ProfileService.java
│   │   │       │   ├── StreakService.java
│   │   │       │   ├── SubmissionService.java
│   │   │       │   ├── UserService.java
│   │   │       │   ├── WeeklyQuestService.java
│   │   │       │   └── XPService.java
│   │   │       │
│   │   │       └── QuestforgeApplication.java
│   │   │
│   │   └── resources/
│   │
│   └── test/
│       └── java/
│           └── questforge/
│               └── QuestforgeApplicationTests.java
│
├── questforge-frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── AchievementCard.jsx
│   │   │   ├── AdminNotificationModal.jsx
│   │   │   ├── AdminQuickLinks.jsx
│   │   │   ├── AdminRoute.jsx
│   │   │   ├── ChallengeCard.jsx
│   │   │   ├── Navbar.jsx
│   │   │   ├── NotificationBell.jsx
│   │   │   ├── ProtectedRoute.jsx
│   │   │   ├── SubmissionCard.jsx
│   │   │   └── ThemeToggle.jsx
│   │   │
│   │   ├── pages/
│   │   │   ├── Achievements.jsx
│   │   │   ├── AdminChallenges.jsx
│   │   │   ├── AdminDashboard.jsx
│   │   │   ├── AdminDashboardOverview.jsx
│   │   │   ├── AdminDashboardStats.jsx
│   │   │   ├── AdminNotifications.jsx
│   │   │   ├── AdminSubmissions.jsx
│   │   │   ├── AdminUsers.jsx
│   │   │   ├── ChallengeAnalytics.jsx
│   │   │   ├── Challenges.jsx
│   │   │   ├── Home.jsx
│   │   │   ├── Leaderboard.jsx
│   │   │   ├── Login.jsx
│   │   │   ├── Notifications.jsx
│   │   │   ├── Profile.jsx
│   │   │   ├── Register.jsx
│   │   │   ├── SolveChallenge.jsx
│   │   │   ├── SubmissionResult.jsx
│   │   │   └── Submissions.jsx
│   │   │
│   │   ├── services/
│   │   │   └── api.js
│   │   │
│   │   ├── utils/
│   │   ├── App.jsx
│   │   ├── index.css
│   │   └── main.jsx
│   │
│   ├── index.html
│   ├── package-lock.json
│   ├── package.json
│   └── vite.config.js
│
├── .gitignore
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

# ⚙️ Prerequisites

Make sure the following are installed:

* ☕ Java JDK
* 📦 Maven or Maven Wrapper
* 🟢 Node.js
* 📦 npm
* 🔧 Git

Check your installations:

```bash
java -version
```

```bash
node -v
```

```bash
npm -v
```

```bash
git --version
```

---

# 🚀 Installation

## 1. Clone the Repository

```bash
git clone https://github.com/Nishu84570/QuestForge.git
```

Navigate into the project:

```bash
cd QuestForge
```

---

# 🖥️ Backend Setup

QuestForge's backend is built with Java and Spring Boot.

From the project root:

### Windows

```bash
.\mvnw.cmd spring-boot:run
```

### macOS / Linux

```bash
./mvnw spring-boot:run
```

The backend will start on the port configured in the Spring Boot application.

---

# 🎨 Frontend Setup

Open a new terminal.

Navigate to the frontend:

```bash
cd questforge-frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

Vite will display the local development URL in the terminal.

Open the displayed URL in your browser.

---

# 🔐 Authentication Flow

QuestForge uses JWT-based authentication.

The basic authentication flow is:

```text
User
 │
 ▼
Login / Register
 │
 ▼
AuthController
 │
 ▼
AuthService
 │
 ▼
JWT Token
 │
 ▼
Frontend
 │
 ▼
Protected API Requests
 │
 ▼
JwtAuthenticationFilter
 │
 ▼
Spring Security
 │
 ▼
Authorized Resource
```

Protected frontend routes are handled through:

```text
ProtectedRoute.jsx
```

Administrative routes are protected through:

```text
AdminRoute.jsx
```

---

# 🧩 Core Backend Architecture

The backend follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Controller Layer

Handles HTTP requests and API endpoints.

### Service Layer

Contains application/business logic.

### Repository Layer

Handles database access.

### Model Layer

Contains application entities such as:

* User
* Challenge
* Question
* Answer
* Option
* Attempt
* Submission
* Achievement
* DailyQuest
* WeeklyQuest
* Notification
* UserStreak

### DTO Layer

Handles request and response data transfer between the API and clients.

### Exception Layer

Provides centralized exception handling through:

```text
GlobalExceptionHandler
```

---

# 🌐 API

The backend exposes REST APIs for major application modules, including:

* Authentication
* Users
* Profiles
* Challenges
* Questions
* Answers
* Options
* Attempts
* Submissions
* Achievements
* Daily quests
* Weekly quests
* Leaderboards
* Notifications
* Streaks
* Admin operations

The project also contains an `OpenAPIConfig`, allowing API documentation tooling to be configured.


# 🧪 Testing

The project contains Spring Boot tests.

Run backend tests using:

### Windows

```bash
.\mvnw.cmd test
```

### macOS / Linux

```bash
./mvnw test
```

---

# 🔄 Git Workflow

For development, a feature-branch workflow can be used:

```bash
git switch -c 
```

After making changes:

```bash
git status
```

Stage changes:

```bash
git add .
```

Commit:

```bash
git commit -m 
```

Push the branch:

```bash
git push origin 
```

Then create a Pull Request for review.

---

# 🤝 Contributing

Contributions and suggestions are welcome.

### 1. Fork the repository

### 2. Create a feature branch

```bash
git switch -c feature/your-feature
```

### 3. Make your changes

### 4. Commit your changes

```bash
git add .
git commit -m "Add your feature"
```

### 5. Push your branch

```bash
git push origin feature/your-feature
```

### 6. Open a Pull Request

Describe what you changed and why.

---

# 📄 License

This project is currently developed for learning, experimentation, and portfolio purposes.

A formal open-source license can be added if the project is intended for public distribution.

---

# 👨‍💻 Author

## Nishu

GitHub: **[@Nishu84570](https://github.com/Nishu84570)**

---

## ⭐ Project

If you find QuestForge interesting, consider giving the repository a ⭐ on GitHub.

**Built with ☕ Java + 🌱 Spring Boot + ⚛️ React + ⚡ Vite**
