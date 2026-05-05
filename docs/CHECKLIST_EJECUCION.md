# Checklist de Arranque - PayMember

## Antes de ejecutar
- [ ] Tener Android Studio instalado.
- [ ] Tener SDK Android 35 disponible.
- [ ] Usar JDK 17 en el proyecto.

## Al abrir el proyecto
- [ ] Abrir carpeta raiz `PayMember`.
- [ ] Esperar Sync de Gradle sin errores.
- [ ] Aceptar generar/actualizar Gradle Wrapper si el IDE lo solicita.

## Verificacion funcional
- [ ] Crear suscripcion nueva.
- [ ] Editar suscripcion existente.
- [ ] Eliminar suscripcion.
- [ ] Cerrar y reabrir app, comprobar persistencia (Room).
- [ ] Activar recordatorio y conceder permiso de notificacion.

## Verificacion tecnica
- [ ] Ejecutar tests de `app/src/test`.
- [ ] Revisar que no haya errores de compilacion.
