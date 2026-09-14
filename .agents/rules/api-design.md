---
trigger: glob
globs: /*Controller.java, /*Dto.java
---

# RESTful API Design Standards

## 1. URL Naming Conventions
- Use nouns, not verbs, for resources (e.g., `GET /users`, not `GET /getUsers`).
- Use plural nouns for collections.
- Represent hierarchy clearly (e.g., `GET /meetings/{id}/votes`).

## 2. Standardized Response Format
- Wrap all API responses in a standard JSON format:
  `{ "success": true, "data": { ... } }`

## 3. Error Handling
- Never return generic HTTP 500 (Internal Server Error) to the client with stack traces.
- Use specific business error codes and meaningful messages.
- Utilize Spring's `@RestControllerAdvice` for global exception handling.