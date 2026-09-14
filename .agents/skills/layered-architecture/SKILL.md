---
name: layered-architecture
description: Enforces Spring Boot Layered Architecture principles. Automatically applies Controller, Service, and Repository separation, ensuring business logic is isolated and dependency injection is correct.
---

# Layered Architecture Skill

Follow strict separation of concerns when generating or reviewing Spring Boot code.

## When to use this skill
- When generating new features (Controllers, Services, Repositories).
- When reviewing existing backend code for structural flaws.
- When refactoring "fat" controllers.

## How to use it

1. **Controllers (Presentation Layer)**:
   - Must ONLY handle HTTP routing, request parsing, and response formatting.
   - Absolutely NO business logic, calculations, or direct database calls.
   - Must delegate all processing to a Service.

2. **Services (Business Layer)**:
   - Must contain ALL business rules, calculations, and transactional logic.
   - Inject dependencies using Lombok's `@RequiredArgsConstructor`.

3. **Repositories (Data Access Layer)**:
   - Must ONLY handle database interactions.
   - Extend `JpaRepository` or custom interfaces.

4. **Data Transfer Objects (DTOs)**:
   - Never expose Database Entities directly in Controller responses. Always map Entities to DTOs before returning them.