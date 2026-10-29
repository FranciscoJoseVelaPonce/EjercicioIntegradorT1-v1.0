# Tracker de Candidaturas Laborales

Aplicación Java para hacer seguimiento de las candidaturas a ofertas de empleo o prácticas: a qué empresa se ha enviado el CV, en qué estado está cada proceso y cuándo se envió.

Ejercicio integrador del 1er trimestre (Acceso a Datos + Desarrollo de Interfaces), realizado en pareja por:

- Francisco José Vela Ponce
- Alejandro López García

## Qué incluye la versión 1.0

La v1.0 es la primera de las tres entregas del proyecto (archivos + CLI + diseño).

**Acceso a Datos.** Programa de línea de comandos que guarda las candidaturas en un archivo de texto plano (`candidaturas.csv`, campos separados por `;`). Cada ejecución realiza una operación y termina, sin menús interactivos:

- listar todas las candidaturas, con filtros por estado y por empresa
- añadir una candidatura
- ver los detalles de una candidatura
- borrar una candidatura
- importar candidaturas desde otro archivo CSV

**Desarrollo de Interfaces.** Prototipo interactivo en Figma con las pantallas de la futura aplicación y un manual de estilo (paleta, tipografía, logo y guía de estilo).

## Tecnologías

- Java 25
- Maven
- JUnit 5 para las pruebas
- Figma para el prototipo y el manual de estilo

## Datos de prueba

[`candidaturas.csv`](candidaturas.csv) contiene 15 candidaturas de ejemplo generadas con IA (5 empresas, con candidaturas en los cuatro estados: `pendiente`, `entrevista`, `rechazado` y `aceptado`). Las fechas van de agosto a octubre de 2026.

Formato de cada línea:

```
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
```

## Cómo compilar y ejecutar

Hace falta Java 25 o superior y Maven. En la carpeta del proyecto:

```bash
mvn package
```

Compila, pasa los tests y crea el archivo `target/tracker-candidaturas.jar`. Después se ejecuta con una operación:

```bash
java -jar target/tracker-candidaturas.jar -listar
```

El programa usa el archivo `candidaturas.csv` de la carpeta desde la que se ejecuta. Si no existe, se crea al añadir la primera candidatura.

En IntelliJ también se puede ejecutar la clase `es.tracker.Main` poniendo la operación en *Program arguments*.

## Uso

Cada ejecución hace una sola operación y termina. Si va bien, el programa termina con código 0, y si hay algún error, con código 1.

### Listar

```bash
java -jar target/tracker-candidaturas.jar -listar
java -jar target/tracker-candidaturas.jar -listar --estado entrevista
java -jar target/tracker-candidaturas.jar -listar --empresa indra
java -jar target/tracker-candidaturas.jar -listar --estado pendiente --empresa accenture
```

La empresa se busca por una parte del nombre y sin distinguir mayúsculas. Ejemplo de salida:

```
ID   TÍTULO                         EMPRESA              ESTADO      FECHA
2    Programador Backend            Accenture            entrevista  05/08/2026
5    Prácticas DAM                  Indra                entrevista  20/08/2026
7    Desarrollador Android          Telefónica           entrevista  01/09/2026
14   Programador Junior             Capgemini            entrevista  15/10/2026
4 candidatura(s).
```

### Añadir

```bash
java -jar target/tracker-candidaturas.jar -añadir --titulo "Desarrollador Python" --empresa Sopra --estado pendiente
java -jar target/tracker-candidaturas.jar -añadir --titulo "Prácticas DAM" --empresa Indra --estado pendiente --fecha 15/09/2026 --enlace https://www.infojobs.net/oferta/123 --notas "Enviado por InfoJobs"
```

`--titulo`, `--empresa` y `--estado` son obligatorios. Los estados permitidos son `pendiente`, `entrevista`, `rechazado` y `aceptado`. Si no se pone `--fecha` (formato `dd/MM/yyyy`), se usa la de hoy. El ID se asigna solo.

```
Candidatura añadida con ID 16: Desarrollador Python en Sopra
```

### Ver

```bash
java -jar target/tracker-candidaturas.jar -ver 4
```

