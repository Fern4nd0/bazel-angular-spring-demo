# bazel-angular-spring-demo

Monorepo con Spring Boot + Angular gestionado por Bazel.

## Prerrequisitos
- Bazel (7.x recomendado)
- Java 17+
- Node 20+ (para reglas JS y Angular CLI)

## Comandos principales

### Backend
- Arrancar servidor: `bazel run //back:server`
- Build: `bazel build //back:server`

### Frontend
- Arrancar dev server: `bazel run //front:devserver`
- Build: `bazel build //front:build` (salida en `bazel-bin/front/dist`)

## Probar flujo completo
1. En una terminal: `bazel run //back:server`
2. En otra terminal: `bazel run //front:devserver`
3. Abre `http://localhost:4200/home` y verifica que aparece "Hello from Spring Boot".

## Nota sobre dependencias npm
Las dependencias npm se resuelven con `npm_translate_lock` a partir de `front/pnpm-lock.yaml`.
Si cambias `front/package.json`, regenera el lockfile con:

```bash
pnpm install --lockfile-only
```
