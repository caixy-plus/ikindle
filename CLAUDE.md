# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

iKindle is an e-book reading platform with a separated frontend/backend architecture.

## Tech Stack

- **Backend**: Java 21 + Spring Boot 3.2.3 + PostgreSQL + Redis + QueryDSL
- **Frontend**: React 18 + TypeScript + Ant Design (PC) / Ant Design Mobile (mobile)
- **Auth**: JWT (configured but SecurityConfig currently uses HTTP Basic as placeholder)
- **Build tools**: Maven 3.9+ (backend), npm (frontend)

## Common Commands

### Backend
```bash
cd backend
mvn compile                    # compile + generate QueryDSL Q classes (required before first run)
mvn spring-boot:run            # start on port 8080 (context-path: /api)
mvn test                       # run tests
mvn test -Dtest=ClassName      # run a single test class
```

### Frontend
```bash
cd frontend
npm install                    # install dependencies
npm start                      # dev server on port 3000, proxies API to localhost:8080
npm run build                  # production build
npm test                       # run tests
```

### Quick Start (both services)
```bash
./start.sh         # starts backend then frontend, checks dependencies
./dev-start.sh     # same with version checks and health verification
```

### Prerequisites
- PostgreSQL on localhost:5432 (database: `ikindle`)
- Redis on localhost:6379
- DB credentials configured in `backend/src/main/resources/application.yml`

### Test Accounts (seeded by DataInitializer)
- Admin: admin / admin123
- User: test / test123

## Architecture

### Backend Structure (`backend/src/main/java/com/ikindle/`)

Standard Spring Boot layered architecture with generics:

- **entity/** — JPA entities with a `BaseEntity` superclass. Key entities: User, Book, Category, Tag, Order, OrderItem, Account, Role, Permission, Dict, UserBookshelf
- **repository/** — Spring Data JPA repos extending `BaseRepository<T, ID>` (which extends both `JpaRepository` and `JpaSpecificationExecutor`). Complex queries use QueryDSL via `*RepositoryCustom` interfaces with `*RepositoryCustomImpl` implementations in `repository/impl/`
- **service/** — Business interfaces extending `BaseService<T, ID>` with implementations in `service/impl/`
- **controller/** — REST controllers returning `ApiResponse<T>` wrapper. API path: `/api/*`
- **dto/** — Data transfer objects (BookDTO, UserDTO, etc.)
- **mapper/** — MapStruct mappers for entity↔DTO conversion. Uses Lombok + MapStruct with `lombok-mapstruct-binding`
- **common/** — `ApiResponse<T>` unified response wrapper (code, message, data, timestamp)
- **config/** — `SecurityConfig` (CORS, auth rules), `DataInitializer` (seeds test data on startup)
- **util/** — `JwtUtil` (JWT token generation/validation)

### Frontend Structure (`frontend/src/`)

Create React App + TypeScript project:

- **pages/** — Login, Register, Home, Bookshelf, BookDetail, Profile
- **services/** — Axios-based API layer (`api.ts` has interceptors for JWT token injection and error handling). Domain services: `bookService.ts`, `userService.ts`, `categoryService.ts`
- **components/** — Shared layout component (`Layout.tsx`)
- **types/** — TypeScript type definitions

### Security Rules
- Public endpoints: `/api/users/register`, `/api/users/login`, `/api/books/**`, `/api/categories/**`, `/api/tags/**`, `/api/actuator/**`
- Authenticated endpoints: `/api/users/**` (other), `/api/orders/**`, `/api/bookshelf/**`, `/api/account/**`

### Key Patterns to Follow
- Controllers return `ApiResponse.success(data)` / `ApiResponse.error(code, message)`
- Use MapStruct mappers for entity↔DTO conversion, not manual mapping
- Repository custom interfaces for QueryDSL queries go in `repository/` with implementations in `repository/impl/`
- QueryDSL Q classes are auto-generated to `target/generated-sources/java` by `apt-maven-plugin`
- All entities extend `BaseEntity` which provides common fields (id, createdAt, updatedAt)
