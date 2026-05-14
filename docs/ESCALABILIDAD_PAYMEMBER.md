# Ideas de escalabilidad para PayMember

Este documento recoge posibles lineas de crecimiento para PayMember de cara al
TFG. La idea no es implementarlas todas ahora, sino demostrar que la aplicacion
esta pensada para evolucionar de una app de gestion personal a una plataforma
mas completa, mantenible y ampliable.

## 1. Catalogo remoto de servicios

Actualmente la app puede trabajar con un catalogo local de servicios populares.
Una evolucion escalable seria mover ese catalogo al backend para poder actualizar
servicios, categorias, planes, precios orientativos e iconos sin publicar una
nueva version de la app.

Ventajas:

- Permite anadir nuevos servicios desde administracion.
- Evita recompilar la app cada vez que cambie el catalogo.
- Facilita separar servicios por pais, moneda o disponibilidad.
- Permite versionar el catalogo y mantener compatibilidad con clientes antiguos.

Posible arquitectura:

- Tabla `services`.
- Tabla `service_plans`.
- Tabla `service_categories`.
- Endpoint `GET /api/catalog/services`.
- Cache local en Android para poder consultar el catalogo sin conexion.

## 2. Monitorizacion externa de cambios de precio

Esta es la idea investigada sobre subidas y bajadas de precios. Aunque no hay
una API universal fiable para todos los servicios de suscripcion, la aplicacion
podria prepararse para integrarla en el futuro mediante un modulo de proveedores.

La app no deberia consultar directamente webs externas. Lo escalable seria que
el backend hiciera comprobaciones periodicas y guardase un historico de cambios.
Android solo recibiria alertas ya calculadas.

Posible arquitectura futura:

- Tabla `service_price_history`.
- Tabla `price_change_events`.
- Servicio backend `PriceMonitorService`.
- Tarea programada diaria o semanal con `@Scheduled`.
- Endpoint `GET /api/price-alerts`.
- Notificaciones push o locales cuando un servicio contratado cambie de precio.

Fuentes posibles:

- Proveedor externo especializado si ofrece API.
- Integracion con herramientas de price monitoring tipo Price2Spy.
- Catalogo curado por administracion para demostracion academica.
- Importacion periodica desde un JSON externo controlado por el proyecto.

Para el TFG se puede presentar como modulo preparado, pero no implementado
productivamente hasta contar con una fuente de datos fiable y legalmente usable.

## 3. Sistema de usuarios y sincronizacion multi-dispositivo

PayMember puede crecer desde almacenamiento local a una experiencia sincronizada
entre dispositivos. Esto permitiria iniciar sesion en varios moviles y mantener
las mismas suscripciones, recordatorios y preferencias.

Elementos necesarios:

- Autenticacion estable con email, Google u otro proveedor OAuth.
- Sincronizacion incremental entre Room y backend.
- Resolucion de conflictos si el usuario edita sin conexion.
- Preferencias por usuario: modo oscuro, moneda, pais, idioma y recordatorios.

## 4. Soporte multi-moneda y pais

Una app de suscripciones escala mejor si no asume una sola moneda. El usuario
podria tener servicios en EUR, USD o GBP y ver totales convertidos a una moneda
principal.

Posibles mejoras:

- Campo `currency` por suscripcion.
- Conversor de moneda en backend.
- Totales mensuales por moneda y total convertido.
- Catalogo filtrado por pais.
- Formatos regionales de fecha, moneda e impuestos.

## 5. Analitica financiera avanzada

La pantalla principal ya muestra gasto mensual y proximos cobros. Una evolucion
natural es anadir analitica para que el usuario entienda mejor su gasto.

Ideas:

- Evolucion mensual y anual del gasto.
- Comparacion por categorias.
- Deteccion de servicios duplicados o poco usados.
- Objetivos de ahorro.
- Simulador: cuanto ahorro si cancelo estas suscripciones.
- Exportacion CSV/PDF para justificar gastos.

## 6. Recomendaciones inteligentes

Con suficientes datos, PayMember podria sugerir acciones sin decidir por el
usuario.

Ejemplos:

- "Tienes dos servicios de streaming similares".
- "Esta suscripcion anual equivale a X al mes".
- "Te conviene cambiar a plan anual si mantienes el servicio mas de N meses".
- "Este mes tienes mas cobros de lo habitual".

Para hacerlo bien, las recomendaciones deberian estar separadas en un modulo
propio y explicarse de forma transparente.

## 7. Compartir gastos y grupos

Muchas suscripciones se comparten con familia, pareja o companeros. PayMember
puede escalar hacia grupos de pago compartido.

Funcionalidades posibles:

- Crear grupo familiar.
- Asignar participantes por suscripcion.
- Calcular cuota de cada persona.
- Marcar pagos pendientes o pagados.
- Enviar recordatorios internos.

Esto tendria mucho valor como ampliacion funcional porque conecta con un caso de
uso real y diferenciador.

## 8. Calendario avanzado e integraciones

La app ya puede mostrar proximos cobros. Una mejora escalable seria convertirlo
en un modulo de calendario completo.

Posibles ampliaciones:

- Exportacion iCal.
- Integracion con Google Calendar u Outlook.
- Filtros por categoria.
- Vista mensual, semanal y lista.
- Avisos configurables por servicio.

## 9. Backend modular y preparado para crecimiento

Para que el proyecto sea mantenible, el backend deberia organizarse por modulos
de dominio.

Propuesta:

- `auth`: usuarios, login y seguridad.
- `subscriptions`: suscripciones del usuario.
- `catalog`: servicios, categorias y planes.
- `alerts`: recordatorios, cambios de precio y eventos.
- `analytics`: calculos agregados y estadisticas.
- `sharing`: grupos y pagos compartidos.

Esta separacion facilita testear, cambiar y ampliar la aplicacion sin mezclar
responsabilidades.

## 10. Notificaciones escalables

Los recordatorios locales son suficientes al inicio, pero si la app crece puede
ser mejor centralizar parte de las notificaciones.

Opciones:

- Notificaciones locales para recordatorios simples.
- Push desde backend para eventos importantes.
- Preferencias por tipo de aviso.
- Historial de notificaciones dentro de la app.
- Reintentos y control de avisos ya enviados.

## 11. Seguridad y privacidad

Como PayMember gestiona informacion financiera personal, la escalabilidad tambien
debe contemplar privacidad.

Medidas posibles:

- Cifrado de tokens.
- No guardar datos bancarios.
- Minimizar datos enviados al backend.
- Borrado completo de cuenta.
- Exportacion de datos del usuario.
- Auditoria basica de accesos.

## 12. Roadmap sugerido para el TFG

Orden realista de evolucion:

1. Consolidar app actual: login, suscripciones, catalogo, calendario y modo oscuro.
2. Mover catalogo a backend o dejar disenado el contrato de API.
3. Anadir analitica avanzada y exportacion.
4. Preparar modulo de alertas, incluyendo cambios de precio como idea futura.
5. Disenar sincronizacion multi-dispositivo.
6. Plantear grupos compartidos como ampliacion de alto valor.

La idea mas importante para defender el proyecto es que PayMember no queda como
una app cerrada: se puede convertir en una plataforma por capas, donde Android
es el cliente, el backend concentra la logica compartida y los modulos futuros
se anaden sin romper lo existente.
