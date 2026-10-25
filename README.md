# Southbound Field (Proyecto Lince - EP3)

**Southbound Field** es una aplicación móvil nativa en Android diseñada para la gestión, control operativo y trazabilidad en tiempo real del transporte y excursiones en la Zona Sur de Chile (Puerto Varas, Puerto Montt, Ensenada, etc.). La herramienta optimiza la coordinación diaria entre **Drivers (Conductores)**, **Guías** y **Coordinadores**, sustituyendo planillas físicas por un flujo digital fluido y reactivo.

---

## Integrantes del Equipo (SyntaxSouls)
- **Sección:** 003D
- **Benjamin Montoya** — Líder de equipo
- **Deiner Quero** — Desarrollador
- **Matias Jeldres** — Desarrollador
- **Victor Huenchucheo** — Desarrollador

---

## Resumen del Proyecto y Funcionalidades del MVP

### Problema Identificado
Las empresas de turismo y transporte de pasajeros enfrentan retrasos, mala comunicación en ruta e ineficiencias en los procesos de Check-in/Check-out debido a la recopilación manual de datos y falta de integración con datos meteorológicos y geográficos en terreno.

### Roles y Usuarios Principales
1. **Driver (Conductor):** Realiza check-in/check-out de pasajeros en transfers, consulta horarios y coordina traslados.
2. **Guía de Turismo:** Accede al detalle de excursiones, listas de pasajeros (`pax`), rutas e información meteorológica.
3. **Coordinador:** Supervisa asignaciones, confirma o reasigna servicios rechazados y gestiona la flota.

### Funcionalidades Clave Implementadas
- **Autenticación con Roles e Interfaz Anti-Teclado:** Pantalla de Login adaptable (`verticalScroll`, `imePadding` y `windowSoftInputMode="adjustResize"`) con selección rápida de rol (`Driver`, `Guía`, `Coordinador`).
- **Lista de Servicios Dinámica:** Tarjetas con estados operativos (`PENDIENTE`, `CONFIRMADO`, `RECHAZADO`), separación clara de acciones (*Ver detalles* para Excursiones / Tours vs. *Iniciar check-in de pasajeros* para Transfers) y filtrado rápido mediante `FilterChip`.
- **Integración API REST (Red):** Peticiones en tiempo real a la API de **OpenWeatherMap** mediante **Retrofit 2** para predecir el clima en los puntos de destino y ruta.
- **Persistencia Local con Room (Modo Offline):** Registro local de tiempos de `Check-in` y `Check-out`, observaciones y estados operativos, permitiendo funcionamiento continuo sin conexión a red.
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
   - Selector de rol en la cabecera.
   - Campos de correo y contraseña con alternancia de visibilidad.
   - Ajuste inteligente ante el despliegue del teclado virtual para evitar el solapamiento de campos.
2. **Servicios (`ServiciosScreen.kt`):**
   - Historial y tarjetas informativas por tipo de servicio.
   - Confirmación/Rechazo con diálogo emergente (`AlertDialog`) para ingresar el motivo de reasignación.
   - Botones de acción dinámicos según el tipo de servicio (*Excursión* o *Transfer*).
3. **Detalle del Servicio (`DetalleScreen.kt`):**
   - Mapeo dinámico del servicio seleccionado (`#SRV-001`, `#SRV-002`, etc.).
   - Visualizador de clima integrado consumido desde la API REST.
   - Timeline gráfico de Origen → Destino.
   - Registro persistido de Check-in y Check-out vinculado con la base de datos local (Room).
   - Botones directos para llamar al pasajero/despacho o abrir navegación GPS externa.

---

## Arquitectura y Estructura del Código

El proyecto sigue una arquitectura limpia basada en **MVVM (Model-View-ViewModel)** y **Unidirectional Data Flow (UDF)** recomendada por Google:

```text
com.example.proyectolince/
│
├── data/
│   ├── local/              # Base de datos local (Room DB, DAOs y Entidades)
│   └── network/            # Cliente HTTP (Retrofit, Interfaces y Modelos de API OpenWeather)
│
├── ui/
│   ├── screens/            # Pantallas en Jetpack Compose (LoginScreen, ServiciosScreen, DetalleScreen, etc.)
│   ├── theme/              # Definición de Colores, Tipografías y Temas Material 3
│   └── viewmodel/          # ViewModels (LoginViewModel, DetalleViewModel, etc.)
│
├── MainActivity.kt          # Punto de entrada y gestión del NavHost de navegación
└── BuildConfig             # Configuración de variables globales (API Keys locales)
