# Southbound FieldLog (Proyecto Lince)

**Southbound FieldLog** es una solución móvil diseñada para digitalizar la captura de datos y el seguimiento de tareas en terreno, eliminando el uso de formularios en papel, reduciendo reprocesos y mejorando la trazabilidad operativa en tiempo real.

---

##  Integrantes del Equipo (SyntaxSouls N°18)
* **Sección:** 003D
* **Benjamin Montoya** — Líder de equipo
* **Deiner Quero** — Desarrollador
* **Matias Jeldres** — Desarrollador
* **Victor Huenchucheo** — Desarrollador

---

##  Resumen de la Clase 1: Definición del MVP y Requerimientos

###  Problema Identificado
El flujo operativo actual presenta demoras, respuestas tardías y falta de trazabilidad por el uso de captura manual de datos en papel o canales informales.

###  Usuarios Principales
1. **Operador de Terreno:** Captura datos, adjunta evidencias fotográficas y registra avances.
2. **Supervisor de Operaciones:** Monitorea el progreso, asigna tareas y aprueba/rechaza solicitudes.
3. **Cliente / Solicitante:** Consulta el estado de sus requerimientos y recibe actualizaciones.

### Alcance del MVP
* **Incluido:** Autenticación de usuarios, captura de datos en terreno con fotos, dashboard y listado con actualización de estados en tiempo real.
* **Fuera de alcance:** Generación de reportes PDF/Excel automatizados, notificaciones push automáticas y módulos de pagos/facturación.

###  Requerimientos No Funcionales Clave
* **Modo Offline:** Registro local con sincronización automática al recuperar conectividad.
* **Usabilidad y Tiempo de Respuesta:** Interfaz intuitiva (completar registro en < 3 pasos) y carga de datos en menos de 2 segundos.

---

##  Resumen de la Clase 2: Identidad Visual, UI y Flujo UML

###  Identidad Visual
* **Nombre de la App:** SouthBound Logfield
* **Logotipo:** Ubicado en `docs/diseno/logo.png`. Representa una brújula del sur combinada con un check de trazabilidad y pin de ubicación.
* **Paleta de Colores (Material Design 3):**
    * **Principal (`#003366`):** Azul Marino (TopAppBar, botones primarios).
    * **Secundario (`#2E7D32`):** Verde (Badges de "Completado", FAB).
    * **Adicional (`#ED6C02`):** Naranja (Estados "En Proceso" / "Pendiente").
    * **Fondo (`#F8F9FA`):** Gris Claro (Fondo general).
    * **Texto (`#1C1B1F`):** Charcoal (Contraste para alta usabilidad).

###  Flujo de Navegación y UML
El flujo operativo abarca desde el inicio de sesión del usuario, visualización en el Dashboard, creación/actualización de tareas en modo offline/online, hasta la verificación de sincronización en el Perfil.
* **Diagrama UML:** Ubicado en `docs/diseno/flujo-usuario-uml.png`.

###  Pantallas Principales del MVP
1. **Login (`screen_login.png`):** Autenticación y control de roles.
2. **Dashboard Principal (`screen_dashboard.png`):** Resumen de métricas y acceso rápido.
3. **Listado de Registros (`screen_listado.png`):** Historial con filtros (`FilterChip`).
4. **Formulario de Registro (`screen_formulario.png`):** Captura de datos e imágenes en terreno.
5. **Detalle de Registro (`screen_detalle.png`):** Información técnica y cambio de estado.
6. **Perfil y Configuración (`screen_perfil.png`):** Estado de red/sincronización y cierre de sesión.



##  Flujo de Usuario (Diagrama de Actividad UML)

El flujo operativo contempla desde la autenticación del usuario hasta el registro local/sincronizado y la actualización de estados en tiempo real.
![Diagrama de Actividad del Flujo de Usuario](./Diseño/flujo-usuario-uml.png)

---

##  Estructura del Repositorio
* **Evidencias:** `docs/evidencias/clase-01/` y `docs/evidencias/clase-02/`
* **Recursos de Diseño:** `docs/diseno/` (Logotipo, diagramas UML y capturas de pantallas)
* **Tecnologías:** Android Studio / Jetpack Compose / Material Design 3