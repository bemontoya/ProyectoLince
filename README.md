# Southbound Field (Proyecto Lince)

**Southbound Field** es una aplicación móvil nativa en Android diseñada para la gestión, control operativo y trazabilidad en tiempo real del transporte y excursiones en la Zona Sur de Chile (Puerto Varas, Puerto Montt, Ensenada, etc.). La herramienta optimiza la coordinación diaria entre **Drivers (Conductores)**, **Guías** y **Coordinadores**, sustituyendo planillas físicas por un flujo digital fluido, reactivo y con soporte **offline-first**.

---

## Integrantes del Equipo (SyntaxSouls)
- **Sección:** 003D
- **Benjamin Montoya** — Líder de equipo
- **Deiner Quero** — Desarrollador
- **Matias Jeldres** — Desarrollador
- **Victor Huenchucheo** — Desarrollador

---

## Problemática Identificada

Las empresas de turismo y transporte de pasajeros enfrentan retrasos, mala comunicación en ruta e ineficiencias en los procesos de Check-in y Check-out debido a la recopilación manual de datos en planillas físicas, la pérdida de conectividad en zonas rurales o cordilleranas y la falta de validación geográfica en terreno.

**Solución:** Centralizar la agenda operativa en una app móvil Android con validación de geocerca GPS (150 m), persistencia local con sincronización automática al recuperar señal y trazabilidad en tiempo real hacia los sistemas de coordinación.

---

## Arquitectura del Sistema

La aplicación está construida bajo un enfoque **MVVM (Model - View - ViewModel)** con **Jetpack Compose** y estrategia **Offline-First**:

- **Capa de Presentación (UI):** Jetpack Compose + Material Design 3 (`LoginScreen`, `ServiciosScreen`, `DetalleScreen`, `DisponibilidadScreen`, `PerfilScreen`), con soporte bilingüe (`ES` / `EN`) y diseño adaptable al teclado.
- **Capa de Estado y Lógica (ViewModels):** `ServiciosViewModel` y `DetalleViewModel` gestionan los estados reactivos (`StateFlow`), validación de geocerca GPS mediante fórmula de Haversine (radio de 150 m) y ventana de anticipación operativa (15 minutos).
- **Capa de Persistencia Local (Offline-First):**
  - **Room Database:** Almacena registros de Check-in/Check-out, confirmaciones e incidencias con `idempotencyKey` único para evitar duplicados.
  - **`OfflineLinceCache`:** Guarda en caché local el catálogo y el detalle de servicios para operar sin conexión.
  - **WorkManager (`SyncOperacionesWorker`):** Sincroniza automáticamente en segundo plano las operaciones pendientes cuando el dispositivo recupera conexión a Internet.
- **Capa de Red (Retrofit 2 + OkHttp):** Comunicación híbrida con el microservicio backend propio en Spring Boot (Render), la API operativa de Southbound Travel y la API meteorológica de OpenWeatherMap.

---

## Documentación de APIs y Endpoints

