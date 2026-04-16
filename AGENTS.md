# BmApp Project Instructions

## Architecture and Design
- Follow Clean Architecture boundaries (data -> domain -> app/ui).
- Apply SOLID principles in every new change.
- Keep mapping logic inside mappers/use-cases and avoid leaking data-layer DTOs into UI.

## Data Models
- Every new data model must be created in its own dedicated file.
- Avoid adding new model classes into already crowded files when introducing new payloads.

## Change Scope
- Keep changes minimal and feature-focused.
- Preserve current UX behavior unless feature requirements explicitly change it.
