---
name: secure-api-design
description: Guides the creation of secure, RESTful APIs in Spring Boot. Enforces standard URL structures, consistent JSON response wrappers, and prevents common vulnerabilities like SQL Injection.
---

# Secure API Design Skill

Apply these standards when designing endpoints or evaluating security.

## When to use this skill
- When creating new `@RestController` endpoints.
- When writing database queries or JPA specifications.
- When reviewing API security.

## How to use it

1. **RESTful Conventions**:
   - Use nouns for resources (e.g., `/api/rooms`, not `/api/getRooms`).
   - Use plural nouns consistently.
   - Use the correct HTTP verbs (`GET`, `POST`, `PUT`, `DELETE`, `PATCH`).

2. **Standardized Responses**:
   - Wrap all API responses in a standard JSON format:
     `{ "success": true, "data": { ... } }`
   - Wrap all errors in:
     `{ "success": false, "code": "ERROR_CODE", "message": "Clear explanation" }`

3. **Security (OWASP Prevention)**:
   - **SQL Injection**: NEVER concatenate raw strings into SQL queries. Always use Spring Data JPA repository methods, `@Query` with named parameters, or Criteria API.
   - Validate all incoming request payloads using `@Valid` and appropriate validation annotations (`@NotNull`, `@Size`, etc.).