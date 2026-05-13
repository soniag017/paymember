# PayMember - Guia de defensa tecnica (arquitectura y decisiones)

## 1) Mensaje corto de apertura (30-45 segundos)
PayMember es una app Android nativa para gestionar suscripciones periodicas.  
He elegido una arquitectura MVVM con Jetpack Compose para la interfaz, Room sobre SQLite para persistencia local y WorkManager para recordatorios.  
La prioridad del TFG ha sido: simplicidad, mantenibilidad, rendimiento en dispositivo y funcionamiento offline, evitando complejidad de infraestructura no necesaria para el alcance del proyecto.

---

## 2) Por que Jetpack Compose (y no UI XML tradicional)

### Justificacion principal
- Menos codigo de interfaz y menos friccion de mantenimiento que XML + ViewBinding.
- Estado reactivo: la UI se actualiza automaticamente cuando cambia el estado del ViewModel.
- Mejor encaje con MVVM moderno (StateFlow + composables).
- Iteracion mas rapida para prototipado y rediseño visual.

### Como se aplica en PayMember
- Pantallas en `ui/screens/*` con composables.
- Navegacion con `navigation-compose`.
- Tema y sistema visual centralizados (`ui/theme/*`), facilitando coherencia y cambios globales.

### Riesgo asumido y mitigacion
- Riesgo: curva inicial de Compose.
- Mitigacion: estructura por componentes reutilizables y tests de logica en ViewModel/DAO para evitar regresiones.

---

## 3) Base de datos: SQL vs NoSQL y por que SQL aqui

### Respuesta directa para tribunal
En PayMember la base de datos es **SQL**, concretamente **SQLite** embebida en Android, gestionada mediante **Room**.

### Por que SQL en este TFG
- El modelo de datos es estructurado y relacional (suscripciones con campos bien definidos).
- Necesitamos operaciones CRUD y filtros/ordenaciones deterministas.
- ACID local de SQLite: consistencia alta para datos financieros de usuario.
- Cero dependencia de internet para guardar/consultar datos.
- Room reduce errores al dar consultas tipadas y validacion en compilacion.

### Por que no NoSQL en este caso
- No habia necesidad de esquema flexible ni volumen masivo distribuido.
- Añadir NoSQL aportaba complejidad sin mejorar el objetivo del MVP.
- Para una app local, SQLite ya viene optimizada en Android y es un estandar maduro.

---

## 4) Por que Room y no SQLite "a mano"

### Ventajas de Room en defensa
- Mapeo objeto-tabla con entidades/DAO claros.
- Menos boilerplate que usar `SQLiteOpenHelper` manual.
- Migraciones controladas (`MIGRATION_1_2` en `AppDatabase`).
- Integracion natural con corrutinas/Flow.
- Mejor testabilidad (`room-testing` en tests de DAO).

### Evidencia real del proyecto
- `data/db/AppDatabase.kt`
- `data/dao/SubscriptionDao.kt`
- `data/model/SubscriptionEntity.kt`
- Test: `SubscriptionDaoTest.kt`

---

## 5) "Conexiones" del sistema: como explicarlo

## 5.1 Conexion de capas internas
Flujo principal:
1. UI (Compose) captura accion del usuario.
2. ViewModel procesa estado y validaciones.
3. Repository actua como capa de acceso.
4. DAO ejecuta operaciones sobre Room/SQLite.
5. La UI observa estado actualizado y se recompone.

Esto desacopla interfaz y persistencia, mejorando mantenimiento y pruebas.

## 5.2 Conexion con servicios externos
En esta version **no hay conexion a backend remoto** (sin login, sin pagos reales, sin API REST).  
Es una decision de alcance: priorizar producto offline robusto antes de arquitectura distribuida.

## 5.3 Conexion de recordatorios
Se usa WorkManager para programar notificaciones locales de forma fiable, incluso con restricciones de bateria o reinicios.

---

## 6) Por que no Spring Boot (de momento)

### Respuesta corta
No se eligio Spring Boot porque el alcance funcional del TFG era local/offline y no requeria servidor.

### Justificacion tecnica
- Un backend Spring Boot implica desplegar y mantener infraestructura adicional (hosting, seguridad, monitorizacion, costes).
- Introduce latencia y dependencia de red para un caso de uso que funciona bien en local.
- Para un MVP academico, era mas eficiente invertir esfuerzo en UX, arquitectura cliente y calidad de codigo.

### Cuando si tendria sentido Spring Boot
- Multiusuario con cuentas y autenticacion.
- Sincronizacion entre varios dispositivos.
- Panel admin, analitica centralizada o recomendaciones avanzadas.
- Integraciones de pago reales y pasarelas externas.

---

## 7) Preguntas tipicas del tribunal (respuestas preparadas)

### "La base de datos es SQL o NoSQL?"
Es SQL: SQLite local, gestionada con Room.

### "Por que no Firestore/Mongo (NoSQL)?"
Porque el dominio es estructurado, el volumen es pequeño y priorizamos consistencia local offline. NoSQL no aportaba valor en esta fase.

### "Por que no Spring Boot?"
Porque no hay necesidad de backend en el MVP. Meter servidor hubiera añadido complejidad operativa sin mejorar el objetivo principal del TFG.

### "Como aseguras escalabilidad futura?"
La separacion por capas (UI, ViewModel, Repository, DAO) permite sustituir facilmente la fuente de datos local por remota o hibrida sin rehacer toda la app.

### "Como controlas cambios de esquema?"
Con versionado de Room y migraciones explicitas (por ejemplo, migracion 1->2 en `AppDatabase`).

### "Que aporta Compose en terminos de calidad?"
Reduce codigo repetitivo de UI, mejora mantenibilidad y acelera iteracion, manteniendo la logica separada en ViewModel.

---

## 8) Limitaciones reconocidas (importante decirlo)
- No hay sincronizacion cloud entre dispositivos.
- No hay autenticacion/roles.
- No hay pasarela de pagos real.

Decir esto suma credibilidad si se acompaña de roadmap tecnico.

---

## 9) Roadmap tecnico (si te preguntan "y despues que?")
1. API REST con Spring Boot para autenticacion y sincronizacion.
2. Estrategia offline-first: Room local + sincronizacion diferida.
3. Cifrado de datos sensibles y hardening de seguridad.
4. Telemetria y monitorizacion de errores.

---

## 10) Frase de cierre para defensa
La arquitectura elegida no busca "la tecnologia mas grande", sino la mas adecuada al problema y al alcance del TFG: app nativa, offline, mantenible y con base solida para evolucionar a backend cuando el producto lo requiera.
