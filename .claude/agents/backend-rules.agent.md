---
name: backend-rules
description: Enrollment business rules and validation logic for the Maplewood backend. Load when implementing or modifying enrollment, prerequisite checks, or any student-course validation.
tools: Read, Grep, Glob, Bash
---

# Business Rules

All four checks run in `EnrollmentService.enroll()` before any write. Each failure throws its own exception.

## Required checks (in order)

| # | Rule | Exception to throw |
|---|------|--------------------|
| 1 | Student must have **passed** the prerequisite course | `PrerequisiteNotMetException` |
| 2 | Course section time slot must not overlap existing schedule | `ScheduleConflictException` |
| 3 | Student may not exceed **5 courses** in a semester | `CourseOverloadException` |
| 4 | Course `grade_level_min <= student.gradeLevel <= grade_level_max` | `GradeLevelViolationException` |

## Prerequisite check

Query `student_course_history` for a `passed` record on the prerequisite course.
If `course.prerequisiteId` is null, skip this check.

```java
boolean hasPassed = historyRepo.existsByStudentIdAndCourseIdAndStatus(
    studentId, course.getPrerequisiteId(), "passed"
);
if (!hasPassed) throw new PrerequisiteNotMetException(prereq.getName());
```

## Schedule conflict check

A conflict exists when two sections in the same semester share any overlapping time slot.
Compare day-of-week + start/end time against all sections the student is already enrolled in.

## Exception classes

Define in `exception/` package. Each extends `RuntimeException`:
- `PrerequisiteNotMetException`
- `ScheduleConflictException`
- `CourseOverloadException`
- `GradeLevelViolationException`

Skipping any of these checks is not a shortcut — missing validation is a bug, not a simplification. Implement all four before the endpoint is considered done.

## Other domain rules (read-only, no exception needed)

- **GPA**: `SUM(credits where passed) / SUM(all credits taken) * 4.0`
- **Credits to graduate**: 30 total credits from passed courses
- **Max courses**: enforced on enrollment, not recalculated on reads
