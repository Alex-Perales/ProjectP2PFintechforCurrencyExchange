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
| Room | Cache local (ofertas, historial) |
| DataStore (Preferences) | Almacenamiento seguro del JWT |
| Material 3 | Sistema de diseño y componentes |
| Coil | Carga y caché de imágenes |

---

## Arquitectura de archivos

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
| **LoginScreen** | Email + contraseña, toggle a registro, enlace recuperar contraseña |
| **RegisterScreen** | Nombre, email, contraseña, aceptar términos, validaciones en tiempo real |
| **MarketScreen** | Ticker de tasas en vivo, filtros par de monedas (Tengo / Quiero), lista de ofertas por mejor tasa, indicador online/offline, badge verificado, % completadas, tiempo de respuesta, límites min/max, botón de matching automático |
| **PublishScreen** | Publicar oferta: monedas, monto total, mínimo y máximo por operación, tasa propia, banco de recepción, tiempo límite de pago |
| **TransactionScreen** | Temporizador regresivo, timeline 4 pasos (Pagar → Voucher → Confirmar → Liberado), datos de cuenta receptora con CCI, mini-chat con el vendedor, zona de subida de voucher con IA OCR, espera o disputa |
| **VendorInboxScreen** | Bandeja del vendedor: transacciones con voucher validado por OCR, botones Liberar Fondos o Disputar |
| **ReceiptScreen** | Comprobante con ID, partes, tasa, monto acreditado, resultado OCR, descarga PDF, acceso a calificar |
| **RatingScreen** | Calificación 1–5 estrellas con comentario opcional |
| **HistoryScreen** | Historial filtrable: Todos / Completados / Pendientes / En disputa |
| **ProfileScreen** | Avatar, calificación promedio, nivel, KYC badge, stats (operaciones, % completadas, tiempo respuesta), menú |
| **BankAccountsScreen** | Lista de cuentas (BCP, Interbank, BBVA, Yape, Plin), agregar nueva |
| **AdminScreen** | Solo rol admin: stats globales, disputas activas con motivo, acciones Liberar o Revertir |
