---
description: Generates specific business exceptions and updates the Global Exception Handler to ensure the API never returns raw 500 server errors.
---

# Title: Standardize Error Handling
# Description: Creates custom business exceptions to prevent generic 500 errors and stack traces.

## Step 1: Define Business Exception
Ask the user for the error context (e.g., "User not found").
- Create a custom `RuntimeException` class.
- Define a specific business error code (e.g., `USER_NOT_FOUND`) and meaningful message instead of a generic 500 Internal Error.

## Step 2: Update RestControllerAdvice
Create or update the `@RestControllerAdvice` global exception handler class.
- Add an `@ExceptionHandler` for the newly created custom exception.
- Ensure the handler formats the response into the standardized format `{ "success": false, "code": "...", "message": "..." }`.

## Step 3: Integration
Output the generated exception class and the updated handler snippet, explaining how to throw it from the Service layer.