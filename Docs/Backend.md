# Backend · Flask API

## Qué se usa

| Herramienta | Rol |
|---|---|
| Python 3.11+ | Lenguaje |
| Flask 3.x | Framework web |
| Flask-RESTX | Rutas versionadas y documentación Swagger automática |
| SQLAlchemy 2.x | ORM |
| Alembic | Migraciones de base de datos |
| Flask-JWT-Extended | Autenticación y autorización con JWT |
| bcrypt | Hashing de contraseñas |
| Marshmallow | Validación y serialización de payloads |
| Celery + Redis | Tareas asíncronas (OCR, notificaciones push, procesamiento de tasas) |
| Gunicorn | Servidor WSGI para producción |
| Nginx | Reverse proxy y servicio de archivos estáticos |
| Docker + Docker Compose | Contenedores del entorno local y producción |
| PostgreSQL 15 | Base de datos principal |
| Redis | Cache de tasas, cola de tareas, sesiones transacción pausada |

---


## Arquitectura de archivos

```
backend/
│
├── .env.example                   # Variables requeridas (plantilla sin secretos)
├── .env                           # Variables reales — NO commitear
├── docker-compose.yml             # Entorno local: Flask + PostgreSQL + Redis + Nginx
├── docker-compose.prod.yml        # Entorno producción
├── Dockerfile                     # Imagen de la app Flask
├── requirements.txt
├── wsgi.py                        # Punto de entrada Gunicorn
│
├── docker/
│   ├── nginx/
│   │   └── nginx.conf             # Proxy a Gunicorn, headers, HTTPS
│   └── postgres/
│       └── init.sql               # Schema inicial y extensión uuid-ossp
│
├── migrations/                    # Alembic — versionado del esquema
│   ├── env.py
│   ├── script.py.mako
│   └── versions/
│       ├── 001_create_users.py
│       ├── 002_create_offers.py
│       ├── 003_create_transactions.py
│       ├── 004_create_vouchers_ocr.py
│       ├── 005_create_disputes.py
│       ├── 006_create_ratings_audit.py
│       └── 007_create_exchange_rate.py
│
├── app/
│   ├── __init__.py                # create_app(): registra blueprints y extensiones
│   │
│   ├── core/                      # Configuración y servicios base (solo backend)
│   │   ├── __init__.py
│   │   ├── config.py              # Clases DevelopmentConfig, ProductionConfig, TestingConfig
│   │   ├── database.py            # SQLAlchemy init, session, BaseModel con UUID y timestamps
│   │   ├── security.py            # JWT setup, hash_password, verify_password
│   │   ├── exceptions.py          # Handlers globales: 400, 401, 403, 404, 422, 500
│   │   └── constants.py           # Roles, estados de TX, códigos de moneda, bancos
│   │
│   ├── api/
│   │   └── v1/
│   │       ├── __init__.py        # Blueprint v1, registra todos los namespaces RESTX
│   │       ├── auth/
│   │       │   ├── __init__.py
│   │       │   ├── routes.py      # POST /register /login /refresh /logout, GET /me
│   │       │   └── schemas.py     # RegisterSchema, LoginSchema, TokenSchema
│   │       ├── offers/
│   │       │   ├── __init__.py
│   │       │   ├── routes.py      # GET POST PATCH DELETE /offers, POST /offers/match
│   │       │   └── schemas.py
│   │       ├── transactions/
│   │       │   ├── __init__.py
│   │       │   ├── routes.py      # POST GET PATCH /transactions, /voucher /confirm /dispute
│   │       │   └── schemas.py
│   │       ├── ratings/
│   │       │   ├── __init__.py
│   │       │   ├── routes.py      # POST /ratings
│   │       │   └── schemas.py
│   │       ├── bank_accounts/
│   │       │   ├── __init__.py
│   │       │   ├── routes.py      # GET POST DELETE /bank-accounts
│   │       │   └── schemas.py
│   │       ├── exchange/
│   │       │   ├── __init__.py
│   │       │   ├── routes.py      # GET /rates, GET /rates/historical
│   │       │   └── schemas.py
│   │       └── admin/
│   │           ├── __init__.py
│   │           ├── routes.py      # GET /disputes, PATCH /disputes/{id}/resolve, GET /users
│   │           └── schemas.py
│   │
│   ├── models/                    # Modelos SQLAlchemy — uno por tabla
│   │   ├── __init__.py
│   │   ├── user.py
│   │   ├── bank_account.py
│   │   ├── currency.py
│   │   ├── exchange_rate.py
│   │   ├── offer.py
│   │   ├── transaction.py
│   │   ├── transaction_status_history.py
│   │   ├── voucher.py
│   │   ├── ocr_result.py
│   │   ├── dispute.py
│   │   ├── rating.py
│   │   └── audit_log.py
│   │
│   ├── repositories/              # Consultas encapsuladas por dominio
│   │   ├── user_repository.py
│   │   ├── offer_repository.py
│   │   ├── transaction_repository.py
│   │   ├── dispute_repository.py
│   │   └── rating_repository.py
│   │
│   ├── services/                  # Lógica de negocio y casos de uso
│   │   ├── auth_service.py
│   │   ├── offer_service.py
│   │   ├── matching_service.py
│   │   ├── transaction_service.py
│   │   ├── voucher_service.py
│   │   ├── ocr_service.py
│   │   ├── dispute_service.py
│   │   ├── rating_service.py
│   │   ├── exchange_rate_service.py
│   │   ├── providers/
│   │   │   ├── __init__.py
│   │   │   ├── exchange_rate_provider.py  # Clase abstracta
│   │   │   ├── exchangerate_host.py      # Proveedor gratis (MVP)
│   │   │   └── open_exchange_rates.py    # Proveedor producción
│   │   └── tasks/
│   │       └── update_exchange_rates.py  # Celery task — actualización automática
│   │
│   ├── middleware/                # Decoradores de autenticación, roles y auditoría
│   │   ├── auth_required.py
│   │   ├── role_required.py
│   │   └── audit.py
│   │
│   └── utils/
│       ├── pagination.py
│       ├── response.py            # Formato estándar de respuesta JSON
│       ├── storage.py             # Upload a S3 / storage local de vouchers
│       └── validators.py
│
├── tests/
│   ├── unit/
│   │   ├── test_auth_service.py
│   │   ├── test_offer_service.py
│   │   └── test_matching_service.py
│   ├── integration/
│   │   ├── test_auth_routes.py
│   │   ├── test_offer_routes.py
│   │   └── test_transaction_routes.py
│   └── fixtures/
│       ├── conftest.py
│       └── factories.py
│
└── scripts/
    ├── seed.py                    # Datos iniciales: monedas, tasas, usuario admin
    └── wait_for_db.sh             # Espera a que PostgreSQL esté listo antes de levantar Flask
```

