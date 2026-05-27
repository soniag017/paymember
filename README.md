# PayMember

App Android en Kotlin + Jetpack Compose para gestionar suscripciones periodicas.

## Estado actual
- MVP funcional con backend Spring Boot + JWT.
- CRUD completo de suscripciones via API REST.
- Recordatorios locales con WorkManager y notificaciones.
- Validaciones de formulario.
- Tests base (DAO y ViewModel).

## Requisitos recomendados
- Android Studio (version reciente)
- JDK 17 configurado en Android Studio
- SDK Android 35 instalado

## Como ejecutar en Android Studio
1. Abrir Android Studio.
2. Seleccionar `Open` y elegir esta carpeta: `PayMember`.
3. Esperar la sincronizacion de Gradle.
4. Si Android Studio pide crear/actualizar Gradle Wrapper, aceptar.
5. Ejecutar en emulador o dispositivo (`Run app`).

## Backend Spring Boot
1. Abrir terminal en `backend/`.
2. Tener MySQL corriendo (el backend crea la BD `paymember` si no existe).
3. Para Google Sign-In, asegurarse de que `local.properties` incluye `GOOGLE_WEB_CLIENT_ID=...`.
4. Ejecutar `mvn spring-boot:run`.
5. La API queda en `http://localhost:8080`.
6. En emulador Android, la app usa `http://10.0.2.2:8080`.

## Notificaciones
- En Android 13+ la app pedira permiso de notificaciones al iniciar.
- Los recordatorios se programan al guardar/editar una suscripcion con recordatorio activo.

## Tests
Desde Android Studio:
- Ejecutar `SubscriptionDaoTest`
- Ejecutar `SubscriptionViewModelTest`

## Estructura
- `data/model`
- `data/dao`
- `data/db`
- `data/repository`
- `viewmodel`
- `ui/screens`
- `ui/theme`
