# Arquitectura de Base de Datos

## Objetivo

La base de datos debe soportar el core del proyecto P2P de divisas: usuarios, ofertas, matching, transacciones, voucher OCR, disputas, calificaciones, historial y auditoria.

## Motor sugerido

- PostgreSQL como motor principal.
- Almacenamiento externo para archivos de vouchers y comprobantes.
- Redis opcional para cache, sesiones cortas o colas temporales.

## Modelo logico

### Entidades principales

- `users`: compradores, vendedores y administradores.
- `roles`: perfiles y permisos.
- `bank_accounts`: cuentas, tarjetas y billeteras de recepcion.
- `currencies`: monedas soportadas por el sistema.
- `exchange_rates`: tasas de referencia y tasas activas.
- `offers`: publicaciones de compra o venta.
- `transactions`: operaciones P2P creadas por matching manual o automatico.
- `transaction_status_history`: historial de estados de cada operacion.
- `vouchers`: archivos y metadatos del comprobante.
- `ocr_results`: texto y datos extraidos del voucher.
- `disputes`: arbitrajes y reclamos.
- `ratings`: calificaciones entre usuarios.
- `audit_logs`: trazabilidad completa.

### Relaciones base

- `users` 1..n `bank_accounts`
- `users` 1..n `offers`
- `offers` 1..n `transactions`
- `transactions` 1..n `transaction_status_history`
- `transactions` 1..n `vouchers`
- `transactions` 1..n `disputes`
- `users` 1..n `ratings`

## Esquema funcional

### users

- Guarda identidad, correo, hash de password, estado y rol.
- Debe permitir distinguir usuario normal y administrador.

### bank_accounts

- Guarda bancos como BCP, Interbank, BBVA, Yape o Plin.
- Debe relacionarse con el usuario y permitir varios medios por perfil.

### offers

- Guarda moneda origen, moneda destino, monto, tasa, banco destino y estado.
- Debe permitir listar el mercado filtrado por par de monedas.

### transactions

- Guarda comprador, vendedor, oferta, monto, tasa, estado y tiempos de control.
- Debe ser la entidad central del flujo.

### transaction_status_history

- Guarda cada cambio de estado para trazabilidad.
- Debe permitir reconstruir la secuencia del caso.

### vouchers y ocr_results

- Guardan evidencia del pago y su validacion automatica.
- Deben estar ligados a la transaccion.

### disputes

- Guarda motivo, evidencia, resolucion y usuario resolutor.
- Debe conservar el resultado final del arbitraje.

### ratings

- Guarda la calificacion entre comprador y vendedor.
- Debe estar ligada a una transaccion cerrada.

### audit_logs

- Guarda acciones sensibles: login, publicacion, matching, disputa, resolucion y cambios de estado.

## Reglas de negocio en BD

- No se debe crear una transaccion sin oferta valida.
- Una oferta debe validar que el par de monedas no sea identico.
- Una transaccion no debe saltar estados sin control.
- Un voucher aceptado debe quedar ligado al movimiento correspondiente.
- Una disputa no debe eliminarse; solo cerrarse o resolverse.
- Todo evento critico debe dejar rastro en auditoria.

## Indices recomendados

- `users.email` unico.
- `offers.from_currency`, `offers.to_currency`, `offers.rate`.
- `transactions.status`, `transactions.created_at`.
- `disputes.status`, `disputes.created_at`.
- `audit_logs.entity_type`, `audit_logs.created_at`.

## Seguridad de datos

- Hash de contrasenas con bcrypt o argon2.
- Integridad referencial con claves foraneas.
- Soft delete donde aplique para mantener trazabilidad.
- Restriccion de acceso a datos sensibles por rol.

## Escalabilidad y durabilidad

- Indices compuestos para consultas frecuentes de mercado, transacciones y disputas.
- Paginacion obligatoria en listados grandes.
- Particion por fecha para tablas de alto crecimiento como `audit_logs` y `transaction_status_history` si el volumen lo requiere.
- Backups automaticos y pruebas de restauracion.
- Retencion de logs y auditoria por politica de negocio.
- Versionado de migraciones para evolucionar el esquema sin romper compatibilidad.
- Cifrado en reposo a nivel de volumen o motor si la infraestructura lo permite.
- Separar archivos binarios del modelo relacional para evitar inflar la BD principal.

## Alcance cubierto

Esta base de datos soporta todo el alcance indicado en el PDF:

- registro e inicio de sesion;
- publicacion de ofertas;
- busqueda y filtrado;
- matching automatico;
- inicio y seguimiento de transacciones;
- confirmacion de pago;
- carga de voucher;
- calificacion;
- historial;
- disputas;
- panel administrativo.