---

## Resumen de APIs

Prefijo base: `/api/v1`

### Autenticación

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/auth/register` | Registro de nuevo usuario | No |
| POST | `/auth/login` | Login, devuelve access y refresh token | No |
| POST | `/auth/refresh` | Renovar access token con refresh token | Refresh token |
| POST | `/auth/logout` | Invalidar sesión actual | Sí |
| GET | `/auth/me` | Datos del usuario autenticado | Sí |

### Ofertas

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/offers` | Listar ofertas con filtros (`from`, `to`, `bank`) y paginación | Sí |
| POST | `/offers` | Publicar oferta (monedas, monto, tasa, banco, límites, tiempo) | Sí |
| PATCH | `/offers/{id}` | Editar oferta propia | Sí |
| DELETE | `/offers/{id}` | Cancelar oferta propia | Sí |
| POST | `/offers/match` | Matching automático: devuelve la mejor oferta para el par | Sí |

### Transacciones

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/transactions` | Iniciar transacción vinculada a una oferta | Sí |
| GET | `/transactions` | Historial de transacciones del usuario | Sí |
| GET | `/transactions/{id}` | Detalle completo de una transacción | Sí |
| PATCH | `/transactions/{id}/status` | Cambiar estado de la transacción | Sí |
| POST | `/transactions/{id}/voucher` | Subir comprobante (multipart), lanza tarea OCR | Sí |
| POST | `/transactions/{id}/confirm` | Vendedor confirma recepción y libera fondos | Sí |
| POST | `/transactions/{id}/dispute` | Abrir disputa (comprador o vendedor) | Sí |

### KYC (Verificación de identidad)

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/kyc/documents` | Subir documentos KYC (DNI frontal/reverso, selfie). Multipart con `type` y `transactional_upload` opcional. | Sí |
| GET  | `/kyc/status` | Consultar estado KYC del usuario (pending, verified, rejected) | Sí |
| POST | `/kyc/verify` | Endpoint que lanza verificación biométrica/externa (opcional, async) | Sí |

