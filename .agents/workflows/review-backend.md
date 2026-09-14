---
description: : Acts as a strict Technical Lead, automatically reviewing backend code against Layered Architecture, Security, and Performance standards.
---

# Title: Backend Code Quality & Security Review
# Description: Evaluates Java code against SOLID, Performance, and Security best practices.

## Step 1: Architecture & Clean Code Check
Analyze the code for Layered Architecture violations.
- Does the Controller contain business logic? If yes, instruct moving it to the Service layer.
- Are SOLID, DRY, and KISS principles applied correctly?.

## Step 2: Performance (N+1) Check
Analyze database interactions.
- Flag any `for` loops making repetitive database calls (N+1 problem). Suggest using `JOIN FETCH` or batch fetching instead.
- Check if pagination is implemented for endpoints returning lists.

## Step 3: Security & Logging Check
- Verify that SQL queries are parameterized (using Spring Data JPA) and not using raw string concatenation to prevent SQL Injection.
- Check logging statements. Raise a critical error if passwords, tokens, or sensitive information are being logged.

## Step 4: Report Generation
Output a markdown report detailing line numbers, issues found, and the refactored code.