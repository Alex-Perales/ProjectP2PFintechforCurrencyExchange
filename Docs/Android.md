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

---

## Recomendaciones y mejoras (sugeridas)

- **Seguridad móvil:** usar Android Keystore + EncryptedSharedPreferences para almacenar tokens; habilitar `BiometricPrompt` (huella/face) para acciones sensibles como liberar fondos o firmar contratos.
- **Network security:** certificate pinning con OkHttp, timeouts explícitos, retries exponenciales y detección de redes no confiables.
- **Idempotencia y reconexión:** diseñar endpoints que acepten `idempotency-key` para creación de transacciones; la app debe reintentar de forma segura sin duplicar operaciones.
- **Offline-first:** cache en `Room` + sincronización con `WorkManager` para transacciones pausadas; mantener solo los metadatos necesarios localmente (no vouchers completos).
- **Privacidad:** minimizar registro de PII en logs; usar redaction; petición de permisos contextual y cumplir políticas de retención definidas en `Docs/BD.md`.
- **Testing & CI:** cubrir ViewModel y UseCases con unit tests, Compose UI tests e instrumented tests; integrar `GitHub Actions` para build/lint/tests y releases automáticos.
- **Observabilidad:** integrar `Sentry` o `Firebase Crashlytics` y trazas básicas para errores críticos; permitir opt-in de analytics.
- **Performance:** optimizar listas con `LazyColumn`, usar `Coil` con placeholders y límites de memoria; configurar `Proguard`/`R8` y mantener reglas mínimas para mantener nombres necesarios.
- **Release & Play Store:** configurar `signingConfig`, versionamiento semántico, políticas de privacidad en la ficha y lista de permisos justificados para Play Console.
- **Accesibilidad & localización:** usar `contentDescription`, contrastes adecuados, soporte TalkBack, y strings traducibles (es, en — posible expansión a otros países).

---

## 🔴 Manejo de Errores en UI

### ErrorResponse del Backend
```kotlin
// data/remote/dto/ErrorResponse.kt
data class ErrorResponse(
    val error: ErrorDetails
)

data class ErrorDetails(
    val code: String,                    // VALIDATION_ERROR, UNAUTHORIZED, etc.
    val message: String,
    val timestamp: String,
    val details: Map<String, String>?    // field -> reason
)
```

### UserMessage en ViewModel
```kotlin
// Sealed class para estados de error en presentación
sealed class UiState<T> {
    class Loading<T> : UiState<T>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(
        val errorCode: String,
        val userMessage: String,           // Mensaje amigable para el usuario
        val details: String? = null
    ) : UiState<T>()
}

// ViewModel mapea errores
fun handleError(throwable: Throwable): Error<Unit> {
    return when {
        throwable is HttpException && throwable.code() == 422 -> {
            val errorResponse = parseErrorResponse(throwable)
            Error(
                errorCode = errorResponse.error.code,
                userMessage = "Datos inválidos: ${errorResponse.error.details?.values?.firstOrNull()}",
                details = errorResponse.error.message
            )
        }
        throwable is HttpException && throwable.code() == 401 -> {
            Error(
                errorCode = "UNAUTHORIZED",
                userMessage = "Tu sesión expiró. Por favor, vuelve a iniciar sesión."
            )
        }
        throwable is SocketTimeoutException -> {
            Error(
                errorCode = "TIMEOUT",
                userMessage = "La conexión tardó demasiado. Verifica tu conexión e intenta de nuevo."
            )
        }
        else -> {
            Error(
                errorCode = "UNKNOWN_ERROR",
                userMessage = "Algo salió mal. Intenta de nuevo."
            )
        }
    }
}
```

### UI Toast/Snackbar
```kotlin
// presentation/transaction/TransactionScreen.kt
when (val uiState = transactionState.collectAsState().value) {
    is UiState.Error -> {
        LaunchedEffect(uiState.errorCode) {
            snackbarHostState.showSnackbar(
                message = uiState.userMessage,
                duration = SnackbarDuration.Long,
                actionLabel = "Reintentar"
            )
        }
    }
    is UiState.Success -> { ... }
    is UiState.Loading -> { ... }
}
```

---

## 🔄 WebSocket: Notificaciones en Tiempo Real

### Conexión y Reconexión Automática
```kotlin
// core/network/WebSocketManager.kt
class WebSocketManager(
    private val tokenManager: TokenManager,
    private val logger: Logger
) {
    private var webSocket: WebSocket? = null
    private val reconnectJob: Job? = null
    private val backoffMs = mutableListOf<Long>()  // Exponential backoff

    fun connect(onEvent: (NotificationEvent) -> Unit) {
        val token = tokenManager.getAccessToken()
        val request = Request.Builder()
            .url("wss://api.peruexchange.com/api/v1/ws/notifications")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = OkHttpClient().newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                logger.d("WebSocket connected")
                backoffMs.clear()
            }

            override fun onMessage(ws: WebSocket, text: String) {
                val event = parseNotificationEvent(text)
                onEvent(event)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                logger.e("WebSocket error: ${t.message}")
                scheduleReconnect()
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                if (code != 1000) {  // 1000 = normal close
                    scheduleReconnect()
                }
            }
        })
    }

    private fun scheduleReconnect() {
        val delay = if (backoffMs.size < 5) {
            (1000L * Math.pow(2.0, backoffMs.size.toDouble())).toLong()
        } else {
            30_000L  // Max 30 segundos
        }
        backoffMs.add(delay)
        reconnectJob?.cancel()
        reconnectJob = GlobalScope.launch {
            delay(delay)
            connect { /* reuse callback */ }
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnect")
    }
}

// Notificaciones parseadas
data class NotificationEvent(
    val eventType: String,  // transaction_status_changed, voucher_validated, etc.
    val transactionId: String,
    val newStatus: String?,
    val timestamp: String
)
```

