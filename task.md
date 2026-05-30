# Plan de Sprints — PeruExchange P2P
> Basado en la lógica del demo `PeruExchange_Final.html`  
> 29 pantallas · 3 capas (Backend Flask + Android Compose + PostgreSQL)

---

## Sprint 1 — Arquitectura Base + Autenticación ✅
**Pantallas:** Splash, Login, Register, Forgot Password, KYC, Terms, Privacy

### Backend
- `POST /api/v1/auth/register` — registrar usuario con bcrypt
- `POST /api/v1/auth/login` — devuelve JWT access + refresh token
- `POST /api/v1/auth/refresh` — renovar access token
- `POST /api/v1/auth/logout` — invalidar sesión
- Modelo `User` con roles: buyer / vendor / admin
- Migraciones SQL: `01_users.sql` → `04_bank_accounts.sql`

### Android
- `TokenManager` (DataStore) — guarda/lee access token, refresh, user info
- `ApiClient` (Retrofit + OkHttp + JWT interceptor automático)
- `NavGraph` base con rutas de auth separadas del main
- `LoginScreen` + `LoginViewModel` — login real contra backend
- `RegisterScreen` — registro real, navega al market al completar
- `ForgotPasswordScreen` — UI funcional
- `KycScreen` — UI de verificación de identidad
- `TermsScreen` / `PrivacyScreen` — contenido estático

### Criterios de Evaluación
- [ ] Login exitoso → token guardado en DataStore
- [ ] Token se adjunta automáticamente en cada request
- [ ] Pantallas protegidas redirigen a Login si no hay token
- [ ] Logout limpia sesión y vuelve a Login
- [ ] Register crea usuario en BD y hace login automático

---

## Sprint 2 — Mercado P2P (Feature Core) ✅
**Pantallas:** Market (ticker + lista ofertas), Publish Offer, Matching automático, Partial buy dialog

### Backend
- `GET /api/v1/offers` — lista activas, excluye propias (JWT optional)
- `POST /api/v1/offers` — publicar oferta (solo vendor/admin)
- `PATCH /api/v1/offers/:id` — pausar / reanudar
- `DELETE /api/v1/offers/:id` — cerrar oferta
- `POST /api/v1/offers/match` — mejor precio, excluye propias
- `GET /api/v1/exchange/rates` — tasas reales (USD/PEN: 3.72, EUR/PEN: 4.05)

### Android
- `MarketScreen` — ticker tasas reales, WelcomeCard con nombre real del usuario
- `MarketViewModel` — loadOffers, matchOffer, loadExchangeRates
- `PublishScreen` + `PublishViewModel` — crear oferta (tipo partial/full)
- `OfferRepository` + `OfferRepositoryImpl`
- `ExchangeApi` + `ExchangeRate` model

### Criterios de Evaluación
- [ ] Ticker TopBar muestra USD/PEN y EUR/PEN reales del backend
- [ ] Mis propias ofertas NO aparecen en mi mercado
- [ ] Matching automático asigna la oferta de mejor precio
- [ ] Partial buy valida límites min/max del vendedor
- [ ] Full buy obliga comprar el monto total disponible
- [ ] Publicar oferta aparece en el mercado del comprador

---

## Sprint 3 — Flujo de Transacción P2P Completo ✅
**Pantallas:** Transaction (timer + timeline + voucher + OCR), Receipt, Rating

### Backend
- `POST /api/v1/transactions` — crear transacción (valida monto, no comprar propio)
- `GET /api/v1/transactions/:id` — con buyer_name + vendor_name resueltos
- `POST /api/v1/transactions/:id/voucher` — subir comprobante
- `POST /api/v1/transactions/:id/confirm` — vendedor confirma → completed
- `POST /api/v1/ratings` — calificar transacción completada
- `GET /api/v1/ratings/received` — reseñas recibidas con promedio y distribución

### Android
- `TransactionScreen` — auto-refresh cada 5s, countdown timer 15min, OCR simulado 1.9s
- `TransactionViewModel` — loadTransaction, uploadVoucher, updateStatus, confirmTransaction
- `ReceiptScreen` — buyer_name + vendor_name + amount reales del backend
- `RatingScreen` + `RatingViewModel` — submit calificación al backend
- `TransactionRepository` + `TransactionRepositoryImpl`