### Enlaces Directos
- **URL Swagger UI (Backend Producción):** [Abrir Swagger UI — Proyecto Lince Backend](https://proyectolince-backend.onrender.com/swagger-ui/index.html#/)
- **API Operativa Southbound Travel:** `https://ws.southbound.travel/` *(VirtualHost local/staging sobre servidor WAMP — módulo `Lince` / `API`, respaldado con caché offline-first en la app)*
- **APK Release Firmada:** Disponible en la sección [**Releases**](../../releases) de este repositorio.

### Principales Endpoints Integrados
- **Backend Propio (`https://proyectolince-backend.onrender.com`):**
  - `POST /api/auth/login` — Autenticación de usuarios y asignación de rol (`Driver`, `Guía`, `Coordinador`).
  - `GET /api/servicios` — Consulta de servicios asignados y estado operativo sincronizado.
  - `POST /api/servicios/{id}/confirmar` — Confirmación o rechazo con motivo de reasignación.
  - `POST /api/operaciones/checkin` — Registro idempotente de Check-in con coordenadas GPS, precisión y validación de geocerca.
  - `POST /api/operaciones/checkout` — Registro idempotente de Check-out y cierre de servicio.
  - `POST /api/disponibilidad/bloqueos` — Gestión de bloqueos de agenda con validación de anticipación mínima.
- **API Southbound Travel (`https://ws.southbound.travel/`):**
  - Consumo del catálogo de transfers, servicios guiados y excursiones (`TRANSFER`, `GUIDED_SERVICE`, `EXCURSION`), itinerarios de origen/destino y pasajeros (`pax`).
- **API Meteorológica (`OpenWeatherMap`):**
  - `GET /data/2.5/weather` — Consulta de condiciones climáticas en tiempo real según coordenadas de ruta.

---

## Roles y Funcionalidades Clave

1. **Driver (Conductor):** Realiza check-in/check-out de pasajeros en transfers, consulta horarios, navega hacia el punto de pickup y coordina traslados.
2. **Guía de Turismo:** Accede al detalle de excursiones, listas de pasajeros (`pax`), rutas e información meteorológica.
3. **Coordinador:** Supervisa asignaciones, confirma o reasigna servicios rechazados y gestiona la disponibilidad de flota.

### Recursos Nativos Integrados
- **GPS / Ubicación en tiempo real:** Cálculo de distancia al punto de pickup para validación de geocerca (150 m).
- **Llamadas telefónicas:** Activación directa del marcador telefónico (`Intent.ACTION_DIAL`) para contactar al pasajero o Despacho central.
- **Navegación GPS Externa:** Apertura directa de Google Maps / Waze (`Intent.ACTION_VIEW` con esquema `geo:`) hacia origen o destino.

---

## Identidad Visual y UI

- **Nombre de la App:** Southbound Field
- **Logotipo:** Configurado desde `R.drawable.logo_lince` y como icono nativo de la aplicación.
- **Paleta de Colores (Material Design 3):**
  - **Azul Principal (`AzulPrincipal`):** TopAppBar, botones primarios y pestañas activas.
  - **Verde Confirmado (`VerdeConfirmado` / `VerdeFondoBadge`):** Estados confirmados, llamadas y check-in exitoso.
  - **Gris & Fondo (`FondoPantalla` / `TextoGris` / `TextoOscuro`):** Contraste de alta legibilidad para uso en terreno bajo luz solar.

---

## Pantallas Principales

1. **Login Screen (`LoginScreen.kt`):** Selector de rol, conmutador de idioma (`ES` | `EN`), precarga en segundo plano del backend en Render y ajuste dinámico ante el teclado virtual.
2. **Servicios (`ServiciosScreen.kt`):** Agenda filtrable por fecha (**Hoy**, **Calendario**), tipo de servicio y estado, con banner indicador de modo sin conexión.
3. **Detalle del Servicio (`DetalleScreen.kt`):** Datos del servicio desde Southbound, clima en ruta, validación de geocerca (150 m), timeline Origen → Destino, Check-in / Check-out persistido en Room + WorkManager, llamadas y navegación GPS.
4. **Disponibilidad (`DisponibilidadScreen.kt`):** Bloqueo de fechas con validación de anticipación y detección de conflictos con servicios asignados.

---

## Pasos de Instalación de la APK (Versión Release Firmada)

1. **Descargar la APK:**
   - Ingresa a la sección [**Releases**](../../releases) de este repositorio en GitHub y descarga el archivo **`app-release.apk`** .
2. **Preparar el dispositivo Android (Android 8.0 / API 26 o superior):**
   - Si tienes instalada una versión previa de desarrollo (*debug*), desinstálala antes para evitar conflictos de firma digital.
   - En el teléfono, ve a **Ajustes → Seguridad / Privacidad → Instalar aplicaciones desconocidas** y habilita el permiso para tu navegador o gestor de archivos.
3. **Instalar y verificar:**
   - Abre el archivo `app-release.apk` descargado y presiona **Instalar**.
   - Una vez instalada, puedes verificar su versión (`versionName` / `versionCode`) desde **Ajustes → Aplicaciones → Southbound Field** o en la pantalla de **Perfil** dentro de la app.

---

## Ejecución de Tests

### Pruebas Unitarias (JVM)
```bash
.\gradlew.bat testDebugUnitTest --tests "com.example.proyectolince.ExampleUnitTest"
