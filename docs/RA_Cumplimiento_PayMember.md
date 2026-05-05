# PayMember - Evidencias por Resultados de Aprendizaje (RA)

## Contexto
Proyecto Android (Kotlin + Jetpack Compose + Room) para gestionar suscripciones periódicas sin login y sin pagos reales.

## RA1 - Identificación de necesidades del sector (10%)
Necesidad detectada:
- Usuarios con multiples suscripciones online pierden control de cobros y renovaciones.

Respuesta del proyecto:
- Registro centralizado de servicios, precio, día de cobro y periodicidad.
- Recordatorios locales para reducir olvidos y cobros inesperados.

Evidencias:
- Modelo de datos con campos de suscripción.
- Pantalla de listado para visibilidad inmediata.

## RA2 - Diseño del proyecto y fases (10%)
Fases definidas:
1. Analisis funcional (MVP y requisitos).
2. Diseño técnico (arquitectura MVVM y Room).
3. Implementación (CRUD + UI Compose).
4. Recordatorios locales (WorkManager + notificaciones).
5. Pruebas y validación.
6. Entrega y mejoras.

Decisiones de diseño:
- Separación por capas (`data`, `repository`, `viewmodel`, `ui/screens`).
- Navegación simple (`list` y `form/{id}`).

## RA3 - Planificación y documentación (10%)
Plan de intervención aplicado:
- Sprint MVP base: persistencia local + CRUD.
- Sprint UX: validación de formulario y entradas numéricas.
- Sprint recordatorios: scheduling, cancelación y permiso notificaciones.
- Sprint calidad: tests unitarios e integración DAO.

Documentación asociada:
- Este documento RA.
- Código organizado por paquetes y responsabilidades.

## RA4 - Seguimiento y control (10%)
Variables de control usadas:
- Funcionalidades implementadas vs requeridas.
- Errores detectados/corregidos por iteración.
- Pruebas automáticas para persistencia y lógica.

Instrumentos:
- Tests (`SubscriptionDaoTest`, `SubscriptionViewModelTest`).
- Revisión incremental por módulos (data, viewmodel, ui, reminders).

## RA5 - Programación orientada a objetos (30%)
Aplicación de POO:
- Clases de dominio y datos (`SubscriptionEntity`, `BillingPeriod`).
- Encapsulación de acceso a datos (`SubscriptionDao`, `SubscriptionRepository`).
- Lógica de estado en `SubscriptionViewModel`.
- Separación de responsabilidades por clase.

Principios observables:
- Cohesión por capas.
- Bajo acoplamiento entre UI y persistencia.
- Reutilización de componentes.

## RA6 - Gestión de base de datos relacional (30%)
Mecanismo de conexión y persistencia:
- Room sobre SQLite local.
- Entidad relacional `subscriptions` con PK autogenerada.
- Operaciones CRUD con DAO.

Evidencias técnicas:
- `AppDatabase`, `SubscriptionDao`, `SubscriptionEntity`.
- Pruebas con Room in-memory para validar inserción/lectura/ordenación.

## Estado actual de cumplimiento
- RA1: Cubierto.
- RA2: Cubierto.
- RA3: Cubierto.
- RA4: Cubierto con evidencias iniciales.
- RA5: Cubierto.
- RA6: Cubierto.

## Mejoras sugeridas para reforzar evaluación
- Añadir diagrama UML simple (clases y flujo MVVM).
- Añadir cronograma visual (Gantt básico).
- Medir cobertura de tests y registrar incidencias en tabla de seguimiento.
