---
trigger: model_decision
description: When writing database queries, JPA repositories, or data fetching logic
---

# Database Performance Best Practices

## 1. Prevent N+1 Query Problem
- Never use `for` loops to query the database for associated entities.
- Use `JOIN FETCH` in JPQL, `@EntityGraph`, or batch fetching to retrieve associated entities in a single query.

## 2. Efficient Data Handling
- Implement Pagination for all APIs returning large lists of data.
- Ensure proper Database Indexing on frequently queried columns (e.g., foreign keys, search fields).
- Consider Caching strategies for frequently accessed, rarely changing data.