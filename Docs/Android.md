# Arquitectura Android Kotlin

## Objetivo

La app Android debe consumir la Flask API y representar el flujo del proyecto P2P en pantallas reales, manteniendo el orden visual del maquetado pero con logica implementada en Kotlin.

## Stack recomendado

- Android Studio.
- Kotlin.
- Arquitectura MVVM.
- Retrofit para consumir la API.
- Coroutines y Flow para asincronia.
- ViewModel para estado de pantalla.
- Room opcional para cache local.
- Jetpack Navigation para navegacion.

## Estructura real de la app Android

La app debe organizarse por capas y por feature para que escale sin volverse un proyecto monolitico. Una estructura base seria:

```text
android/
├── app/
│   ├── src/main/java/com/proyectomobiles/
│   │   ├── core/
│   │   │   ├── network/
│   │   │   ├── security/
│   │   │   ├── ui/
│   │   │   ├── theme/
│   │   │   └── utils/
│   │   ├── data/
│   │   │   ├── remote/
│   │   │   ├── local/
│   │   │   ├── dto/
│   │   │   ├── mapper/
│   │   │   └── repository/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── usecase/
│   │   ├── presentation/
│   │   │   ├── auth/
│   │   │   ├── market/
│   │   │   ├── offer/
│   │   │   ├── transaction/
│   │   │   ├── voucher/
│   │   │   ├── dispute/
│   │   │   ├── profile/
│   │   │   └── admin/
│   │   ├── navigation/
│   │   └── di/
│   └── res/
├── build.gradle.kts
└── settings.gradle.kts
```

## Responsabilidad de cada capa

- `core`: red, seguridad, tema visual, utilidades y componentes comunes.
- `data`: acceso a API, base local, DTOs y mapeo.
- `domain`: reglas puras de negocio y contratos.
- `presentation`: pantallas, estado de UI y navegacion por feature.
- `di`: inyeccion de dependencias.

## Estructura por feature

Cada flujo importante debe vivir en su propio paquete:

- `auth`: login, registro y manejo de sesion.
- `market`: listado, filtros y ofertas disponibles.
- `offer`: publicar y editar oferta.
- `transaction`: detalle, estados y seguimiento.
- `voucher`: carga de voucher y resultado OCR.
- `dispute`: apertura y seguimiento del conflicto.
- `profile`: tarjetas, cuentas e historial.
- `admin`: vista de arbitraje y control.

## Como se ve la app en la practica

- La app abre en login.
- Tras autenticarse, entra al mercado con filtros de moneda.
- El usuario publica oferta o compra una disponible.
- La transaccion avanza en una pantalla con timeline claro.
- El voucher se sube desde una vista dedicada.
- El comprobante final y el historial quedan separados.
- Si el rol es admin, aparece la seccion de arbitraje.

## Navegacion sugerida

- `Splash -> Login -> Market -> Offer Detail -> Transaction -> Voucher -> Receipt -> History`
- `Profile -> Bank Accounts`
- `Admin -> Disputes -> Resolution`

## State management

- Cada feature debe tener su `ViewModel` o `StateHolder`.
- Las pantallas no deben hablar directo con Retrofit.
- Los repositorios devuelven estados claros: loading, success y error.

## Escalabilidad

- Agregar una nueva feature no debe romper las existentes.
- Los componentes compartidos deben vivir en `core`.
- Los contratos de dominio no deben depender de UI.
- La app puede crecer hacia Compose sin cambiar la logica central.

## Seguridad movil

- Guardar tokens en almacenamiento seguro del sistema, no en texto plano.
- Renovar sesion con refresh token y forzar logout ante expiracion.
- Usar HTTPS siempre y, si el proyecto lo justifica, certificate pinning.
- Validar respuestas del backend antes de pintarlas en UI.
- No exponer secretos, claves ni URLs internas en el cliente.
- Bloquear capturas o acciones sensibles si el caso de uso lo requiere.
- Mantener cache local solo para datos no sensibles o cifrados.

## Rendimiento movil

- Cargar listas con paginacion o carga incremental.
- Evitar hacer llamadas directas desde la vista.
- Mantener estados livianos y recomposiciones controladas.
- Manejar errores de red con reintentos y mensajes claros.
- Reducir payloads y descargar solo lo necesario por pantalla.

## Pruebas

- pruebas de ViewModel.
- pruebas de repositorios.
- pruebas de mapeo de DTOs.
- pruebas de pantallas criticas del flujo.

Esta organizacion hace que la app quede ordenada, mantenible y lista para crecer con mas modulos sin reescribirla desde cero.

## UI, maquetado y alcance

### Pantallas principales

- Login y registro.
- Mercado de ofertas.
- Publicacion de oferta.
- Matching o seleccion manual de oferta.
- Detalle de transaccion.
- Carga de voucher.
- Seguimiento de estados.
- Comprobante final.
- Perfil y medios de cobro.
- Historial.
- Vista admin si el rol lo permite.

### Mapeo con el maquetado

- Inicio y autenticacion.
- Vista de mercado con filtros.
- Vista de publicacion.
- Vista de transaccion con timeline.
- Vista de recibo final.
- Vista de perfil y tarjetas.
- Panel administrativo inferior.

El archivo `Maquetado.html` solo sirve como referencia de interfaz y secuencia visual.

### Capas de la app

- Presentation: screens, composables o activities/fragments y manejo de estado.
- ViewModel: orquesta llamadas a casos de uso o repositorios.
- Domain: login, ofertas, transacciones, voucher, disputa y calificacion.
- Data: repository, Retrofit service, DTOs, mapeadores y Room opcional.

### Flujo funcional en la app

1. El usuario inicia sesion.
2. Ve el mercado y filtra por moneda.
3. Publica una oferta o selecciona una disponible.
4. La app crea la transaccion mediante la API.
5. El usuario sube el voucher.
6. La app muestra el estado de OCR y validacion.
7. El flujo termina en comprobante o disputa.
8. El usuario puede revisar historial y calificaciones.

### Consideraciones de UI

- Mantener una navegacion simple y clara.
- Respetar el orden visual del prototipo.
- Mostrar estados de transaccion con timeline.
- Priorizar claridad en montos, tasas y moneda.
- Diferenciar vista de usuario y vista admin por rol.

### Alcance cubierto

La app cubre todo lo pedido en el PDF:

- login;
- publicacion de ofertas;
- busqueda y filtrado;
- matching automatico;
- transacciones;
- confirmacion de pago;
- voucher;
- calificacion;
- historial;
- disputas;
- admin.