Nota: los uploads deben soportar presigned URLs para S3/environments de storage; el servicio backend debe encolar el trabajo de verificación (Celery) y notificar por Webhook/WebSocket cuando termine.

### Calificaciones y cuentas

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/ratings` | Calificar a la contraparte de una TX cerrada | Sí |
| GET | `/bank-accounts` | Listar cuentas bancarias propias | Sí |
| POST | `/bank-accounts` | Agregar cuenta o billetera | Sí |
| DELETE | `/bank-accounts/{id}` | Eliminar cuenta propia | Sí |

### Administración (solo rol admin)

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/admin/disputes` | Listar disputas activas con detalle y motivo | Admin |
| PATCH | `/admin/disputes/{id}/resolve` | Resolver: liberar al comprador o revertir al vendedor | Admin |
| GET | `/admin/users` | Listar usuarios con rol, estado y estadísticas | Admin |

### Tasas de Cambio

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/exchange/rates` | Obtener tasa de cambio actual (con caché de 5 min) | Sí |
| GET | `/exchange/rates/historical` | Histórico de tasas para análisis | Sí |

### Perfil de Usuario

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| PATCH | `/users/profile` | Editar datos del perfil (nombre, teléfono, foto) | Sí |
| GET | `/users/{id}/rating-summary` | Resumen de calificaciones del usuario | Sí |

### Transacciones Avanzadas

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/transactions/{id}/pause` | Pausar transacción en progreso (almacenar estado en Redis) | Sí |
| GET | `/transactions/{id}/resume` | Recuperar transacción pausada (restaurar desde Redis) | Sí |
| GET | `/transactions/pending` | Listar transacciones pendientes (para vendor inbox) | Sí |
| GET | `/transactions/{id}/status-history` | Historial de cambios de estado de la transacción | Sí |

### Disputas Personales

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/disputes/my-disputes` | Disputas del usuario actual (buyer + seller) | Sí |

### Mis Ofertas

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/offers/my-offers` | Listar ofertas publicadas por el usuario actual | Sí |

### Comprobantes

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/transactions/{id}/receipt/pdf` | Descargar comprobante de transacción en PDF | Sí |

---

## 📋 Especificaciones de Request/Response y Validación

### ErrorResponse Estándar

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Email ya existe en el sistema",
    "timestamp": "2026-05-25T14:30:00Z",
    "details": {
      "field": "email",
      "reason": "unique_constraint"
    }
  }
}
```

**Códigos HTTP comunes**: 401 UNAUTHORIZED | 403 FORBIDDEN | 404 NOT_FOUND | 422 VALIDATION_ERROR | 409 CONFLICT | 500 INTERNAL_ERROR

### Validación de Campos

- **email**: RFC 5322, único, max 255 chars
- **password**: Min 8 chars, 1 mayúscula, 1 número, 1 símbolo
- **phone**: Formato +51-xxxxxxxxx (Perú)
- **dni**: 8 dígitos, único
- **amount**: > 0, ≤ límite diario
- **rate**: > 0, máximo 4 decimales
- **account_number** (CCI): 20 dígitos con validación check digit
- **kyc document**: Max 10 MB, jpeg/png/pdf

### WebSocket: Notificaciones Tiempo Real

```
WS /api/v1/ws/notifications
Authorization: Bearer {token}
```

Eventos: `transaction_status_changed`, `voucher_validated`, `dispute_resolved`, `kyc_verified`, `offer_matched`

Fallback: `GET /api/v1/notifications?since={timestamp}` (polling)

### Tareas Celery

| Tarea | Trigger |
|---|---|
| `update_exchange_rates` | Scheduled 5 min |
| `process_ocr_voucher` | POST /transactions/{id}/voucher |
| `verify_kyc_documents` | POST /kyc/documents |
| `resolve_disputes_auto` | Scheduled 1h |
| `cleanup_expired_offers` | Scheduled 1h |

### Idempotencia

Header requerido: `Idempotency-Key: {uuid}` en POST sensibles. Respuesta 409 si duplicado con previous_response guardada.

### Presigned URLs para Uploads

1. `POST /transactions/{id}/voucher/presigned-url` → obtener URL de S3
2. Cliente sube directo a S3
3. `POST /transactions/{id}/voucher/complete` con s3_key
4. Backend lanza OCR async
5. Webhook notifica cuando esté listo

---