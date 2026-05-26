# Proyecto P2P de divisas

Este proyecto toma como base el PDF del curso y usa `Maquetado.html` como referencia visual de la interfaz. La meta es convertir ese flujo en una app movil real con Kotlin y un backend en Flask API.

## Documentacion

- Base de datos: [Docs/BD.md](Docs/BD.md)
- Backend Flask API: [Docs/Backend.md](Docs/Backend.md)
- Android Kotlin: [Docs/Android.md](Docs/Android.md)
- Indice general: [Docs/Arquitectura_BD_Backend_Mobile.md](Docs/Arquitectura_BD_Backend_Mobile.md)
- Tasas de cambio y APIs: [Docs/Exchange_Rates_API.md](Docs/Exchange_Rates_API.md)
- Documento Historias de usuario: https://docs.google.com/document/d/17NVohPg2bjVlZ_WTKR1ElYv2Bw3S3ONzCkSl15gZHbk/edit?usp=sharing

## Stack

- Mobile: Android Studio, Kotlin, MVVM.
- Backend: Flask API, SQLAlchemy, JWT.
- BD: PostgreSQL.
- Infraestructura del backend: Docker, Docker Compose como parte de la arquitectura, Nginx y almacenamiento para vouchers.

## Arquitectura de trabajo

- Base de datos: [Docs/BD.md](Docs/BD.md)
- Backend Flask API: [Docs/Backend.md](Docs/Backend.md)
- Android Kotlin: [Docs/Android.md](Docs/Android.md)
- Indice general: [Docs/Arquitectura_BD_Backend_Mobile.md](Docs/Arquitectura_BD_Backend_Mobile.md)


## Alcance

- Login y roles.
- Publicacion y busqueda de ofertas.
- Matching automatico.
- Transacciones P2P con estados.
- Subida de voucher y validacion OCR.
- Disputas y panel administrativo.
- Historial y calificacion de usuarios.


## Distribución de Historias de Usuario

| Integrante | Historias de Usuario |
|------------|----------------------|
| Shirley    | US01 → US02 → US03   |
| Emily      | US04 → US12          |
| Andres     | US17 → US18 → US19   |
| Alex       | US05 → US06 → US07 → US16 |
| Jasmín     | US08 → US09 → US10   |
| Mateo      | US11 → US13 → US14 → US15 |

## Credenciales de Prueba

| Rol       | Email                      | Contraseña    |
|-----------|----------------------------|---------------|
| Comprador | comprador@peruexchange.com | Comprador123! |
| Vendedor  | vendedor@peruexchange.com  | Vendedor123!  |
| Admin     | admin@peruexchange.com     | Admin123!     |

## Nota

El archivo `Maquetado.html` no es el producto final. Solo define la interfaz y el orden de pantallas para construir la app Android y la API.
