# Byzantine Simulation Engine

## 1. Project Vision

### Description

Byzantine Simulation Engine is a turn-based web simulation set in the Byzantine Empire.

The player controls a character that progresses through narrative events, energy consumption, and an experience-based leveling system. Combat and progression are resolved through deterministic mathematical simulations with controlled randomness (moderate RNG).

The goal is not to replicate Gladiatus exactly, but to build a modular and extensible simulation engine that can evolve toward more complex systems such as PvP, builds, and skill trees.

---

### Technical Objective

Develop a full-stack architecture using:

- **Backend:** Spring Boot (Java 17)
- **Frontend:** Angular
- **Database:** PostgreSQL

Applying:

- Clean architecture principles
- Rich domain modeling
- Clear separation of responsibilities
- Modular monolithic structure
- Early unit testing

This project is conceived as a simulation engine, not a CRUD application.

---

## 2. Version 0.1 Scope

Version 0.1 focuses on establishing a solid technical foundation.

### The player will be able to:

- Register and log in.
- Create a single character.
- Have basic attributes.
- Execute energy-consuming events.
- Gain experience.
- Level up.
- View character progression.

### The following will NOT be included in 0.1:

- PvP
- Trading system
- Multiplayer interactions
- Complex dungeons
- Skill trees
- Microtransactions

Priority is architectural stability, not gameplay complexity.

---

## 3. Core Domain Model (Initial Design)

The system will be modeled using rich domain objects, not anemic entities.

### User

Responsible for identity and authentication.

Fields:

- `id` (UUID)
- `username`
- `email`
- `passwordHash`
- `role`
- `createdAt`

No progression logic is stored here.

---

### Character

Represents player progression.

Planned structure:

- `id`
- `userId`
- `name`
- `level` (object)
- `energy` (object)
- base stats

Responsibilities:

- Orchestrate progression
- React to level-up events
- Delegate rule logic to internal objects

---

### Level (Value Object)

Not a simple `int`.

Responsibilities:

- Current level
- Accumulated experience
- Calculation of required experience
- Level-up management
- Experience overflow carry-over

Initial experience curve will be linear and simple to allow future balancing.

---

### Energy (Value Object)

Not a simple `int`.

Responsibilities:

- Current energy
- Maximum energy
- Time-based regeneration
- Energy consumption
- Enforcing boundaries (never below 0, never above max)

Regeneration will be **lazy**, recalculated upon interaction rather than via scheduled jobs.

---

## 4. Planned Evolution (Post 0.1)

### Combat Engine (Round-Based Simulation)

- Instant resolution for the user
- Internal round simulation
- Alternating turns
- Minimum damage guarantee
- Maximum round cap (to prevent infinite loops)
- Controlled RNG
- Future stat influence:
  - Strength
  - Defense
  - Agility
  - Luck

Combat will operate on temporary snapshots (`Combatant`), not directly on persistent character state.

---

### Specializations & Builds (Future)

Planned extensions:

- Skill trees
- Character specializations (e.g., Warrior, Rogue)
- Armor penetration
- Stat synergies
- Passive modifiers

The current architecture is being designed to allow these systems without refactoring the core domain.

---

## 5. Technical Decisions & Architecture

### Backend

**Technology:** Spring Boot (Java 17)

Justification:

- Widely adopted in enterprise environments
- Integration with Spring Security
- Dependency injection
- Clear layered architecture
- Seamless integration with JPA/Hibernate

Architecture:

- Modular monolith
- Domain-based separation
- Layers:
  - Controller
  - Service
  - Repository
  - Domain

---

### Frontend

**Technology:** Angular

Justification:

- Structured and scalable
- Component-based architecture
- Built-in dependency injection
- Strong REST API integration
- Robust form and state management

Frontend will be fully decoupled from backend via HTTP communication.

---

### Database

**Technology:** PostgreSQL

Local environment:

- Docker

Demo environment:

- Supabase or Neon

Relational model:

User → Character → (Future systems)

---

## 6. Design Principles

- Rich domain model (no anemic entities)
- Value Objects for rule-driven concepts
- Dependency injection
- Encapsulated formulas inside domain objects
- Early unit testing
- Incremental evolution without premature overengineering
