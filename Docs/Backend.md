# Arquitectura Backend Flask API

## Objetivo

El backend debe exponer una API real en Flask para soportar todo el flujo del proyecto P2P: autenticacion, ofertas, matching, transacciones, vouchers, OCR, disputas, calificaciones y panel administrativo.

## Stack recomendado

- Flask como Web API.
- Flask-RESTX o Flask-Smorest para organizar rutas y documentacion.
- SQLAlchemy como ORM.
- Alembic para migraciones.
- JWT para autenticacion y autorizacion.
- Marshmallow o Pydantic para validacion.
- Swagger u OpenAPI para documentacion.

## Infraestructura del backend

Docker Compose forma parte de la arquitectura del backend porque define como se levanta y conecta el entorno local y la base reproducible de despliegue.

### Servicios base

- `backend`: API Flask.
- `db`: PostgreSQL.
- `redis`: cache o colas livianas.
- `nginx`: reverse proxy opcional.

### Objetivo de infraestructura

- Levantar el backend con dependencias reproducibles.
- Separar configuracion por entorno.
- Facilitar desarrollo local y preproduccion.
- Mantener alineada la arquitectura con el despliegue real.

## Estructura real del backend

La idea es dividir el backend en una API delgada, una capa core con reglas de negocio y una carpeta de pruebas separada. Una estructura base escalable seria:

```text
backend/
├── app/
│   ├── api/
│   │   ├── v1/
│   │   │   ├── auth/
│   │   │   ├── users/
│   │   │   ├── offers/
│   │   │   ├── matching/
│   │   │   ├── transactions/
│   │   │   ├── vouchers/
│   │   │   ├── ocr/
│   │   │   ├── disputes/
│   │   │   ├── ratings/
│   │   │   └── admin/
│   │   └── __init__.py
│   ├── core/
│   │   ├── config.py
│   │   ├── extensions.py
│   │   ├── security.py
│   │   ├── exceptions.py
│   │   └── constants.py
│   ├── modules/
│   │   ├── auth/
│   │   ├── users/
│   │   ├── offers/
│   │   ├── matching/
│   │   ├── transactions/
│   │   ├── vouchers/
│   │   ├── ocr/
│   │   ├── disputes/
│   │   ├── ratings/
│   │   └── admin/
│   ├── schemas/
│   ├── models/
│   ├── repositories/
│   ├── services/
│   ├── middleware/
│   ├── utils/
│   └── __init__.py
├── migrations/
├── tests/
│   ├── unit/
│   ├── integration/
│   └── fixtures/
├── scripts/
├── requirements.txt
├── pyproject.toml
└── run.py
```

## Responsabilidad de cada carpeta

- `app/api`: expone rutas HTTP, validaciones de entrada y respuesta.
- `app/core`: configura la aplicacion, extensiones, seguridad, constantes y manejo global de errores.
- `app/modules`: contiene la logica por dominio, separada por negocio.
- `app/schemas`: define contratos de entrada y salida.
- `app/models`: define el modelo de persistencia.
- `app/repositories`: encapsula consultas a la base de datos.
- `app/services`: contiene casos de uso y reglas de negocio.
- `app/middleware`: filtros, autenticacion, control de roles y auditoria.
- `tests`: pruebas unitarias, integracion y datos de apoyo.

## Flujo interno real

1. La app Android llama a `app/api`.
2. La API valida el request con `schemas`.
3. El controlador delega la operacion a `services`.
4. `services` aplica reglas de negocio y usa `repositories`.
5. `repositories` interactua con `models` y la BD.
6. La respuesta vuelve por la API con formato estable.

## Division por dominio

### auth

- registro, login, refresh, logout y recuperacion.

### users

- perfil, roles, datos personales y medios asociados.

### offers

- publicacion, listado, filtros, edicion y estado.

### matching

- seleccion automatica de la mejor contraparte.

### transactions

- creacion, bloqueo de fondos logico, estados y cierre.

### vouchers y ocr

- subida de comprobante, lectura OCR y validacion.

### disputes

- apertura, evidencia, resolucion y trazabilidad.

### ratings

- calificacion de usuarios por operacion.

### admin

- supervision, arbitraje y control de casos.

## Capas tecnicas recomendadas

- `routes` o `resources`: capa HTTP.
- `use_cases` o `services`: capa de negocio.
- `repositories`: capa de datos.
- `entities` o `models`: capa de dominio/persistencia.
- `dtos` o `schemas`: capa de contrato.

## Pruebas

### tests/unit

- pruebas de servicios, validaciones y reglas de negocio.

### tests/integration

- pruebas de endpoints, base de datos y flujos completos.

### tests/fixtures

- datos base, factories y mocks reutilizables.

## Como se ve el backend en la practica

- La carpeta `api` queda liviana y ordenada.
- La logica pesada vive en `services`.
- La persistencia vive en `repositories`.
- Los errores se centralizan en `core`.
- Las pruebas se ejecutan sin mezclar codigo de produccion.

Esta division es la que hace escalable el backend cuando crezcan usuarios, ofertas, disputas y modulos administrativos.

## API, seguridad y despliegue

### Endpoints base

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `GET /me`
- `GET /offers`
- `POST /offers`
- `PATCH /offers/{id}`
- `POST /offers/match`
- `POST /transactions`
- `GET /transactions/{id}`
- `PATCH /transactions/{id}/status`
- `PATCH /transactions/{id}/upload-voucher`
- `POST /transactions/{id}/confirm-payment`
- `POST /transactions/{id}/disputes`
- `GET /admin/disputes`
- `PATCH /admin/disputes/{id}/resolve`

### Seguridad

- JWT con expiracion.
- Password hashing con bcrypt o argon2.
- Proteccion de rutas por rol.
- Logs de seguridad y auditoria.
- Validacion estricta de payloads.

### Persistencia y despliegue

- PostgreSQL como base principal.
- Archivos de vouchers en storage externo.
- Docker para despliegue reproducible.
- Nginx como reverse proxy opcional.
- GitHub Actions para CI/CD.
- Docker Compose como orquestacion local y base del entorno reproducible.

### Escalabilidad y operacion

- Versionar la API con prefijos como `v1` para evolucionar sin romper clientes.
- Paginar listas de ofertas, transacciones y disputas.
- Aplicar filtros y ordenamiento desde el backend, no desde la app.
- Usar cache para tasas, catálogos y consultas de alta lectura.
- Mover OCR, notificaciones y tareas pesadas a colas asincronas.
- Mantener servicios sin estado para escalar horizontalmente.
- Centralizar logs, metricas y trazas para monitoreo.
- Implementar health checks y manejo consistente de errores.

### Seguridad reforzada

- Rotacion y expiracion de refresh tokens.
- Rate limiting en login, registro y endpoints sensibles.
- Validacion de origen y tamanio de archivos subidos.
- Cifrado en transito con HTTPS/TLS.
- Variables de entorno para secretos, nunca en el codigo.
- Autorizacion por rol y por recurso, no solo por login.
- Idempotencia en operaciones sensibles como confirmacion de pago.
- Auditoria obligatoria en cambios de estado y resoluciones.

### Alcance funcional cubierto

El backend cubre el alcance completo del PDF:

- registro e inicio de sesion;
- ofertas y marketplace;
- matching automatico;
- transacciones P2P;
- estados y confirmacion de pago;
- voucher OCR;
- calificaciones;
- historial;
- disputas;
- panel administrativo.