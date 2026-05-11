# Proyecto P2P de divisas

Este proyecto toma como base el PDF del curso y usa `Maquetado.html` como referencia visual de la interfaz. La meta es convertir ese flujo en una app movil real con Kotlin y un backend en Flask API.

## Documentacion

- Base de datos: [Docs/BD.md](Docs/BD.md)
- Backend Flask API: [Docs/Backend.md](Docs/Backend.md)
- Android Kotlin: [Docs/Android.md](Docs/Android.md)
- Indice general: [Docs/Arquitectura_BD_Backend_Mobile.md](Docs/Arquitectura_BD_Backend_Mobile.md)
- Documento base del proyecto: https://docs.google.com/document/d/17NVohPg2bjVlZ_WTKR1ElYv2Bw3S3ONzCkSl15gZHbk/edit?usp=sharing

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

## Backend en desarrollo

- `app/api`: expone endpoints versionados por dominio.
- `app/core`: configuracion, seguridad, excepciones y extensiones.
- `app/modules`: logica de negocio por feature.
- `app/repositories`: acceso a datos.
- `app/services`: casos de uso y reglas de negocio.
- `tests`: pruebas unitarias e integracion.

## Backend en produccion

- Flask ejecutado detras de Gunicorn o equivalente.
- Nginx como reverse proxy.
- PostgreSQL como base principal.
- Redis opcional para cache y colas livianas.
- Storage externo para vouchers y comprobantes.
- Logs, monitoreo y backups automatizados.
- Variables de entorno para secretos.
- HTTPS/TLS obligatorio.

## Infraestructura del backend

Docker Compose forma parte de la arquitectura del backend porque permite definir el entorno local y la base de despliegue reproducible junto con Flask, PostgreSQL, Redis y Nginx.

Flujo sugerido en local:

1. Copiar variables de entorno.
2. Levantar servicios con Docker Compose.
3. Ejecutar migraciones.
4. Cargar datos base si aplica.
5. Probar la API y la app Android contra ese entorno.

Para produccion, la idea es usar la misma arquitectura con imagen publicada, secretos inyectados por entorno y servicios endurecidos.

## Alcance

- Login y roles.
- Publicacion y busqueda de ofertas.
- Matching automatico.
- Transacciones P2P con estados.
- Subida de voucher y validacion OCR.
- Disputas y panel administrativo.
- Historial y calificacion de usuarios.

## Evaluacion del estado actual

La arquitectura documental ya esta lista para avanzar al desarrollo porque separa bien datos, backend y mobile, y ya considera seguridad, escalabilidad, pruebas y despliegue.

Lo que aun falta para decir que el proyecto esta listo al 100% es la implementacion real:

- crear el backend Flask con sus rutas, modelos, servicios y pruebas;
- crear la app Android con su navegacion, estados y consumo real de API;
- definir migraciones y datos semilla;
- configurar CI/CD, secretos y despliegue;
- integrar OCR, notificaciones y almacenamiento de archivos.

Con la estructura actual, si se puede proseguir con el proyecto sin rehacer la arquitectura.

## Nota

El archivo `Maquetado.html` no es el producto final. Solo define la interfaz y el orden de pantallas para construir la app Android y la API.