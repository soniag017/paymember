# PayMember

App Android en Kotlin + Jetpack Compose para gestionar suscripciones periodicas.

## Estado actual
- MVP funcional sin login y sin pagos reales.
- CRUD completo de suscripciones.
- Persistencia local con Room.
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