### Fallback a Polling
```kotlin
// domain/usecase/PollNotificationsUseCase.kt
class PollNotificationsUseCase(
    private val transactionApi: TransactionApi
) {
    operator fun invoke(since: Long): Flow<List<NotificationEvent>> = flow {
        while (currentCoroutineContext().isActive) {
            try {
                val notifications = transactionApi.getNotifications(since)
                emit(notifications)
                delay(5_000)  // Poll cada 5 segundos
            } catch (e: Exception) {
                // Emitir error pero continuar retentando
                delay(10_000)  // Back off 10 segundos en error
            }
        }
    }
}

// En ViewModel: intentar WebSocket, fallback a polling
val notificationFlow = if (isWebSocketAvailable()) {
    wsManager.connectAndObserve()
} else {
    pollNotificationsUseCase(since = System.currentTimeMillis() / 1000)
}
```

---

## 💾 WorkManager: Pausas de Transacciones

### Guardar Transacción Pausada en Local + Redis
```kotlin
// data/local/model/PausedTransaction.kt
@Entity(tableName = "paused_transactions")
data class PausedTransactionEntity(
    @PrimaryKey val id: String,           // transaction_id
    val offerData: String,                // JSON serializado
    val currentStep: Int,                 // 0-3
    val voucherPath: String?,             // Ruta local del comprobante en progreso
    val cciEntered: String?,              // CCI del comprador
    val pausedAt: Long,                   // timestamp
    val expiresAt: Long                   // timestamp + 24h
)

// TransactionRepositoryImpl
suspend fun pauseTransaction(txId: String, metadata: PausedMetadata) {
    // 1. Guardar en Room
    val entity = mapToEntity(txId, metadata)
    transactionDao.insertPaused(entity)

    // 2. Sincronizar a Backend (opcional)
    try {
        transactionApi.pauseTransaction(
            txId = txId,
            body = PauseRequest(
                paused_metadata = metadata.toJson()
            )
        )
    } catch (e: Exception) {
        // Si falla backend, ya tenemos en local. WorkManager retentará later.
    }
}

suspend fun resumeTransaction(txId: String): Result<Transaction> {
    // 1. Recuperar de Room
    val pausedTx = transactionDao.getPaused(txId) ?: return Result.failure(Exception("Not found"))

    // 2. Recuperar del Backend (más actualizado)
    return try {
        val updated = transactionApi.resumeTransaction(txId)
        Result.success(updated)
    } catch (e: Exception) {
        // Fallback a local
        Result.success(mapFromEntity(pausedTx))
    }
}
```

### WorkManager para Sincronizar Pausadas
```kotlin
// di/WorkModule.kt
class PauseTransactionSyncWorker(
    context: Context,
    params: WorkerParameters,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        val pausedTxs = transactionRepository.getAllPausedLocal()

        pausedTxs.forEach { tx ->
            if (tx.expiresAt < System.currentTimeMillis()) {
                // Expirada, eliminar
                transactionRepository.deletePaused(tx.id)
            } else {
                try {
                    transactionRepository.syncPauseToBackend(tx.id)
                } catch (e: Exception) {
                    // Reintentar después
                    return@forEach
                }
            }
        }

        Result.success()
    } catch (e: Exception) {
        // Reintentar después (BackoffPolicy.EXPONENTIAL)
        Result.retry()
    }
}

// En app startup (MainActivity o Application)
val syncRequest = PeriodicWorkRequestBuilder<PauseTransactionSyncWorker>(
    repeatInterval = 30,
    repeatIntervalTimeUnit = TimeUnit.MINUTES
).build()

WorkManager.getInstance(this).enqueueUniquePeriodicWork(
    "pause_sync",
    ExistingPeriodicWorkPolicy.KEEP,
    syncRequest
)
```

### UI: Banner de Transacción Pausada
```kotlin
// presentation/market/MarketScreen.kt
@Composable
fun MarketScreen(viewModel: MarketViewModel) {
    val pausedTx by viewModel.pausedTransaction.collectAsState()
    val offers by viewModel.offers.collectAsState()

    Column {
        // Banner si hay pausada
        pausedTx?.let { tx ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        // Navegar a TransactionScreen con ID
                        navController.navigate(
                            Screen.Transaction.createRoute(tx.id)
                        )
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Transacción en progreso", style = MaterialTheme.typography.labelMedium)
                        Text("${tx.amount} ${tx.currency}", style = MaterialTheme.typography.bodyMedium)
                    }
                    Button(onClick = { /* continue */ }) {
                        Text("Continuar")
                    }
                }
            }
        }

        // Lista de ofertas
        LazyColumn {
            items(offers) { offer ->
                OfferCard(offer = offer)
            }
        }
    }
}
```

---

## 📋 Roadmap de Implementación (8 Fases Independientes)

