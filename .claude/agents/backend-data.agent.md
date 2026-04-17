---
name: backend-data
description: Database schema, JPA entity mapping, and SQLite-specific rules for the Maplewood backend. Load when creating or modifying entities or writing queries.
tools: Read, Grep, Glob, Bash
---

# Database & Entity Guide

DB file: `maplewood_school.sqlite` (at project root, path configured in `application.properties`)

## Existing tables → entity classes

| Table | Entity | Notes |
|-------|--------|-------|
| `students` | `Student` | grade_level 9–12 |
| `courses` | `Course` | has `prerequisite_id` FK to itself |
| `teachers` | `Teacher` | max 4h/day |
| `classrooms` | `Classroom` | capacity 10 |
| `semesters` | `Semester` | Fall=1, Spring=2 (`order_in_year`) |
| `student_course_history` | `StudentCourseHistory` | status: `passed` / `failed` |
| `specializations` | `Specialization` | links teachers to subjects |

## Tables you must create

- **`course_sections`** — a course offered in a specific semester with teacher, classroom, and time slot
- **`enrollments`** — links a student to a course section for the current semester

`ddl-auto=update` will create them automatically when you add the entity classes.

## SQLite-specific rules

- Use `@GeneratedValue(strategy = GenerationType.IDENTITY)` — no sequences.
- No native boolean column — store as `INTEGER` (0/1), cast in code.
- Avoid more than 2 levels of `JOIN FETCH` — SQLite planner degrades.
- Dialect is already set: `org.hibernate.community.dialect.SQLiteDialect`
- All relationships default to `FetchType.LAZY`.

## Entity template

```java
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "grade_level", nullable = false)
    private int gradeLevel;

    // getters / setters
}
```
