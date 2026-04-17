---
name: backend-layers
description: DDD layer rules and SOLID principles for the Maplewood Spring Boot backend. Load when creating or modifying controllers, services, repositories, or DTOs.
tools: Read, Grep, Glob, Bash
---

# DDD Layer Rules

## Controller
- Thin HTTP adapter only. No logic, no repo calls.
- Accept `@RequestBody` DTOs, return `ResponseEntity<*DTO>`.
- Annotate: `@RestController` + `@RequestMapping("/api/<resource>")`.

```java
@PostMapping
public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest req) {
    return ResponseEntity.ok(enrollmentService.enroll(req));
}
```

## Service
- Owns the flow: validate → execute → persist → return DTO.
- Annotate writes with `@Transactional`.
- Calls repositories and domain services only — never another application service.
- Throws typed exceptions (see `backend-rules.agent.md`) instead of returning error flags.

## Repository
- Extend `JpaRepository<Entity, Long>`.
- Complex queries go here as JPQL or `@Query` — not in the service.
- Name by Spring Data convention: `findByStudentIdAndSemesterId(...)`.

## DTO
- Separate classes for request and response. Never reuse.
- Validate requests with `@Valid` + Bean Validation (`@NotNull`, `@Min`, etc.).
- No JPA annotations on DTOs.

## Exception handler
One `@RestControllerAdvice` maps every typed exception to HTTP + `ErrorResponse`:

```java
@ExceptionHandler(PrerequisiteNotMetException.class)
public ResponseEntity<ErrorResponse> handle(PrerequisiteNotMetException ex) {
    return ResponseEntity.status(422).body(new ErrorResponse("prerequisite", ex.getMessage()));
}
```

---

## SOLID Principles

**S — Single Responsibility**
Each class does one thing. `EnrollmentService` validates and persists enrollments; it does not calculate GPA. `StudentService` reads student data; it does not touch enrollments. If a class needs a second `@Autowired` repo that belongs to a different domain concept, that is a signal to split it.

**O — Open/Closed**
Add behaviour by adding classes, not by editing existing ones. New validation rules get a new exception class and a new check method — not a new `if` branch inside an existing method.

**L — Liskov Substitution**
Do not override methods in ways that change their contract. If a base class or interface says a method returns a non-null result, every implementation must honour that. Prefer interfaces over inheritance for service contracts.

**I — Interface Segregation**
Define narrow service interfaces. A `CourseQueryService` interface for reads and an `EnrollmentService` interface for writes is better than one fat `CourseService` interface that controllers partially depend on.

**D — Dependency Inversion**
Controllers depend on service interfaces, not concrete classes. Services depend on repository interfaces, not implementations. Wire everything through Spring constructor injection — never `new` a service or repository directly.

```java
// Correct — depend on the interface
public class EnrollmentController {
    private final EnrollmentService enrollmentService; // interface, not impl

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }
}
```

### Complexity balance

Before writing a class, ask: *does the problem actually require this?*

| Simpler — prefer this | More complex — only if justified |
|-----------------------|----------------------------------|
| One service class per domain concept | Multiple layers of delegation within a concept |
| Method that does one thing | Abstract base class or template method |
| Direct repository call in service | Generic query builder / specification pattern |
| Typed exception per rule | Exception hierarchy with shared base logic |
| Spring constructor injection | Custom bean factory or provider |

A design is too complex when:
- You need to open more than two files to follow a single request from controller to DB.
- A new team member would need to ask why a pattern exists before they can use it.
- The abstraction has only one implementation and no concrete plan for a second.

A design is too simple when:
- Business logic leaks into the controller.
- A service method does validation, persistence, *and* email/notification in one block.
- Copy-pasted code means a rule change requires edits in more than one place.

The target is the middle: each class has one clear job, dependencies point inward, and nothing exists without a current reason.
