# Byzantine Simulation Engine

Turn-based simulation backend inspired by progression-heavy browser RPGs, set in a Byzantine-flavored world.

The project is being built incrementally with two parallel goals:

1. Ship a coherent backend from the beginning.
2. Use each version scope as a learning step in architecture, domain modeling, testing, and controlled growth.

## Project Status

The project already has a working backend foundation:

- Spring Boot application with layered architecture.
- User registration and persistence.
- One character per user.
- Rich domain objects for level, energy, stats, and gold.
- Event execution flow for training and combat.
- Transactional persistence around domain mutations.
- Debug panel for fast manual testing.
- PostgreSQL for `dev` and H2 for `test`.

Some parts are intentionally incomplete:

- The stat system now affects combat and progression through soft-scaling formulas, but it still needs balancing and deeper interactions.
- `LUCK` is now part of the stat model, lightly biases random combat rolls, and slightly improves combat gold rewards.
- The debug panel is intentionally open in local development and is not production-safe.
- The current web UI is a temporary debug tool, not the final frontend.

## Local Development

### Prerequisites

- Java 17+
- Git
- Docker (optional, only if you want a local PostgreSQL instance)

### Clone

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
cd YOUR_REPOSITORY
```

### Run Tests

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw test
```

Tests use the `test` profile with H2 and do not require external services.

### Run the Combat Balance Snapshot

This project includes a deterministic balance harness that simulates repeated fights and prints a compact report with win rates, draws, average rounds, and current reward modifiers.

The snapshot currently covers small and extreme cases such as `+3`, `+10`, and `+20` stat gaps so balance changes can be checked against both early progression and long-term scaling.

Run it on its own with:

```bash
./mvnw -Dtest=CombatBalanceReportTest test
```

On Windows PowerShell:

```powershell
.\mvnw -Dtest=CombatBalanceReportTest test
```

### Run the Application in DEV

The `dev` profile expects these environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

You can run it directly:

```bash
./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
```

On Windows PowerShell:

```powershell
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Or use the helper script:

```powershell
.\run-dev.ps1
```

`run-dev.ps1` reads variables from `.env` and starts the app with the `dev` profile.

### Useful URLs

- Debug panel: `http://localhost:8080/debug`
- Health check: `http://localhost:8080/api/health`

## Profiles

| Profile | Purpose |
| ------- | ------- |
| `dev`   | PostgreSQL-backed local development |
| `test`  | In-memory H2 for automated tests |

## Architecture Principles

The project is intentionally being built around a few core rules:

- Rich domain model, not an anemic entity model.
- Business rules belong in the domain whenever possible.
- Services orchestrate use cases, but do not absorb core rules.
- Persistence is an implementation detail behind repository abstractions.
- Features should be added in small, coherent scopes instead of large speculative jumps.
- Tests should evolve with the domain and protect both behavior and architecture.

## Debug Panel Note

The debug routes are currently open on purpose to support fast iteration while the project is still in active backend-first development.

That is acceptable for local development, but it must change before any public deployment:

- Restrict or remove public access to `/debug` and `/api/debug/**`
- Re-enable CSRF where appropriate
- Replace debug-only flows with proper authenticated application flows

This is a planned hardening step, not a forgotten issue.

## Version Scopes

The README should be updated at the end of each scope with three things:

1. What was completed.
2. What was intentionally deferred.
3. What the next scope is supposed to achieve.

That discipline matters because the project is also a learning exercise in planning and controlled scaling.

### Version 0.1 - Foundation

Status: completed

Goal:
Build a stable technical base before chasing feature breadth.

Delivered:

- User entity and persistence.
- Character creation with one-to-one user ownership.
- Level and energy as domain concepts.
- Basic event execution.
- Testable domain and application structure.
- Initial Spring Security wiring.
- PostgreSQL/H2 environment split.

Deferred on purpose:

- Deeper combat rules.
- Real frontend flows.
- Public deployment hardening.

### Version 0.2 - Coherent Progression and Debug-Driven Iteration

Status: in progress

Goal:
Expand the engine without losing coherence, while using the debug panel and tests to validate behavior quickly.

Delivered so far:

- Gold and stat progression foundations.
- Training event with transactional persistence.
- Combat engine prototype with isolated combat flow.
- `STR` now increases combat damage through combat-state generation.
- `AGI` now modifies hit chance based on the attacker/defender matchup.
- `INT` now increases experience rewards in combat and training.
- `LUCK` now nudges random combat rolls, enables low critical-hit chances, and slightly improves combat gold rewards.
- Combat and reward formulas now use soft scaling so stats can keep growing without dead hard caps.
- Unified Spring persistence path around JPA.
- Consistent password handling across services and seed data.
- Debug controller refactor to reuse services instead of duplicating logic.
- Safer debug output rendering without `innerHTML`.
- Validation against non-positive stat, XP, and gold mutations where appropriate.
- Better debug-focused tests with `MockMvc`.

Still intentionally incomplete inside 0.2:

- Stats shape combat through soft scaling, but they still need deeper balancing and more nuanced interactions.
- The debug panel is still a developer tool, not a user-facing product feature.
- Combat balance is still structural rather than gameplay-tuned.

### Version 0.3 - Planned Next Scope

Status: planned

Primary goal:
Make the current systems feel more connected instead of merely coexisting.

Planned focus:

- Refine and balance the new stat-driven combat rules.
- Tighten API contracts and remove remaining ambiguous validations.
- Expand automated coverage around combat and debug-assisted flows.
- Start separating "temporary debug capability" from "real application capability".
- Prepare the project for a cleaner frontend/API boundary.

Not the goal of 0.3:

- Massive feature expansion.
- Premature multiplayer systems.
- Complex skill trees or economy depth before the core loop feels coherent.

## Current Review Priorities

If you want to continue improving the codebase in the same spirit, the highest-value next checks are:

1. Balance the new stat-driven combat formulas with real gameplay targets.
2. Continue replacing duplicated controller logic with domain/application flows.
3. Keep extending tests whenever a rule becomes important enough to rely on.
4. Update this README after each meaningful scope so planning stays visible and honest.

## Tech Stack

- Backend: Spring Boot 3 / Java 17
- Persistence: Spring Data JPA + Hibernate
- Databases: PostgreSQL (`dev`), H2 (`test`)
- Security: Spring Security
- Testing: JUnit 5, Spring Boot Test, MockMvc

## Development Approach

This project is intentionally not being treated as "just write features until it works".

The aim is to learn how to:

- design a project from the beginning,
- keep the architecture coherent while features grow,
- document decisions and scope boundaries,
- and scale complexity without losing control of the codebase.
