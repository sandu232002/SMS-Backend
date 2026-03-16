# SMS Backend

Microservice-based Student Management System with Spring Boot, PostgreSQL, and Spring Cloud Gateway. Services: auth, students, courses, enrollments, audit, and a single entrypoint API gateway.

## Stack
- Java 17 + Spring Boot 3.x, Spring Cloud Gateway
- PostgreSQL 16 (local container) or external/Supabase
- JJWT 0.12.x for JWT auth (HS256)
- Docker & Docker Compose for orchestration

## Services & Ports
- API Gateway: `8080`
- Auth Service: `8081`
- Student Service: `8082`
- Course Service: `8083`
- Enrollment Service: `8084`
- Audit Service: `8085`
- PostgreSQL: `5432` (mounted with `init-db.sql` seed data)

## Quick Start (Docker)
1) Copy `.env.example` to `.env` and adjust as needed (DB creds, `JWT_SECRET` ≥ 32 chars).
2) Run `docker compose up --build` from the repo root.
3) Gateway will be available at `http://localhost:8080`.

## Environment Variables (core)
- `DB_USERNAME`, `DB_PASSWORD`: database credentials (default `smsuser`/`smspassword`).
- `JWT_SECRET`: shared HMAC secret used by all services.
- `SPRING_DATASOURCE_URL`: override to point to external Postgres/Supabase per service.

## Auth & Security
- JWTs signed with HS256 using the raw `JWT_SECRET` bytes (no Base64 decoding). Keep the same value across auth-service and api-gateway.
- Gateway injects `X-Auth-User`, `X-Auth-Role`, `X-Auth-Admin-Id` headers after validation.

## Data Seed
- `init-db.sql` creates tables and inserts sample degree programs and a default admin (`admin` / password hash for "password").

## Development Notes
- Each service has its own `pom.xml` and Dockerfile under its directory.
- Health checks exposed via `/actuator/health` per service.
- To rebuild a single service: `docker compose build <service-name>` then `docker compose up <service-name>`.
