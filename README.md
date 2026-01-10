# bazel-angular-spring-demo

Monorepo with Spring Boot + Angular managed by Bazel.

## Prerequisites
- Bazel (7.x recommended)
- Java 17+
- Node 20+ (for JS rules and Angular CLI)

## Main commands

### Backend
- Run server: `bazel run //microservicios/users:server`
- Build: `bazel build //microservicios/users:server`
- Unit tests: `bazel test //microservicios/users:app_test`

### OpenAPI (Swagger)
- The API is defined in `microservicios/users/src/main/resources/spec/openapi.yaml`.
- Models and interfaces are generated at build time with Bazel (not versioned).
- Regenerate/build: `bazel build //microservicios/users:app_lib`
- Generated code is packaged in `bazel-bin/microservicios/users/openapi-generated.srcjar`.

### Frontend
- Run dev server: `bazel run //front:devserver`
- Build: `bazel build //front:build` (output in `bazel-bin/front/dist`)

## Frontend architecture
- Angular with standalone components and feature-based routing.
- Lazy loaded features via `loadChildren` (see `front/src/app/app.routes.ts`).
- Feature structure lives under `front/src/app/features/`.
  - Example: `front/src/app/features/users/` contains `users.routes.ts`, `pages/`, and `data-access/`.
- Global pages (simple screens) live under `front/src/app/pages/`.
- Feature data-access services live under each feature `data-access/` folder.

## End-to-end flow
1. In one terminal: `bazel run //microservicios/users:server`
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
