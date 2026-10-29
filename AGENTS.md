# AGENTS.md — Instrucciones para asistentes de IA

> Este archivo recoge las reglas que se dan a las herramientas de IA generativa usadas en el proyecto, como pide el enunciado (apartado de Entregables). Los prompts empleados se registran en `planning.md`.

## Equipo

- Francisco José Vela Ponce
- Alejandro López García

## Proyecto

**Tracker de Candidaturas Laborales**: aplicación Java para hacer seguimiento de candidaturas a ofertas de empleo (ejercicio integrador del 1er trimestre, Acceso a Datos + Desarrollo de Interfaces).

Entrega actual: **v1.0 (Archivos + CLI + Diseño)**. Solo se hace hasta la v1.0: **nada de base de datos, JavaFX ni Hibernate**.

## Reglas obligatorias de la v1.0

1. Java 24 o superior, gestionado con Maven.
2. Interfaz **solo por línea de comandos**: el programa recibe la operación y sus parámetros en `main(String[] args)`.
3. **Prohibido** usar `Scanner`, `System.in` o menús interactivos.
4. Cada ejecución realiza **una operación y termina**.
5. Persistencia en **un archivo de texto plano** (`candidaturas.csv`). Sin base de datos.
6. Los errores se muestran con un **mensaje claro por consola**. El programa nunca termina con un stack trace.

## Operaciones (comandos)

| Comando | Qué hace |
|---|---|
| `-listar` | Lista todas las candidaturas |
| `-listar --estado X --empresa Y` | Filtra por estado y/o empresa (texto) |
| `-añadir --titulo X --empresa Y --estado Z --fecha dd/MM/yyyy` | Añade una candidatura al archivo |
| `-ver 13` | Muestra todos los detalles de la candidatura con ID 13 |
| `-borrar 13` | Elimina la candidatura con ID 13 |
| `-importar candidaturas.csv` | Importa candidaturas desde otro archivo CSV |

## Formato de datos

```
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
1;Desarrollador Java Junior;Indra;pendiente;02/08/2026;https://...;Enviado por InfoJobs
```

- Primera línea: cabecera.
- Fechas `dd/MM/yyyy` con `LocalDate` y `DateTimeFormatter`.
- Estados: `pendiente`, `entrevista`, `rechazado`, `aceptado` (enum `Estado`).
- `enlace` y `notas` son opcionales.
- Codificación UTF-8.

## Estructura de paquetes

```
src/main/java/es/tracker/
  modelo/        Oferta, Estado
  repositorio/   RepositorioOfertas (interfaz) y RepositorioOfertasArchivo
  servicio/      OfertaService
  cli/           Cli
  Main.java
```

El `Main` solo arranca; los argumentos y la salida van en `cli`; las reglas en `servicio`; el archivo solo en `repositorio`.

## Calidad del código

- Código limpio en paquetes, sin código muerto ni comentarios de depuración.
- Nombres en **español**.
- **Javadoc** en clases y métodos públicos, escrito de forma sencilla. **Ningún otro comentario.**
- Sin código duplicado.
- `try-with-resources` y `java.nio.file` con `StandardCharsets.UTF_8`.
- Capturar las excepciones donde se pueda dar un mensaje útil; no tragárselas.

## Pruebas

- JUnit 5. El servicio se prueba con un repositorio en memoria hecho dentro del test.
- Cubrir la lógica del servicio (filtrar, añadir, ver, borrar, importar, validaciones).
- Repositorio con archivos temporales (`@TempDir`), nunca con el archivo real.

## Control de versiones

- Un único repositorio en GitHub; los dos miembros son colaboradores.
- Commits pequeños, uno por cada parte terminada, con mensajes descriptivos en español.
- La hora de los commits es la real. No se reescribe el historial.
- La entrega se etiqueta en `main` como `v1.0`.

## Cómo debe trabajar la IA en este repositorio

- Seguir estas reglas por encima de cualquier otra preferencia de estilo.
- **Explicar y preguntar antes de hacer cualquier cambio.** Si algo no está claro o hay que decidir, preguntar en lugar de inventar. Las decisiones se anotan en `planning.md`.
- Explicar qué cambia y por qué, para que el equipo lo revise y lo entienda.
- Cada miembro debe revisar y comprender el código que se incorpora.
- Registrar los prompts usados en `planning.md`.
