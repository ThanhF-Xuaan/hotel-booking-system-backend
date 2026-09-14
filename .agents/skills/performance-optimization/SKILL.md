---
name: performance-optimization
description: Optimizes Spring Boot database interactions and overall system performance. Identifies N+1 query problems, enforces pagination, and suggests caching strategies.
---

# Performance & Database Optimization Skill

Ensure the backend code is optimized for scale and efficiency.

## When to use this skill
- When writing complex JPA queries or fetching associated entities.
- When reviewing data-heavy APIs.
- When refactoring slow services.

## How to use it

1. **Prevent the N+1 Query Problem**:
   - Never use `for` loops to call repository methods for associated data.
   - Use `JOIN FETCH` in JPQL or `@EntityGraph` to load associated entities in a single query when needed.

2. **Data Pagination**:
   - APIs returning collections of data MUST use pagination (`Pageable` in Spring Data) to prevent memory overload.

3. **Caching Strategy**:
   - Suggest the use of Redis caching (`@Cacheable`) for frequently accessed, rarely changing data (e.g., Room Categories, Base Pricing Rules).

4. **Stateless Services**:
   - Ensure all Services and Controllers are stateless to allow for horizontal scaling. Do not store user-specific data in local variables or HTTP Sessions.