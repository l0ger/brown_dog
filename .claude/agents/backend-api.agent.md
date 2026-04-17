---
name: backend-api
description: REST API contracts, error response format, and build order for the Maplewood backend. Load when designing or implementing endpoints.
tools: Read, Grep, Glob, Bash
---

# API Design

Base path: `/api`  Server port: `8080`

## Endpoints

| Method | Path | Description | Status |
|--------|------|-------------|--------|
| GET | `/api/courses` | All courses (filterable by grade) | 200 |
| GET | `/api/courses/{id}` | Single course with prerequisite | 200 |
| GET | `/api/students/{id}` | Student profile | 200 |
| GET | `/api/students/{id}/schedule` | Current semester sections | 200 |
| GET | `/api/students/{id}/progress` | GPA + credits + graduation status | 200 |
| POST | `/api/enrollments` | Enroll in a course section | 200 |
| DELETE | `/api/enrollments/{id}` | Drop a course | 204 |

## Error response (all failures use this shape)

```json
{
  "type": "prerequisite | conflict | max_courses | grade_level | not_found",
  "message": "Human-readable explanation"
}
```

Map to HTTP status:
- `prerequisite` / `conflict` / `max_courses` / `grade_level` → `422 Unprocessable Entity`
- `not_found` → `404`
- Unexpected → `500`

## Recommended build order

1. `Student`, `Course`, `Semester` entities from existing tables
2. `GET /api/courses` and `GET /api/students/{id}` (read-only, no validation needed)
3. `CourseSection` entity + seed/create sections for the current semester
4. `Enrollment` entity
5. `POST /api/enrollments` with all 4 business rule validations
6. `GET /api/students/{id}/schedule` and `/progress`
7. `DELETE /api/enrollments/{id}`
