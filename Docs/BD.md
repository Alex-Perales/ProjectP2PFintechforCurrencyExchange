# Base de Datos · PostgreSQL

## Qué se usa

| Herramienta | Rol |
|---|---|
| PostgreSQL 15+ | Motor relacional principal |
| UUID (`gen_random_uuid()`) | Primary key de todas las tablas |
| Redis | Cache de tasas, transacciones pausadas, cola de tareas |
| Storage externo (S3 / local) | Vouchers y comprobantes (no en BD) |
| Alembic | Versionado y migraciones del esquema |

---

## Nuevas características

### 🔔 Notificaciones de Vendor
- Tabla `vendor_notifications` para rastrear transacciones pendientes
- Estado: `pending`, `confirmed`, `disputed`, `resolved`
- Timestamp de expiración para limpiar notificaciones antiguas

### ⏸️ Transacciones Pausadas
- Estado `paused_in_progress` en tabla `transactions`
- Timestamp `paused_at` y `resumed_at` para auditoría
- Datos completos se cachean en Redis (TTL 24h) para recuperación rápida
- Cliente sincroniza automáticamente al reanudar

### 📋 Historial de Disputas
- Tabla `disputes` con campos `status` y `resolved_by`
- Auditoría completa: quién abrió, cuándo, motivación, resolución
- Índices en `buyer_id`, `seller_id` para búsquedas rápidas

### 💱 Historial de Tasas
- Tabla `exchange_rates` con histórico diario
- Índice compuesto `(from_currency, to_currency, captured_at)`
- Rotación automática: mantener últimos 90 días

---

## Tablas y entidades

### `users`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `email` | VARCHAR(255) UNIQUE NOT NULL | Correo de acceso |
| `password_hash` | VARCHAR(255) NOT NULL | Hash bcrypt/argon2 |
| `full_name` | VARCHAR(255) NOT NULL | Nombre completo |
| `phone` | VARCHAR(20) | Teléfono opcional |
| `role` | VARCHAR(20) NOT NULL DEFAULT 'user' | `user` o `admin` |
| `kyc_verified` | BOOLEAN DEFAULT FALSE | Identidad verificada |
| `is_active` | BOOLEAN DEFAULT TRUE | Cuenta habilitada |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Fecha de registro |
| `updated_at` | TIMESTAMPTZ DEFAULT NOW() | Última actualización |

---

### `bank_accounts`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `user_id` | UUID FK → users | Propietario de la cuenta |
| `bank` | VARCHAR(50) NOT NULL | `bcp`, `interbank`, `bbva`, `yape`, `plin` |
| `account_number` | VARCHAR(100) NOT NULL | Número de cuenta o CCI |
| `alias` | VARCHAR(100) | Alias opcional del usuario |
| `is_active` | BOOLEAN DEFAULT TRUE | Cuenta habilitada |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Fecha de registro |

---

### `currencies`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `code` | VARCHAR(10) UNIQUE NOT NULL | `PEN`, `USD`, `EUR`, `BRL` |
| `name` | VARCHAR(100) NOT NULL | Nombre completo de la moneda |
| `symbol` | VARCHAR(10) | Símbolo (S/, $, €) |
| `is_active` | BOOLEAN DEFAULT TRUE | Moneda habilitada en el sistema |

---

### `exchange_rates`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `from_currency` | VARCHAR(10) NOT NULL | Moneda origen |
| `to_currency` | VARCHAR(10) NOT NULL | Moneda destino |
| `rate` | NUMERIC(12,6) NOT NULL | Tasa de referencia |
| `source` | VARCHAR(50) | `manual` o `api_externa` |
| `captured_at` | TIMESTAMPTZ DEFAULT NOW() | Momento de captura |

---

