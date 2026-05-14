# Android · Kotlin

## Qué se usa

| Herramienta | Rol |
|---|---|
| Android Studio | IDE |
| Kotlin | Lenguaje |
| Jetpack Compose | UI declarativa |
| MVVM + Clean Architecture | Patrón de arquitectura |
| Retrofit 2 + OkHttp | Consumo de la Flask API |
| Coroutines + Flow | Asincronía y streams reactivos |
| ViewModel + StateFlow | Estado de pantalla |
| Compose Navigation | Navegación entre pantallas |
| Hilt | Inyección de dependencias |
| Room | Cache local (ofertas, historial, transacciones pausadas) |
| DataStore (Preferences) | Almacenamiento seguro del JWT y datos de sesión |
| Material 3 | Sistema de diseño y componentes |
| Coil | Carga y caché de imágenes |
| WorkManager | Tareas periódicas en background (sincronización OCR, notificaciones) |

---

## Nuevas características implementadas

### 🔔 Notificación de Transacciones Pendientes
- **Botón flotante** en esquina superior derecha mostrando cantidad de transacciones pendientes
- Badge dinámico que se actualiza automáticamente
- Modal deslizable desde abajo con acciones (Liberar/Disputar) directas

### ⏸️ Persistencia de Transacciones
- Las transacciones se guardan en Room cuando se inician
- Si el usuario sale de una transacción, puede continuarla después
- Banner visual en el mercado indicando transacción pausada con opción de continuar
- Se limpia automáticamente al completar o disputar

### 📋 Consolidación de Disputas en Perfil
- Acceso centralizado a todas las disputas (como comprador y vendedor)
- Indicador de rol en cada disputa
- Bandeja unificada sin necesidad de cambiar de modo

### 🏪 Vendor Inbox desde Perfil
- Transacciones pendientes de confirmación accesibles desde el perfil
- Botón directo "Transacciones Pendientes" en "Mi Actividad"
- Notificaciones en tiempo real de nuevos vouchers

---

## Arquitectura de archivos mejorada

```
android/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
│
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    │
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   └── java/com/p2pexchange/app/
        │       │
        │       ├── P2PApplication.kt
        │       ├── MainActivity.kt
        │       │
        │       ├── core/
        │       │   ├── network/
        │       │   │   ├── ApiClient.kt          # Retrofit + OkHttp builder
        │       │   │   ├── AuthInterceptor.kt    # Adjunta el JWT a cada request
        │       │   │   └── NetworkResult.kt      # Sealed class: Success, Error, Loading
        │       │   ├── security/
        │       │   │   └── TokenManager.kt       # DataStore: guardar y leer JWT
        │       │   ├── ui/
        │       │   │   ├── theme/
        │       │   │   │   ├── Color.kt
        │       │   │   │   ├── Theme.kt
        │       │   │   │   └── Type.kt
        │       │   │   └── components/           # Composables reutilizables
        │       │   │       ├── OfferCard.kt
        │       │   │       ├── StatusTimeline.kt
        │       │   │       ├── OcrUploadZone.kt
        │       │   │       └── LoadingOverlay.kt
        │       │   └── utils/
        │       │       ├── DateUtils.kt
        │       │       └── CurrencyFormatter.kt
        │       │
        │       ├── data/
        │       │   ├── remote/
        │       │   │   ├── api/
        │       │   │   │   ├── AuthApi.kt
        │       │   │   │   ├── OfferApi.kt
        │       │   │   │   ├── TransactionApi.kt
        │       │   │   │   ├── RatingApi.kt
        │       │   │   │   └── AdminApi.kt
        │       │   │   └── dto/
        │       │   │       ├── AuthDto.kt
        │       │   │       ├── OfferDto.kt
        │       │   │       ├── TransactionDto.kt
        │       │   │       └── DisputeDto.kt
        │       │   ├── local/
        │       │   │   ├── db/
        │       │   │   │   └── AppDatabase.kt
        │       │   │   └── dao/
        │       │   │       ├── OfferDao.kt
        │       │   │       └── TransactionDao.kt
        │       │   ├── mapper/
        │       │   │   ├── OfferMapper.kt
        │       │   │   └── TransactionMapper.kt
        │       │   └── repository/
        │       │       ├── AuthRepositoryImpl.kt
        │       │       ├── OfferRepositoryImpl.kt
        │       │       ├── TransactionRepositoryImpl.kt
        │       │       └── AdminRepositoryImpl.kt
        │       │
        │       ├── domain/
        │       │   ├── model/
        │       │   │   ├── User.kt
        │       │   │   ├── Offer.kt
        │       │   │   ├── Transaction.kt
        │       │   │   ├── Voucher.kt
        │       │   │   ├── Dispute.kt
        │       │   │   └── Rating.kt
        │       │   ├── repository/
        │       │   │   ├── AuthRepository.kt
        │       │   │   ├── OfferRepository.kt
        │       │   │   └── TransactionRepository.kt
        │       │   └── usecase/
        │       │       ├── LoginUseCase.kt
        │       │       ├── RegisterUseCase.kt
        │       │       ├── GetOffersUseCase.kt
        │       │       ├── MatchOfferUseCase.kt
        │       │       ├── StartTransactionUseCase.kt
        │       │       ├── UploadVoucherUseCase.kt
        │       │       ├── ConfirmPaymentUseCase.kt
        │       │       ├── OpenDisputeUseCase.kt
        │       │       └── RateUserUseCase.kt
        │       │
        │       ├── presentation/
        │       │   ├── auth/
        │       │   │   ├── LoginScreen.kt
        │       │   │   ├── LoginViewModel.kt
        │       │   │   ├── LoginUiState.kt
        │       │   │   ├── RegisterScreen.kt
        │       │   │   └── RegisterViewModel.kt
        │       │   ├── market/
        │       │   │   ├── MarketScreen.kt
        │       │   │   ├── MarketViewModel.kt
        │       │   │   └── MarketUiState.kt
        │       │   ├── offer/
        │       │   │   ├── PublishScreen.kt
        │       │   │   └── PublishViewModel.kt
        │       │   ├── transaction/
        │       │   │   ├── TransactionScreen.kt
        │       │   │   ├── TransactionViewModel.kt
        │       │   │   ├── TransactionUiState.kt
        │       │   │   └── VendorInboxScreen.kt
        │       │   ├── receipt/
        │       │   │   ├── ReceiptScreen.kt
        │       │   │   └── ReceiptViewModel.kt
        │       │   ├── rating/
        │       │   │   ├── RatingScreen.kt
        │       │   │   └── RatingViewModel.kt
        │       │   ├── history/
        │       │   │   ├── HistoryScreen.kt
        │       │   │   └── HistoryViewModel.kt
        │       │   ├── profile/
        │       │   │   ├── ProfileScreen.kt
        │       │   │   └── ProfileViewModel.kt
        │       │   ├── cards/
        │       │   │   ├── BankAccountsScreen.kt
        │       │   │   └── BankAccountsViewModel.kt
        │       │   └── admin/
        │       │       ├── AdminScreen.kt
        │       │       └── AdminViewModel.kt
        │       │
        │       ├── navigation/
        │       │   ├── NavGraph.kt               # Define el grafo completo de navegación
        │       │   └── Screen.kt                 # Sealed class con rutas y argumentos
        │       │
        │       └── di/
        │           ├── NetworkModule.kt          # Provee Retrofit, OkHttp, ApiClient
        │           ├── DatabaseModule.kt         # Provee Room, DAOs
        │           └── RepositoryModule.kt       # Bindea interfaces con implementaciones
        │
        └── test/
            ├── java/com/p2pexchange/app/
            │   ├── presentation/                 # Tests de ViewModel
            │   └── domain/                       # Tests de UseCases
            └── androidTest/                      # Tests de UI con Compose
```

