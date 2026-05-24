# Migracion a Spring Boot (PayMember)

## Que se ha migrado
- Se ha creado un backend en `backend/` con Spring Boot.
- La app Android ahora consume API REST con Retrofit en lugar de Room como fuente principal.
- Se ha añadido autenticacion JWT en backend.
- Se ha añadido login local (`register/login`) y endpoint inicial para login Google.

## Backend: estructura principal
- `backend/src/main/java/com/paymember/backend/controller/AuthController.java`
- `backend/src/main/java/com/paymember/backend/controller/SubscriptionController.java`
- `backend/src/main/java/com/paymember/backend/service/AuthService.java`
- `backend/src/main/java/com/paymember/backend/service/SubscriptionService.java`
- `backend/src/main/java/com/paymember/backend/security/*`

## Endpoints
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/google`
- `GET /api/subscriptions`
- `GET /api/subscriptions/{id}`
- `POST /api/subscriptions`
- `PUT /api/subscriptions/{id}`
- `DELETE /api/subscriptions/{id}`

## Android: cambios principales
- `MainActivity` ahora usa `ApiClient` + `SubscriptionRepository` remoto.
- Nuevo paquete remoto:
  - `data/remote/ApiClient.kt`
  - `data/remote/ApiService.kt`
  - `data/remote/ApiModels.kt`
  - `data/remote/SessionStore.kt`
  - `data/remote/RemoteAuthManager.kt`
- `SubscriptionRepository` ahora sincroniza contra backend y mantiene `Flow` en memoria.

## Como arrancar

## 1) Backend
Desde `backend/`:
```bash
mvn spring-boot:run
```
Arranca en `http://localhost:8080`.

Por defecto usa **MySQL** (`spring.profiles.active=mysql`).

Variables opcionales para MySQL:
- `DB_URL` (default: `jdbc:mysql://localhost:3306/paymember?createDatabaseIfNotExist=true&serverTimezone=UTC`)
- `DB_USERNAME` (default: `root`)
- `DB_PASSWORD` (default: `root`)

## 2) Android
- Ejecuta la app en emulador Android.
- La app apunta a `http://10.0.2.2:8080/` (host local desde emulador).

## Login Google real: configuracion necesaria
1. Crear credenciales OAuth en Google Cloud:
- `Web client` (para verificar `idToken` en backend).
- `Android client` (para firma/app Android).
2. Poner el `Web client ID` en:
- Android: `local.properties` -> `GOOGLE_WEB_CLIENT_ID=tu-web-client-id.apps.googleusercontent.com`
- Backend: variable de entorno `GOOGLE_WEB_CLIENT_ID=tu-web-client-id.apps.googleusercontent.com`
3. El backend ya valida el `idToken` recibido antes de emitir JWT propio.

## Decisiones de migracion
- Se mantiene la UI y ViewModel para minimizar riesgo.
- Se cambia solo la capa de datos para pasar de local-only a cliente-servidor.
- Se conserva una sesion JWT en `SharedPreferences`.
- Se adopta MySQL como base robusta por defecto.
