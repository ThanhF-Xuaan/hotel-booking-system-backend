---
trigger: always_on
---

# Security, Configuration, and Logging

## 1. Logging Constraints
- Log incoming requests, responses, errors, and audit logs.
- **CRITICAL:** NEVER log sensitive information such as Passwords, JWT Tokens, or PII (Personally Identifiable Information).

## 2. Configuration Management
- Do NOT hard-code configurations (e.g., database URLs, secrets) in the source code.
- Externalize properties to `application.yml` and use environment variables.
- Encrypt sensitive configurations (e.g., using Jasypt or Hashicorp Vault).

## 3. Vulnerability Prevention
- Prevent SQL Injection: Always use Spring Data JPA or parameterized queries. Never concatenate raw strings for SQL queries.
- Implement proper Authorization (RBAC) to ensure users can only access their permitted resources.