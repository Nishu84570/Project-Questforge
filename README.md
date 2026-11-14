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
│   │
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
│   │   │       │   ├── AchievementResponseDTO.java
│   │   │       │   ├── AdminChallengeOverviewDTO.java
│   │   │       │   ├── AdminDashboardResponseDTO.java
│   │   │       │   ├── AdminSubmissionResponseDTO.java
│   │   │       │   ├── AdminSubmissionStatusRequestDTO.java
│   │   │       │   ├── AdminUserResponseDTO.java
│   │   │       │   ├── AdminUserRoleRequestDTO.java
│   │   │       │   ├── AdminUserXpRequestDTO.java
│   │   │       │   ├── AnswerRequestDTO.java
│   │   │       │   ├── AnswerResponseDTO.java
│   │   │       │   ├── AttemptRequestDTO.java
│   │   │       │   ├── AttemptResponseDTO.java
│   │   │       │   ├── ChallengeAnalyticsResponseDTO.java
│   │   │       │   ├── ChallengeRequestDTO.java
│   │   │       │   ├── ChallengeResponseDTO.java
│   │   │       │   ├── DailyQuestResponseDTO.java
│   │   │       │   ├── GlobalLeaderboardEntryDTO.java
│   │   │       │   ├── LeaderboardEntryDTO.java
│   │   │       │   ├── LeaderboardResponseDTO.java
│   │   │       │   ├── LoginRequestDTO.java
│   │   │       │   ├── LoginResponseDTO.java
│   │   │       │   ├── NotificationBroadcastRequestDTO.java
│   │   │       │   ├── NotificationResponseDTO.java
│   │   │       │   ├── OptionRequestDTO.java
│   │   │       │   ├── OptionResponseDTO.java
│   │   │       │   ├── ProfileProgressDTO.java
│   │   │       │   ├── ProfileUpdateRequestDTO.java
│   │   │       │   ├── QuestionRequestDTO.java
│   │   │       │   ├── QuestionResponseDTO.java
│   │   │       │   ├── SubmissionAnswerDTO.java
│   │   │       │   ├── SubmissionRequestDTO.java
│   │   │       │   ├── SubmissionResponseDTO.java
│   │   │       │   ├── SubmissionStatusRequestDTO.java
│   │   │       │   ├── UserProfileDTO.java
│   │   │       │   ├── UserRequestDTO.java
│   │   │       │   ├── UserResponseDTO.java
│   │   │       │   ├── UserUpdateRequestDTO.java
│   │   │       │   └── WeeklyQuestResponseDTO.java
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
│   │   │       │   ├── AchievementRepository.java
│   │   │       │   ├── AnswerRepository.java
│   │   │       │   ├── AttemptRepository.java
│   │   │       │   ├── ChallengeRepository.java
│   │   │       │   ├── DailyQuestRepository.java
│   │   │       │   ├── NotificationRepository.java
│   │   │       │   ├── OptionRepository.java
│   │   │       │   ├── QuestionRepository.java
│   │   │       │   ├── SubmissionRepository.java
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── UserStreakRepository.java
│   │   │       │   └── WeeklyQuestRepository.java
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
│               │
│               ├── controller/
│               │   ├── AchievementControllerTest.java
│               │   ├── AdminChallengeOverviewControllerTest.java
│               │   ├── AdminDashboardControllerTest.java
│               │   ├── AdminSubmissionControllerTest.java
│               │   ├── AdminUserControllerTest.java
│               │   ├── AnswerControllerTest.java
│               │   ├── AttemptControllerTest.java
│               │   ├── AuthControllerTest.java
│               │   ├── ChallengeAnalyticsControllerTest.java
│               │   ├── ChallengeControllerTest.java
│               │   ├── DailyQuestControllerTest.java
│               │   ├── LeaderboardControllerTest.java
│               │   ├── NotificationControllerTest.java
│               │   ├── OptionControllerTest.java
│               │   ├── ProfileControllerTest.java
│               │   ├── QuestionControllerTest.java
│               │   ├── StreakControllerTest.java
│               │   ├── SubmissionControllerTest.java
│               │   ├── UserControllerTest.java
│               │   ├── UserProfileControllerTest.java
│               │   ├── WeeklyQuestControllerTest.java
│               │   └── XPControllerTest.java
│               │
│               ├── exception/
│               │   ├── GlobalExceptionHandlerTest.java
│               │   └── ResourceNotFoundExceptionTest.java
│               │
│               ├── repository/
│               │   ├── AchievementRepositoryTest.java
│               │   ├── AnswerRepositoryTest.java
│               │   ├── AttemptRepositoryTest.java
│               │   ├── ChallengeRepositoryTest.java
│               │   ├── DailyQuestRepositoryTest.java
│               │   ├── NotificationRepositoryTest.java
│               │   ├── OptionRepositoryTest.java
│               │   ├── QuestionRepositoryTest.java
│               │   ├── SubmissionRepositoryTest.java
│               │   ├── UserRepositoryTest.java
│               │   ├── UserStreakRepositoryTest.java
│               │   └── WeeklyQuestRepositoryTest.java
│               │
│               ├── service/
│               │   ├── AchievementServiceTest.java
│               │   ├── AdminChallengeOverviewServiceTest.java
│               │   ├── AdminDashboardServiceTest.java
│               │   ├── AdminSubmissionServiceTest.java
│               │   ├── AdminUserServiceTest.java
│               │   ├── AnswerServiceTest.java
│               │   ├── AttemptServiceTest.java
│               │   ├── AuthServiceTest.java
│               │   ├── ChallengeAnalyticsServiceTest.java
│               │   ├── ChallengesServiceTest.java
│               │   ├── CustomUserDetailsServiceTest.java
│               │   ├── DailyQuestServiceTest.java
│               │   ├── JwtServiceTest.java
│               │   ├── LeaderboardServiceTest.java
│               │   ├── NotificationServiceTest.java
│               │   ├── OptionServiceTest.java
│               │   ├── ProfileServiceTest.java
│               │   ├── QuestionServiceTest.java
│               │   ├── StreakServiceTest.java
│               │   ├── SubmissionServiceTest.java
│               │   ├── UserProfileServiceTest.java
│               │   ├── UserServiceTest.java
│               │   ├── WeeklyQuestServiceTest.java
│               │   └── XPServiceTest.java
│               │
│               └── QuestforgeApplicationTests.java
│
├── questforge-frontend/
│   │
│   ├── src/
│   │   │
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
│   │   ├── styles/
│   │   │   ├── AchievementCard.css
│   │   │   ├── Achievements.css
│   │   │   ├── AdminChallengeOverview.css
│   │   │   ├── AdminDashboard.css
│   │   │   ├── AdminDashboardAnalytics.css
│   │   │   ├── AdminDashboardOverview.css
│   │   │   ├── AdminNotificationLink.css
│   │   │   ├── AdminNotifications.css
│   │   │   ├── AdminQuickLinks.css
│   │   │   ├── AdminSubmissions.css
│   │   │   ├── AdminUsers.css
│   │   │   ├── AdminUserSubmissions.css
│   │   │   ├── ChallengeAnalytics.css
│   │   │   ├── ChallengeCard.css
│   │   │   ├── Challenges.css
│   │   │   ├── Home.css
│   │   │   ├── Leaderboard.css
│   │   │   ├── Login.css
│   │   │   ├── Navbar.css
│   │   │   ├── NotificationBell.css
│   │   │   ├── Notifications.css
│   │   │   ├── Profile.css
│   │   │   ├── Register.css
│   │   │   ├── SolveChallenge.css
│   │   │   ├── Streak.css
│   │   │   ├── StreakCalendar.css
│   │   │   ├── SubmissionCard.css
│   │   │   ├── SubmissionResult.css
│   │   │   ├── Submissions.css
│   │   │   └── ThemeToggle.css
│   │   │
│   │   ├── services/
│   │   │   └── api.js
│   │   │
│   │   ├── utils/
│   │   │   └── auth.js
│   │   │
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
git switch -c feature/your-feature
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
git commit -m "Describe your changes"
```

Push the branch:

```bash
git push origin feature/your-feature
```

Then create a Pull Request for review.

---

# 🔒 Security

Do not commit sensitive information to the repository.

Never upload:

* Passwords
* JWT secrets
* API keys
* Database credentials
* Private tokens
* Production secrets
* `.env` files containing credentials

Use environment variables or appropriate application configuration for sensitive values.

---

# 🗺️ Future Improvements

Potential improvements for future versions include:

* 🧪 More comprehensive unit and integration tests
* 🐳 Docker containerization
* 🔄 CI/CD pipeline
* ☁️ Cloud deployment
* 📊 More detailed analytics
* 🔍 Advanced challenge filtering
* 🏆 Expanded gamification system
* 📈 More detailed user progress tracking
* ⚡ Performance optimization
* 📱 Further responsive UI improvements

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
