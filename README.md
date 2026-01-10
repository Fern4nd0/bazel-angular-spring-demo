# bazel-angular-spring-demo

Monorepo with Spring Boot + Angular managed by Bazel.

## Prerequisites
- Bazel (7.x recommended)
- Java 17+
- Node 20+ (for JS rules and Angular CLI)

## Main commands

### Backend
- Run server: `bazel run //microservices/users:server`
- Run server (trackings): `bazel run //microservices/trackings:server`
- Build: `bazel build //microservices/users:server`
- Build (trackings): `bazel build //microservices/trackings:server`
- Unit tests: `bazel test //microservices/users:app_test`
- Unit tests (trackings): `bazel test //microservices/trackings:app_test`

### OpenAPI (Swagger)
- The API is defined in `microservices/users/src/main/resources/spec/openapi.yaml`.
- The tracking API is defined in `microservices/trackings/src/main/resources/spec/openapi.yaml`.
- Models and interfaces are generated at build time with Bazel (not versioned).
- Regenerate/build (users): `bazel build //microservices/users:app_lib`
- Regenerate/build (trackings): `bazel build //microservices/trackings:app_lib`
- Generated code is packaged in `bazel-bin/microservices/users/openapi-generated.srcjar` and `bazel-bin/microservices/trackings/openapi-generated.srcjar`.

### Frontend
- Run dev server: `bazel run //front:devserver`
- Build: `bazel build //front:build` (output in `bazel-bin/front/dist`)

## Frontend architecture
- Angular with standalone components and feature-based routing.
- Lazy loaded features via `loadChildren` (see `front/src/app/app.routes.ts`).
- Feature structure lives under `front/src/app/features/`.
  - Example: `front/src/app/features/users/` contains `users.routes.ts`, `pages/`, and `data-access/`.
- Global pages (simple screens) live under `front/src/app/pages/` (for now, `home` and `about`).
- Feature data-access services live under each feature `data-access/` folder.

## End-to-end flow
1. In one terminal: `bazel run //microservices/users:server`
2. In another terminal: `bazel run //front:devserver`
3. Open `http://localhost:4200/home` and verify "Hello from Spring Boot" appears.


## Local infrastructure (Docker Compose)
- Definition in `infrastructure/docker-compose.yml`.
- Services: `redis` and `postgres`.
- Ports: Redis `6379`, Postgres `1234` (host) -> `5432` (container).
- Postgres credentials: user `postgres`, password `admin`, initial DB `test`.
- Databases created automatically: `app_users` and `app_tracks` via `infrastructure/create_multiple_db.sh`.

Start services:
```bash
docker compose -f infrastructure/docker-compose.yml up -d

delete table
docker exec -it infrastructure-postgres-1 psql -U postgres -d postgres -c "DROP DATABASE IF EXISTS app_users;"
docker exec -it infrastructure-postgres-1 psql -U postgres -d postgres -c "CREATE DATABASE app_users;"
```

## Backend endpoints (per OpenAPI)
- `GET /users`
- `POST /users`
- `GET /users/search`
- `GET /users/{userId}`
- `PATCH /users/{userId}`
- `DELETE /users/{userId}`

## Note about npm dependencies
npm dependencies are resolved with `npm_translate_lock` from `front/pnpm-lock.yaml`.
If you change `front/package.json`, regenerate the lockfile with:

```bash
pnpm install --lockfile-only
```