### Criterios de Evaluación
- [ ] Comprador sube voucher → estado cambia a `voucher_uploaded`
- [ ] Vendedor confirma → estado pasa a `completed` en tiempo real
- [ ] Receipt muestra nombres y montos reales (no hardcoded)
- [ ] Botón "Ver Comprobante" aparece solo cuando está `completed`
- [ ] Rating se envía al backend y actualiza el promedio del vendedor
- [ ] Error "No puedes comprar tu propia oferta" aparece en español

---

## Sprint 4 — Perfil + Cuentas Bancarias + Reseñas ✅
**Pantallas:** Profile, Edit Profile, Bank Accounts, My Reviews

### Backend
- `GET /api/v1/users/me` — datos completos del usuario autenticado
- `PATCH /api/v1/users/profile` — actualizar nombre / teléfono
- `GET /api/v1/bank-accounts` — listar cuentas del usuario
- `POST /api/v1/bank-accounts` — agregar cuenta bancaria
- `DELETE /api/v1/bank-accounts/:id` — eliminar cuenta
- `GET /api/v1/ratings/received` — promedio + distribución + lista comentarios

### Android
- `ProfileScreen` + `ProfileViewModel` — nombre/email/rating/rol reales del backend
- `EditProfileScreen` + `EditProfileViewModel` — pre-llena campos, guarda con PATCH
- `BankAccountsScreen` + `BankAccountsViewModel` — CRUD completo real
- `ReviewsScreen` + `ReviewsViewModel` — promedio real, barras distribución, comentarios reales

### Criterios de Evaluación
- [ ] Perfil muestra datos reales del usuario logueado (no hardcoded)
- [ ] Editar nombre/teléfono persiste al reabrir la app
- [ ] Agregar cuenta BCP aparece en la lista
- [ ] Eliminar cuenta la remueve del backend
- [ ] Reseñas muestra "0 reseñas" si no tiene (no datos de ejemplo)

---

## Sprint 5 — Modo Vendedor + Historial de Operaciones ✅
**Pantallas:** Vendor Inbox, Vendor confirm mode, History, Transaction Detail

### Backend
- `GET /api/v1/transactions/pending` — transacciones pendientes del vendor autenticado
- `POST /api/v1/transactions/:id/confirm` — liberar fondos al comprador
- `GET /api/v1/transactions` — historial completo buyer + vendor con nombres reales
- `GET /api/v1/transactions/:id` — detalle con todos los campos

### Android
- `VendorInboxScreen` — buyer_name real, monto real, botón Confirmar llama API
- `HistoryScreen` — nombres reales, amount_from en USD, filtros + búsqueda funcionales
- `TransactionDetailScreen` — carga datos reales por ID, status badge dinámico, onBack funcional
- `TransactionViewModel` — pendingTransactions, confirmTransaction

### Criterios de Evaluación
- [ ] Vendor Inbox muestra transacciones reales pendientes
- [ ] Confirmar pago actualiza estado a `completed` en tiempo real
- [ ] Historial muestra buyer_name / vendor_name reales (no "Yo" / "Otro")
- [ ] Búsqueda en historial filtra por nombre o ID de transacción
- [ ] Transaction Detail carga todos los campos reales del backend

---

## Sprint 6 — Disputas + Panel Administrador ✅
**Pantallas:** My Disputes, Register Dispute, Dispute Detail (admin), Dispute Resolve (admin), Admin Panel

### Backend
- `POST /api/v1/transactions/:id/dispute` — abrir disputa, estado → `disputed`
- `GET /api/v1/transactions/disputes` — disputas del usuario autenticado
- `GET /api/v1/admin/dashboard` — stats: total_users, total_transactions, total_volume
- `GET /api/v1/admin/disputes` — todas las disputas con filtros por estado
- `POST /api/v1/admin/disputes/:id/resolve` — resolver a favor de buyer/vendor

### Android
- `RegisterDisputeScreen` — dropdown 5 razones funcional, envía al backend
- `MyDisputesScreen` — lista real, "Ver Detalle" navega a TransactionDetail correcto
- `AdminScreen` + `AdminViewModel` — stats reales, lista disputas, acción resolver
- `DisputesViewModel` — createDispute, getDisputes

### Criterios de Evaluación
- [ ] Abrir disputa cambia estado de transacción a `disputed`
- [ ] Disputa aparece en "Mis Disputas" con estado real del backend
- [ ] Admin ve dashboard con números reales de la BD
- [ ] Admin resuelve disputa → estado actualizado
- [ ] "Ver Detalle" desde disputa navega a la transacción correcta