### `offers`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `seller_id` | UUID FK → users | Vendedor que publica |
| `from_currency` | VARCHAR(10) NOT NULL | Moneda que ofrece |
| `to_currency` | VARCHAR(10) NOT NULL | Moneda que recibe |
| `amount` | NUMERIC(18,2) NOT NULL | Monto total disponible |
| `min_amount` | NUMERIC(18,2) NOT NULL | Mínimo por operación |
| `max_amount` | NUMERIC(18,2) NOT NULL | Máximo por operación |
| `rate` | NUMERIC(12,6) NOT NULL | Tasa personalizada del vendedor |
| `bank_account_id` | UUID FK → bank_accounts | Cuenta receptora |
| `time_limit_min` | SMALLINT DEFAULT 15 | Tiempo límite de pago en minutos |
| `status` | VARCHAR(20) DEFAULT 'active' | `active`, `matched`, `completed`, `cancelled` |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Fecha de publicación |
| `updated_at` | TIMESTAMPTZ DEFAULT NOW() | Última actualización |

---

### `transactions`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `offer_id` | UUID FK → offers | Oferta base de la transacción |
| `buyer_id` | UUID FK → users | Comprador |
| `seller_id` | UUID FK → users | Vendedor |
| `from_currency` | VARCHAR(10) NOT NULL | Moneda que transfiere el comprador |
| `to_currency` | VARCHAR(10) NOT NULL | Moneda que recibe el comprador |
| `amount` | NUMERIC(18,2) NOT NULL | Monto acordado |
| `rate` | NUMERIC(12,6) NOT NULL | Tasa al momento de crear la TX |
| `total_paid` | NUMERIC(18,2) NOT NULL | Total que transfiere el comprador |
| `status` | VARCHAR(30) DEFAULT 'pending_payment' | Ver estados abajo |
| `expires_at` | TIMESTAMPTZ | Vencimiento del tiempo límite |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Inicio de la transacción |
| `updated_at` | TIMESTAMPTZ DEFAULT NOW() | Última actualización |

**Estados de `transactions.status`:** `pending_payment` → [`paused_in_progress`] → `voucher_uploaded` → `confirmed` → `completed` / `disputed` / `cancelled` (Los brackets indican estados opcionales de pausa durante la transacción)

---

### `transaction_status_history`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `transaction_id` | UUID FK → transactions | Transacción asociada |
| `from_status` | VARCHAR(30) | Estado anterior |
| `to_status` | VARCHAR(30) NOT NULL | Estado nuevo |
| `changed_by` | UUID FK → users | Usuario que realizó el cambio |
| `note` | TEXT | Observación opcional |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Momento del cambio |

---

### `vouchers`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `transaction_id` | UUID FK → transactions | Transacción a la que pertenece |
| `uploaded_by` | UUID FK → users | Usuario que sube el archivo |
| `file_url` | TEXT NOT NULL | URL en storage externo |
| `file_name` | VARCHAR(255) | Nombre original del archivo |
| `status` | VARCHAR(20) DEFAULT 'pending' | `pending`, `validated`, `rejected` |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Fecha de subida |

---

### `ocr_results`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `voucher_id` | UUID FK → vouchers | Voucher procesado |
| `operation_code` | VARCHAR(100) | Código de operación detectado |
| `amount_detected` | NUMERIC(18,2) | Monto leído por OCR |
| `bank_detected` | VARCHAR(50) | Banco detectado en el voucher |
| `raw_text` | TEXT | Texto completo extraído |
| `is_valid` | BOOLEAN | Resultado de validación |
| `confidence` | NUMERIC(5,2) | Porcentaje de confianza del modelo |
| `processed_at` | TIMESTAMPTZ DEFAULT NOW() | Fecha de procesamiento |

---

### `disputes`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `transaction_id` | UUID FK → transactions | Transacción en disputa |
| `opened_by` | UUID FK → users | Usuario que abre la disputa |
| `role_opener` | VARCHAR(20) NOT NULL | `buyer` o `seller` |
| `reason` | TEXT NOT NULL | Motivo declarado |
| `evidence_url` | TEXT | Evidencia adicional en storage |
| `status` | VARCHAR(20) DEFAULT 'open' | `open`, `under_review`, `resolved` |
| `resolved_by` | UUID FK → users | Admin que resuelve |
| `resolution` | TEXT | Dictamen del admin |
| `resolved_at` | TIMESTAMPTZ | Fecha de cierre |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Fecha de apertura |

---