```
ID:      4
Título:  Desarrollador Full Stack
Empresa: NTT Data
Estado:  aceptado
Fecha:   14/08/2026
Enlace:  https://www.nttdata.com/es/empleo/full-stack
Notas:   Empiezo en septiembre
```

### Borrar

```bash
java -jar target/tracker-candidaturas.jar -borrar 16
```

```
Candidatura borrada: Desarrollador Python
```

### Importar

```bash
java -jar target/tracker-candidaturas.jar -importar otras-candidaturas.csv
```

Añade al final las candidaturas de otro CSV con el mismo formato y les da IDs nuevos. Las líneas que no son válidas se saltan y se indica su número.

### Errores

Si algo falla, el programa muestra un mensaje que empieza por `Error:` y termina con código 1. Algunos ejemplos:

| Caso | Mensaje |
|---|---|
| Sin operación | `Falta la operación.` y la ayuda de uso |
| Falta un parámetro obligatorio | `Error: Falta el parámetro --estado` |
| Estado que no existe | `Error: Estado no válido: "enviado". Estados permitidos: pendiente, entrevista, rechazado, aceptado` |
| ID que no existe | `Error: No existe ninguna candidatura con el ID 99.` |
| ID que no es un número | `Error: El ID debe ser un número entero: abc` |
| Archivo a importar que no existe | `Error: no se encuentra el archivo noexiste.csv` |

## Prototipo

Prototipo interactivo con 7 pantallas navegables: candidaturas, detalle, formulario, empresas, entrevistas, estadísticas y asistente IA.

- [Prototipo en Figma (solo lectura)](https://www.figma.com/design/37Zy9om98P5F0cNnEkegDO/Tracker-de-Candidaturas-%E2%80%94-Prototipo-v1.0)
- [PDF de las pantallas](doc/pantallas.pdf)

## Diseño

El [manual de estilo](doc/Hoja%20del%20manual%20de%20estilo.pdf) recoge la identidad visual de la aplicación: logo, paleta, tipografía, guía de botones, tablas, formularios, tarjetas, navegación, iconografía, componentes y accesibilidad. Todas las pantallas del prototipo usan esos colores, estilos de texto y componentes.

### Paleta de colores

| Uso | Color |
|---|---|
| Primario | `#2B4ACB` |
| Primario claro | `#E6EAFA` |
| Secundario | `#0E7C86` |
| Secundario claro | `#DDF2F3` |
| Texto | `#1B1F2A` |
| Texto suave | `#4A5162` |
| Fondo | `#F5F6FA` |
| Superficie | `#FFFFFF` |
| Borde | `#D5D9E2` |

Cada estado de una candidatura tiene su color de texto y su color de fondo:

| Estado | Texto | Fondo |
|---|---|---|
| Pendiente | `#8A5A00` | `#FFF1CC` |
| Entrevista | `#1D4ED8` | `#E0E8FB` |
| Rechazado | `#B42318` | `#FDE7E5` |
| Aceptado | `#1F7A45` | `#E3F4EA` |

### Tipografía

| Estilo | Fuente |
|---|---|
| Títulos | Lexend (Bold 30 y 22, SemiBold 16) |
| Texto | Inter (Regular 14 y 12, SemiBold 14) |
| IDs y fechas | JetBrains Mono (Regular 13) |

## Contribuciones

| Miembro | Aportaciones |
|---|---|
| Francisco José Vela Ponce | Proyecto Maven, `enum Estado`, `record Oferta`, interfaz `RepositorioOfertas`, lectura y escritura del CSV (`RepositorioOfertasArchivo`) y sus tests. README: descripción, tecnologías y datos de prueba. PDF de las pantallas y enlace al prototipo de Figma. Tabla de contribuciones y checklist |
| Alejandro López García | `OfertaService` y sus tests, `Cli`, `Main`, `candidaturas.csv`, `AGENTS.md` y `planning.md`. README: compilación, ejecución y uso de cada comando. Manual de estilo en PDF y apartado de diseño. Organización de los PDF en `doc` |
| Los dos | Prototipo de Figma y manual de estilo, revisión del código y vídeo de demostración |

Hemos usado IA (Claude) como ayuda durante el proyecto. Las reglas que le dimos están en [`AGENTS.md`](AGENTS.md) y los prompts, en el registro de [`planning.md`](planning.md).