---

## Sprint 7 — Funcionalidades Secundarias + Calidad ⏳
**Pantallas:** Notifications, My Offers, Complaints, Help, About

### Backend
- `PATCH /api/v1/offers/:id` con `{"status": "paused"}` / `{"status": "active"}`
- `DELETE /api/v1/offers/:id` → cierra la oferta definitivamente

### Android
- `MyOffersScreen` — Pausar / Reanudar / Eliminar llaman al backend
- `ComplaintsScreen` — validación descripción, submit con Toast confirmación
- `HelpScreen` — WhatsApp Intent + Email Intent funcionales
- `NetworkSecurityConfig` — HTTP solo para 10.0.2.2 (emulador), HTTPS para el resto
- Tests: `NetworkResultTest`, `ScreenRouteTest`

### Checklist de Calidad del Proyecto
- [ ] Build Android compila sin errores
- [ ] Cero referencias a `Dto` en el código Android
- [ ] Carpeta `model/` (no `dto/`), `bank_accounts/` (no `cards/`)
- [ ] Sin `colors.xml` — colores definidos en `Color.kt`
- [ ] `NetworkSecurityConfig` activo en `AndroidManifest.xml`
- [ ] Backend sin `constants.py` ni `services/` muertos
- [ ] `migrations/sql/` con 10 archivos `.sql` separados por tabla + `00_seed.seed`
- [ ] `docker/` con 4 imágenes `p2p_*` y proyecto `name: p2p`
- [ ] `docker-compose.yml` sin `version:` obsoleto

### Criterios de Evaluación
- [ ] Pausar oferta → desaparece del mercado público
- [ ] Reanudar oferta → vuelve a aparecer para compradores
- [ ] Eliminar oferta → se cierra permanentemente
- [ ] Help abre WhatsApp y cliente de email del dispositivo
- [ ] Flujo completo end-to-end: comprar → pagar → confirmar → receipt → calificar

---

## Resumen de Estado

| Sprint | Feature | Estado |
|--------|---------|--------|
| 1 | Autenticación + Arquitectura | ✅ Completado |
| 2 | Mercado P2P | ✅ Completado |
| 3 | Flujo Transacción Completo | ✅ Completado |
| 4 | Perfil + Cuentas + Reseñas | ✅ Completado |
| 5 | Modo Vendedor + Historial | ✅ Completado |
| 6 | Disputas + Admin | ✅ Completado |
| 7 | Calidad + Extras | ⏳ Pendiente |

---

## Usuarios de Prueba

| Rol | Email | Contraseña |
|-----|-------|------------|
| Comprador | `comprador@peruexchange.com` | `Comprador123!` |
| Vendedor | `vendedor@peruexchange.com` | `Vendedor123!` |
| Admin | `admin@peruexchange.com` | `Admin123!` |

---

## Arrancar el Proyecto

```bash
# Levantar todos los contenedores
cd p2p_backend/docker
docker compose up -d

# Backend disponible en:
http://localhost:5000/api/v1/

# Android Emulator conecta via:
http://10.0.2.2:5000/api/v1/
```

## Estructura del Proyecto

```
ProjectP2PFintechforCurrencyExchange/
├── p2p_android/          ← Kotlin + Jetpack Compose
│   └── app/src/main/java/com/example/p2p/
│       ├── core/         (ApiClient, TokenManager, NetworkResult)
│       ├── data/
│       │   ├── remote/api/     (Retrofit interfaces)
│       │   ├── remote/model/   (Kotlin models)
│       │   └── repository/     (implementaciones)
│       ├── domain/repository/  (interfaces)
│       ├── navigation/         (NavGraph, Screen)
│       ├── presentation/       (14 features: screens + viewmodels)
│       └── ui/theme/           (Color, Theme, Type)
│
├── p2p_backend/          ← Flask + SQLAlchemy + PostgreSQL
│   ├── app/
│   │   ├── api/v1/       (8 blueprints)
│   │   ├── core/         (config, db, security, exceptions)
│   │   └── models/       (SQLAlchemy models)
│   ├── docker/           (Dockerfile×4, docker-compose, nginx)
│   ├── migrations/       (sql/×10 tablas + Alembic versions/)
│   └── wsgi.py
│
└── PeruExchange_Final.html  ← Demo de referencia (29 pantallas)
```