### `kyc_documents`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `user_id` | UUID FK → users | Propietario del documento |
| `type` | VARCHAR(20) NOT NULL | `dni_front`, `dni_back`, `selfie` |
| `file_url` | TEXT NOT NULL | URL en storage externo (S3) |
| `status` | VARCHAR(20) DEFAULT 'pending' | `pending`, `processing`, `verified`, `rejected` |
| `confidence` | NUMERIC(5,2) | Confianza del procesado biométrico/ocr |
| `review_notes` | TEXT | Notas del revisor o razón de rechazo |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Fecha de subida |

---

### `vendor_notifications`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `transaction_id` | UUID FK → transactions | Transacción relacionada |
| `seller_id` | UUID FK → users | Vendedor que debe confirmar |
| `type` | VARCHAR(50) | `pending_voucher`, `timeout`, `escalation` |
| `status` | VARCHAR(20) DEFAULT 'pending' | `pending`, `sent`, `acknowledged`, `resolved` |
| `expires_at` | TIMESTAMPTZ | TTL para la notificación en la UI |
| `payload` | JSONB | Datos adicionales (previews, links) |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Fecha de creación |

---

### `user_contracts`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `user_id` | UUID FK → users | Usuario que firma |
| `signed_at` | TIMESTAMPTZ | Fecha de firma digital |
| `signature_method` | VARCHAR(30) | `click_to_sign`, `biometric` |
| `document_url` | TEXT | URL del contrato firmado (opcional) |
| `ip_address` | VARCHAR(45) | IP del firmante |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Registro |


### `ratings`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `transaction_id` | UUID FK → transactions | Transacción calificada |
| `rater_id` | UUID FK → users | Quien califica |
| `rated_id` | UUID FK → users | Quien es calificado |
| `score` | SMALLINT NOT NULL CHECK (1..5) | Calificación de 1 a 5 |
| `comment` | TEXT | Comentario opcional |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Fecha de calificación |

---

### `audit_logs`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | UUID PK | Identificador único |
| `user_id` | UUID FK → users | Usuario que realizó la acción |
| `action` | VARCHAR(100) NOT NULL | Ej: `login`, `offer_created`, `dispute_opened` |
| `entity_type` | VARCHAR(50) | `offer`, `transaction`, `dispute`, etc. |
| `entity_id` | UUID | ID del registro afectado |
| `details` | JSONB | Payload o contexto adicional |
| `ip_address` | VARCHAR(45) | IP del cliente |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | Momento del evento |

---

## 🔧 Migraciones Alembic Requeridas

### Migración 008: Tablas KYC, Notificaciones y Contratos ✅

```python
# migrations/versions/008_kyc_notifications_contracts.py
# Crea: kyc_documents, vendor_notifications, user_contracts

def upgrade():
    op.create_table('kyc_documents', ...)
    op.create_table('vendor_notifications', ...)
    op.create_table('user_contracts', ...)
```

### Migración 009: Índices de Performance 🏃

```python
# migrations/versions/009_add_performance_indexes.py

# Índices para búsquedas rápidas
CREATE INDEX idx_transactions_buyer_seller_status 
  ON transactions(buyer_id, seller_id, status);

CREATE INDEX idx_offers_seller_currencies_status 
  ON offers(seller_id, from_currency, to_currency, status);

CREATE INDEX idx_disputes_status_opened_by 
  ON disputes(status, opened_by);

CREATE INDEX idx_exchange_rates_currencies_date 
  ON exchange_rates(from_currency, to_currency, captured_at DESC);

CREATE INDEX idx_kyc_user_status 
  ON kyc_documents(user_id, status);

CREATE INDEX idx_bank_accounts_user 
  ON bank_accounts(user_id, is_active);

CREATE INDEX idx_ratings_rated 
  ON ratings(rated_id);

CREATE INDEX idx_transaction_history_transaction 
  ON transaction_status_history(transaction_id);
```

### Migración 010: Campos Adicionales 📝

