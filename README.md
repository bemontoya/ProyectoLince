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

## Documentación de APIs y Servicios Conectados

La aplicación opera mediante una arquitectura híbrida que integra los servicios operativos de **Southbound Travel** junto con nuestro microservicio propio en **Spring Boot** desplegado en Render (con sincronización idempotente, geocercas GPS y persistencia local):

- **Documentación Interactiva Backend (Swagger UI — Producción):** [Abrir Swagger UI — Proyecto Lince Backend](https://proyectolince-backend.onrender.com/swagger-ui/index.html#/)

---

## Resumen del Proyecto y Funcionalidades del MVP

### Problema Identificado
Las empresas de turismo y transporte de pasajeros enfrentan retrasos, mala comunicación en ruta e ineficiencias en los procesos de Check-in/Check-out debido a la recopilación manual de datos y falta de integración con datos meteorológicos y geográficos en terreno.

### Roles y Usuarios Principales
1. **Driver (Conductor):** Realiza check-in/check-out de pasajeros en transfers, consulta horarios y coordina traslados.
2. **Guía de Turismo:** Accede al detalle de excursiones, listas de pasajeros (`pax`), rutas e información meteorológica.
3. **Coordinador:** Supervisa asignaciones, confirma o reasigna servicios rechazados y gestiona la flota.

### Funcionalidades Clave Implementadas
- **Autenticación con Roles e Interfaz Anti-Teclado:** Pantalla de Login adaptable (`verticalScroll`, `imePadding` y `windowSoftInputMode="adjustResize"`) con selección rápida de rol (`Driver`, `Guía`, `Coordinador`) y selector de idioma (`ES` / `EN`).
- **Agenda y Lista de Servicios Dinámica:** Tarjetas con estados operativos (`PENDIENTE`, `CONFIRMADO`, `RECHAZADO`), filtros avanzados por fecha (**Hoy**, **Calendario**, fechas rápidas), tipo de servicio (`TRANSFER`, `GUIDED_SERVICE`, `EXCURSION`) y estado operativo.
- **Integración API REST (Red):** 
  - Consumo del catálogo y detalle de servicios en tiempo real desde la **API de Southbound (`ws.southbound.travel`)**.
  - Registro de confirmaciones, geocercas y eventos de `Check-in` / `Check-out` con **idempotencia** en el microservicio propio (**Spring Boot / Render**).
  - Predicción meteorológica en tiempo real mediante **OpenWeatherMap** y **Retrofit 2**.
- **Arquitectura Offline-First (Room + Caché Local + WorkManager):** Respaldo local de catálogos y detalles (`OfflineLinceCache`), persistencia de registros operacionales en **Room** y sincronización automática en segundo plano mediante **WorkManager** al recuperar conectividad.
- **Validación GPS y Geocercas (150 m):** Verificación de proximidad mediante fórmula de Haversine, precisión GPS y ventana de anticipación antes del pickup.
- **Integración con Recursos Nativos del Dispositivo:** 
  - **Llamadas telefónicas:** Activación directa del marcador telefónico (`Intent.ACTION_DIAL`) para contactar al titular del servicio o Despacho central.
  - **Navegación GPS Externa:** Apertura directa de Waze / Google Maps (`Intent.ACTION_VIEW` con esquema `geo:`) hacia la ubicación de origen/destino.

---

## Identidad Visual y UI

- **Nombre de la App:** Southbound Field
- **Logotipo:** Configurado desde `R.drawable.logo_lince` y como icono nativo de la aplicación.
- **Paleta de Colores (Material Design 3):**
  - **Azul Principal (`AzulPrincipal`):** Color distintivo en TopAppBar, botones primarios y tabs seleccionados.
  - **Verde Confirmado (`VerdeConfirmado` / `VerdeFondoBadge`):** Indicadores de estados confirmados, llamadas y check-in.
  - **Gris & Fondo (`FondoPantalla` / `TextoGris` / `TextoOscuro`):** Contraste de alta legibilidad para interfaces en terreno bajo la luz del sol.

---

## Pantallas Principales

1. **Login Screen (`LoginScreen.kt`):**
   - Selector de rol en la cabecera y conmutador de idioma (`ES` | `EN`).
   - Campos de correo y contraseña con alternancia de visibilidad.
   - Precarga en segundo plano del servidor backend y ajuste inteligente ante el despliegue del teclado virtual.
2. **Servicios (`ServiciosScreen.kt`):**
   - Agenda filtrable por fecha, tipo de servicio y estado, con indicador visual en modo sin conexión.
   - Confirmación/Rechazo con diálogo emergente (`AlertDialog`) para ingresar el motivo de reasignación.
   - Botones de acción dinámicos según el rol (*Driver*, *Guía* o *Coordinador*).
3. **Detalle del Servicio (`DetalleScreen.kt`):**
   - Mapeo dinámico del servicio seleccionado desde la API de Southbound.
   - Visualizador de clima integrado consumido desde la API REST.
   - Validación de geocerca GPS (150 m), timeline gráfico de Origen → Destino y registro persistido de Check-in / Check-out en Room + WorkManager.
   - Botones directos para llamar al pasajero/despacho o abrir navegación GPS externa.
4. **Disponibilidad (`DisponibilidadScreen.kt`):**
   - Gestión de bloqueos de agenda con validación de anticipación mínima y control de conflictos con servicios asignados.

---

## Ejecución de Tests

### 1. Pruebas Unitarias (JVM)
```bash
.\gradlew.bat testDebugUnitTest --tests "com.example.proyectolince.ExampleUnitTest"