---

## Resumen de vistas

| Pantalla | Descripción |
|---|---|
| **SplashScreen** | Logo animado, barra de progreso, redirige automáticamente a Login |
| **LoginScreen** | Email + contraseña, toggle a registro, enlace recuperar contraseña, Google auth |
| **RegisterScreen** | Nombre, email, contraseña, aceptar términos, validaciones en tiempo real, Google signup |
| **MarketScreen** | **NEW:** Banner compacto con nivel y notificación de transacciones pendientes. Ticker de tasas, filtros (Tengo/Quiero), lista de ofertas, botón matching automático. **NEW:** Banner de transacción pausada con opción de continuar |
| **PublishScreen** | Publicar oferta: monedas, monto total, mínimo y máximo, tasa propia, banco de recepción |
| **TransactionScreen** | **NEW:** Botón back para pausar transacción. Temporizador, timeline 4 pasos, CCI, chat con vendedor, subida OCR, opción disputa. Persistencia automática |
| **VendorInboxScreen** | **CONSOLIDADO EN PERFIL:** Transacciones pendientes, botones Liberar/Disputar |
| **VendorNotificationModal** | **NEW:** Modal deslizable con transacciones pendientes, acciones directas, badge dinámico |
| **DisputesProfileScreen** | **NEW:** Consolidado en perfil. Todas las disputas (comprador/vendedor) en un lugar |
| **ReceiptScreen** | Comprobante con ID, partes, tasa, monto, OCR, descarga PDF, calificar |
| **RatingScreen** | Calificación 1–5 estrellas con comentario opcional |
| **HistoryScreen** | Historial filtrable: Todos / Completados / Pendientes / En disputa |
| **ProfileScreen** | Avatar, calificación, nivel, KYC badge, stats. **NEW:** Acceso a Editar Perfil, Transacciones Pendientes, Mis Disputas |
| **EditProfileScreen** | **NEW:** Editar nombre, teléfono, país, moneda, notificaciones |
| **BankAccountsScreen** | Lista de cuentas (BCP, Interbank, BBVA, Yape, Plin), agregar nueva |
| **LegalScreens** | **NEW:** Términos y Condiciones, Política de Privacidad, Acerca de, Ayuda |
| **AdminScreen** | Solo rol admin: stats globales, disputas activas, acciones Liberar o Revertir |
