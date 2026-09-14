---
description: Automates the creation of a standard Spring Boot layered architecture (Controller, Service, Repository, DTO) for a new entity.
---

# Title: Scaffold Spring Boot API
# Description: Generates standard Layered Architecture components for a new feature.

## Step 1: Initialize Entity & DTOs
Ask the user for the `{entity_name}`. Once provided, create:
- The JPA `@Entity` class.
- Request and Response DTOs. Ensure DTOs are used for data transfer, not raw entities.

## Step 2: Generate Repository
Create the `{entity_name}Repository` interface extending `JpaRepository`.
- Ensure it handles data access only.

## Step 3: Generate Service
Create the `{entity_name}Service` class.
- Use constructor injection via `@RequiredArgsConstructor`.
- Implement business logic here.

## Step 4: Generate Controller
Create the `{entity_name}Controller` class.
- Strictly map HTTP verbs (`GET`, `POST`, `PUT`, `DELETE`) to resources using nouns, not verbs.
- Ensure the Controller ONLY handles HTTP requests and delegates ALL business logic to the Service layer.
- Wrap all return values in the standardized JSON response wrapper `{ "success": true, "data": {...} }`.