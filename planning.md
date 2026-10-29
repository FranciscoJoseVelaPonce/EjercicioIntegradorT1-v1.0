# planning.md — Planificación de la v1.0

**Proyecto:** Tracker de Candidaturas Laborales
**Equipo:** Francisco José Vela Ponce y Alejandro López García
**Entrega:** v1.0 (Archivos + CLI + Diseño)

## 1. Decisiones de diseño

- [x] **Fecha en `-añadir`:** `--fecha` es opcional; si no se indica, se usa la fecha de hoy.
- [x] **Campos opcionales en `-añadir`:** se admiten también `--enlace` y `--notas`.
- [x] **Identificadores:** el nuevo ID es el mayor existente + 1.
- [x] **Importación:** las candidaturas importadas se añaden al final con IDs nuevos. Las líneas no válidas se omiten y se informa de su número de línea.
- [x] **Filtros de `-listar`:** sin distinguir mayúsculas/minúsculas; `--empresa` busca el texto dentro del nombre; si se dan `--estado` y `--empresa`, deben cumplirse ambos.
- [x] **Punto y coma en los textos:** se sustituye por `,` al guardar.
- [x] **Argumentos:** se procesan a mano, sin librerías.
- [x] **Archivo de datos estropeado:** si `candidaturas.csv` tiene líneas mal, el programa avisa y no guarda nada, para no perder datos.
- [x] **Letra `ñ` en `-añadir`:** funciona en Windows; se acepta también `-anadir`.
- [x] **Comentarios:** solo Javadoc en clases y métodos públicos.
- [x] **Flujo de Git:** commits directos a `main`, cada uno desde su ordenador y con su cuenta.

## 2. Plan de trabajo

### 2.1 Código (Acceso a Datos)

- [x] Proyecto Maven (Java 25, JUnit 5)
- [x] Clase `Oferta` y `enum Estado`
- [x] Repositorio sobre archivo: leer todo, guardar todo, leer otro CSV
- [x] Servicio: listar con filtros
- [x] Servicio: añadir con validaciones (título y empresa obligatorios, estado válido, fecha válida)
- [x] Servicio: ver por ID
- [x] Servicio: borrar por ID
- [x] Servicio: importar desde otro CSV
- [x] Capa `cli`: interpretar los argumentos y mostrar resultados y errores
- [x] Mensajes de error claros
- [x] Pruebas unitarias del servicio (10)
- [x] Pruebas del repositorio con `@TempDir` (5)
- [x] Javadoc en clases y métodos públicos
- [ ] Revisión final entre los dos

### 2.2 Diseño (Desarrollo de Interfaces)

- [x] Prototipo interactivo en Figma con navegación entre pantallas:
  - [x] Listado de candidaturas con filtros
  - [x] Detalle de una candidatura (empresa, entrevistas, notas)
  - [x] Formulario de alta/edición de candidatura
  - [x] Gestión de empresas (listado + formulario)
  - [x] Gestión de entrevistas
  - [x] Panel de estadísticas (v3.0)
  - [x] Asistente IA (v3.0)
- [x] Manual de estilo: paleta, tipografía, logo, guía de estilo y componentes
- [x] Revisar contraste y accesibilidad
- [x] PDF de las pantallas y PDF del manual

### 2.3 Datos de prueba

- [x] CSV generado con IA con el prompt del apartado 4
- [x] Comprobado: 15 registros, 5 empresas, 4 pendientes, 4 entrevistas, 4 rechazadas y 3 aceptadas, fechas del 02/08/2026 al 21/10/2026
- [x] Guardado como `candidaturas.csv` y enlazado desde el README

## 3. Reparto de tareas

Cada miembro debe tener al menos 5 commits con aportaciones sustanciales.

| Tarea | Francisco José | Alejandro |
|---|---|---|
| Modelo + repositorio + tests | ✔ | |
| Servicio + CLI + tests | | ✔ |
| Pantallas de Figma | ✔ | ✔ |
| Manual de estilo | ✔ | ✔ |
| README y CSV de prueba | ✔ | ✔ |
| Vídeo de demostración | ✔ | ✔ |

## 4. Registro de prompts

| Fecha | Quién | Herramienta | Prompt | Resultado / qué se hizo con él |
|---|---|---|---|---|
| 28/10/2026 | Francisco José | Claude (Claude Code) | "Cámbialo todo por el tracker de candidaturas" (a partir del enunciado completo) | Plan de la v1.0. Se decidió proyecto nuevo aparte, Figma nuevo y Javadoc sencillo |
| 28/10/2026 | Francisco José | Claude (Claude Code) | Implementar la v1.0 por capas (modelo, repositorio, servicio, cli) con los comandos del enunciado | Código y tests. Revisados y probados con `mvn package` y ejecutando cada comando |
| 28/10/2026 | Francisco José | Claude (Claude Code) | Genera un archivo CSV con 15 registros de candidaturas laborales para una aplicación de seguimiento de empleo. El CSV debe tener las siguientes columnas separadas por punto y coma: id;titulo;empresa;estado;fechaPostulacion;enlace;notas. Los estados posibles son: pendiente, entrevista, rechazado, aceptado. Incluye al menos 4 empresas diferentes y al menos 3 ofertas en cada estado. Las fechas deben estar en formato dd/MM/yyyy y abarcar los últimos 3 meses. Devuelve solo el CSV, sin explicaciones. | `candidaturas.csv`. Se comprobó que cumple las condiciones |
| 28/10/2026 | Francisco José | Claude (Claude Code) | Prototipo en Figma con las pantallas del enunciado y manual de estilo | Archivo de Figma del Tracker |
| 29/10/2026 | Francisco José | Claude (Claude Code) | Guion de los commits de la v1.0, con el reparto entre los dos y el título y la descripción de cada uno | `COMMITS-v1.0.md`. Cada uno ha hecho sus commits desde su cuenta |
| 29/10/2026 | Francisco José | Claude (Claude Code) | Prepara el README: descripción, tecnologías, datos de prueba, cómo compilar y ejecutar con un ejemplo de cada comando y los errores | Apartados del README. Las salidas de ejemplo se comprobaron ejecutando cada comando |
| 29/10/2026 | Francisco José | Claude (Claude Code) | Apartado de diseño del README con la paleta y las tipografías del Figma | Apartado «Diseño», con los colores y las fuentes sacados de las variables y los estilos del Figma |
| 29/10/2026 | Francisco José | Claude (Claude Code) | Tabla de contribuciones y checklist de la entrega | Apartado «Contribuciones» del README y checklist de este archivo, revisados con el historial de commits |

## 5. Checklist de entrega v1.0

- [x] Repositorio **público** en GitHub, con los dos como colaboradores
- [ ] Tag `v1.0` en `main`
- [x] PDF con las pantallas del prototipo
- [x] PDF del manual de estilo
- [ ] Vídeo de 5 a 10 minutos con los dos (prototipo navegable + comandos)
- [x] README con descripción, tecnologías, instrucciones, tabla de contribuciones, enlace al CSV y enlace a Figma de solo lectura
- [x] `AGENTS.md` y `planning.md` en el repositorio
- [x] Nombres de los dos en todos los documentos
- [x] Al menos 5 commits de cada miembro
