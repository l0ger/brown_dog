# Maplewood High School — Course Planning System

A full-stack web application that allows students to browse available courses, build their semester schedule, and track their graduation progress.

---

## How to Run

### Backend (Spring Boot — port 8080)

```bash
cd backend
mvn spring-boot:run
```

### Frontend (React — port 3000)

```bash
cd frontend
npm install
npm start
```


## Architecture

### Backend — Domain-Driven Design (DDD)

```
backend/src/main/java/com/maplewood/
├── controller/      # HTTP adapters only — no business logic
├── service/         # Application flow and business rule enforcement
├── domain/model/    # JPA entities mapped to SQLite tables
├── repository/      # Spring Data JPA interfaces
├── dto/             # Separate request and response records
├── exception/       # Typed exceptions + single GlobalExceptionHandler
└── config/          # CORS, DataInitializer (seeds course sections)
```

**SOLID principles applied throughout:**
- Each service class has one responsibility (CourseService reads courses, EnrollmentService handles enrollment logic)
- New validation rules are added as new exception classes, not new `if` branches
- All dependencies are injected via constructor — no `new` in service code

### Frontend — Redux Toolkit

```
frontend/src/
├── store/slices/    # coursesSlice, sectionsSlice, studentSlice, enrollmentSlice
├── api/             # Typed Axios client per resource
├── components/      # StudentSelector, StudentProfile, Schedule, CourseCatalog
└── types/           # TypeScript interfaces matching API response shapes
```

State is centralized. Every async operation has `pending / fulfilled / rejected` states. Enrollment errors are surfaced as typed `ApiError` objects so the UI can show specific, human-readable messages.

---

## Business Rules Enforced

All four rules are validated in `EnrollmentService` before any database write:

| Rule | HTTP response on violation |
|---|---|
| Prerequisite course must be passed | `422 { "type": "prerequisite", "message": "..." }` |
| No time slot conflicts in current schedule | `422 { "type": "conflict", "message": "..." }` |
| Maximum 5 courses per semester | `422 { "type": "max_courses", "message": "..." }` |
| Course must match student's grade level | `422 { "type": "grade_level", "message": "..." }` |

---

## API Reference

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/courses` | All courses (optional `?gradeLevel=`) |
| GET | `/api/courses/{id}` | Single course |
| GET | `/api/students/{id}` | Student profile |
| GET | `/api/students/{id}/schedule` | Active semester enrollments |
| GET | `/api/students/{id}/progress` | GPA, credits earned, graduation status |
| GET | `/api/sections` | All sections for the active semester |
| POST | `/api/enrollments` | Enroll `{ studentId, sectionId }` |
| DELETE | `/api/enrollments/{id}?studentId=` | Drop a course |

---

## Database

Pre-populated SQLite file (`maplewood_school.sqlite`) with:
- 400 students across grades 9–12
- 57 courses with prerequisite chains
- ~6,700 historical enrollment records
- 9 semesters (Fall 2024 is active)

Two tables are created on first startup by the `DataInitializer`:
- `course_sections` — one section per Fall 2024 course with assigned teacher, classroom, and time slot
- `enrollments` — tracks current semester student–section relationships

---

## Design Decisions

**Why DDD layers instead of a flat structure?**
The business rules (prerequisites, conflicts, overloads) are complex enough that mixing them into controllers would make testing and modification error-prone. Keeping them in a dedicated service layer makes each rule independently traceable and testable.

**Why Redux Toolkit over local state?**
Enrollment status (loading, success, error) is shared across the schedule view and catalog simultaneously. A centralized store avoids prop-drilling and makes optimistic/pessimistic update logic straightforward to reason about.

**Why SQLite?**
The database is provided as a file. SQLite avoids running a separate database server, keeping local setup to a single `mvn spring-boot:run` command.

**Complexity balance**
No abstract factories, generic repositories, or strategy patterns. Every abstraction in this codebase has at least two concrete uses. Simple things are kept simple — a three-line service method is not extracted into a helper class.
