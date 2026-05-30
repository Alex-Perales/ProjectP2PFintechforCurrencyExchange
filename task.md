# Tareas del Proyecto Perú Exchange P2P

- [x] **Sprint 1: Autenticación Simple y Base del Proyecto**
  - [x] Backend: Inicializar base de datos PostgreSQL.
  - [x] Backend: Crear endpoint de Login simple (que devuelva JWT).
  - [x] Android: Configurar Retrofit, Hilt y navegación básica.
  - [x] Android: Implementar pantalla de Login simple e integrar API.
  - [x] Android: Guardar JWT en DataStore.

- [x] **Sprint 2: Mercado y Publicación de Ofertas (Core)**
  - [x] Backend: Endpoints para listar ofertas activas (`GET /offers`).
  - [x] Backend: Endpoints para crear ofertas (`POST /offers`).
  - [x] Backend: Lógica API Exchange Rates.
  - [x] Android: Implementar vista "Mercado" (lista real de ofertas desde el API).
  - [x] Android: Implementar vista "Publicar" (formulario de creación funcional).
  - [x] Android: Integrar llamadas a la API de ofertas y ViewModels.

- [x] **Sprint 3: Transacciones P2P y Estados**
  - [x] Backend: Endpoint de matching/inicio de transacción (`POST /transactions`).
  - [x] Backend: Endpoint de detalle y estado de transacción.
  - [x] Android: Implementar vista "Detalle de Transacción" funcional (temporizador, actualización de estado con API).
  - [x] Android: Integrar persistencia (Room/DataStore) e integrarse a endpoints de transacción.

- [x] **Sprint 4: Comprobantes (Vouchers) y Liberación**
  - [x] Backend: Endpoint para subir voucher (`POST /transactions/voucher`).
  - [x] Backend: Endpoint para confirmar/liberar fondos (`PATCH /transactions/confirm`).
  - [x] Android: Implementar funcionalidad completa para adjuntar voucher y enviarlo al API.
  - [x] Android: Implementar vista y lógica de liberación de fondos para el vendedor (integración API).

- [x] **Sprint 5: Historial, Perfil y Disputas**
  - [x] Backend: Endpoints de historial y perfil (`GET /me`).
  - [x] Backend: Endpoints para disputas.
  - [x] Android: Implementar vista "Mi Actividad / Perfil" consumiendo la API de datos reales.
  - [x] Android: Implementar lógica de creación y vista de disputas en el Vendor Inbox.

- [x] **Sprint 6: Registro, Validaciones y Pulido Final**
  - [x] Backend: Endpoint completo de Registro (`POST /register`) con validaciones (Mockeado/Omitido).
  - [x] Backend: Endpoints de calificación (`POST /ratings`).
  - [x] Android: Implementar pantalla de Registro compleja con consumo de API y manejo de errores (Mockeado/Omitido).
  - [x] Android: Implementar flujo completo de calificación en frontend.
  - [x] Android: Pulido final de animaciones, estados de carga (Loaders) y manejo general de errores.