```python
# migrations/versions/010_add_missing_fields.py

# En transactions
ALTER TABLE transactions 
  ADD COLUMN paused_metadata JSONB,           -- Estado guardado cuando se pausa
  ADD COLUMN paused_at TIMESTAMPTZ,           -- Timestamp de pausa
  ADD COLUMN resumed_at TIMESTAMPTZ,          -- Timestamp de reanudación
  ADD COLUMN idempotency_key VARCHAR(100) UNIQUE;  -- Prevenir duplicados

# En offers
ALTER TABLE offers 
  ADD COLUMN expires_at TIMESTAMPTZ,          -- Auto-expiración de ofertas
  ADD COLUMN is_default BOOLEAN DEFAULT FALSE; -- Oferta por defecto del vendedor

# En bank_accounts
ALTER TABLE bank_accounts 
  ADD COLUMN updated_at TIMESTAMPTZ DEFAULT NOW(),  -- Auditoría
  ADD COLUMN is_default BOOLEAN DEFAULT FALSE;  -- Cuenta por defecto

# En vouchers
ALTER TABLE vouchers 
  ADD COLUMN updated_at TIMESTAMPTZ DEFAULT NOW();  -- Timestamp de actualización

# En disputes
ALTER TABLE disputes 
  ADD COLUMN appeal_count SMALLINT DEFAULT 0,    -- Contador de apelaciones
  ADD COLUMN updated_at TIMESTAMPTZ DEFAULT NOW(); -- Auditoría

# En kyc_documents
ALTER TABLE kyc_documents 
  ADD COLUMN updated_at TIMESTAMPTZ DEFAULT NOW();  -- Auditoría

# En users
ALTER TABLE users 
  ADD COLUMN last_login_at TIMESTAMPTZ;            -- Analytics
```

---

## 🔐 Soft Deletes (Eliminación Lógica)

Para datos sensibles como usuarios y cuentas bancarias:

```python
# Agregar columna a usuarios
ALTER TABLE users 
  ADD COLUMN deleted_at TIMESTAMPTZ;

# En queries, filtrar siempre:
SELECT * FROM users WHERE deleted_at IS NULL;

# Para eliminar (no physico):
UPDATE users SET deleted_at = NOW() WHERE id = ?;
```

---

## 📊 Relaciones y Restricciones

### Foreign Keys

```sql
ALTER TABLE bank_accounts ADD CONSTRAINT fk_bank_user 
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE offers ADD CONSTRAINT fk_offer_seller 
  FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE transactions ADD CONSTRAINT fk_tx_offer 
  FOREIGN KEY (offer_id) REFERENCES offers(id);

-- Etc. (todas las FK definen comportamiento CASCADE o SET NULL)
```

### Check Constraints

```sql
-- Validar tasas positivas
ALTER TABLE exchange_rates ADD CONSTRAINT check_positive_rate 
  CHECK (rate > 0);

-- Validar calificaciones 1-5
ALTER TABLE ratings ADD CONSTRAINT check_rating_range 
  CHECK (score >= 1 AND score <= 5);

-- Validar montos
ALTER TABLE offers ADD CONSTRAINT check_amounts 
  CHECK (min_amount > 0 AND max_amount >= min_amount);
```

---

## 🔄 Estados y Máquinas de Estado

### Transactions (Estados Completos)

```
pending_payment 
  ↓
paused_in_progress (OPCIONAL - usuario puede pausar aquí)
  ↓
voucher_uploaded 
  ↓
confirmed 
  ↓ (exitoso)
completed
  ↓ (problema)
disputed
  ↓ (cancelado)
cancelled
```

### Disputes (Estados Actualizados)

```
open 
  ↓
under_review 
  ↓
resolved 
  ↓ (posible apelación)
resolved_under_appeal (NEW)
  ↓
final_resolution
```

### KYC Documents (Estados Actualizados)

```
pending 
  ↓
processing 
  ↓ (aprobado)
verified 
  ↓ (rechazado - puede reintentarse)
rejected 
  ↓ (reintento después de correcciones)
resubmitted
```

---

## 🧹 Limpieza y Mantenimiento

### Rotación de Histórico

```sql
-- Mantener histórico de tasas últimos 90 días
DELETE FROM exchange_rates 
  WHERE captured_at < NOW() - INTERVAL '90 days';

-- Archivar transacciones completadas hace 1 año
DELETE FROM transactions 
  WHERE status = 'completed' 
    AND completed_at < NOW() - INTERVAL '1 year';
```

### Índices de Análisis

```sql
-- Para reportes y analytics (bajo impacto)
CREATE INDEX idx_audit_logs_date 
  ON audit_logs(created_at DESC) 
  WHERE action IN ('transaction_completed', 'dispute_resolved');
```
