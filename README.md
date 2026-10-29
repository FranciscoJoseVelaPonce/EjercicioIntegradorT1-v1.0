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
