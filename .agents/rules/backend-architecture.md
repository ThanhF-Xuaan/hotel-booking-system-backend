---
trigger: glob
globs: src/main/java//*.java
---

# Backend Core Architecture & Clean Code

## 1. Layered Architecture
- Strictly separate the application into layers: `Controller`, `Service`, and `Repository`.
- **Controllers**: Must ONLY handle HTTP requests and responses. Never write business logic inside Controllers.
- **Services**: Must contain all the business logic.
- **Repositories**: Must handle data access only.

## 2. Dependency Injection
- Use constructor injection via Lombok's `@RequiredArgsConstructor` (or standard constructors) instead of field injection (`@Autowired`) for better testability.

## 3. Clean Code Principles
- Follow S.O.L.I.D, DRY (Don't Repeat Yourself), and KISS (Keep It Simple, Stupid) principles.