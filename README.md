# DOSW-Library

Sistema de gestión de biblioteca desarrollado con Spring Boot y Maven.

## Configuración del Proyecto

- **GroupId**: edu.eci.dosw
- **ArtifactId**: DOSW-Library
- **version java**: 17
- **version spring root**: 3.2.0

## Dependencias

- Spring Boot Starter Web
- Spring Boot Starter Test (incluye JUnit 5)
- JaCoCo (cobertura de pruebas)
- SonarQube (análisis de calidad de código)

---

## Ejecución de las funcionalidades de la API

Se registra un usuario en el sistema correctamente.

![agregar usuario](imagenes/agregar%20usuario%20ahora%20si.png)

Se agrega un libro con sus ejemplares disponibles.

![agregar libros](imagenes/agregar%20libros.png)

Se consultan todos los libros registrados en la biblioteca.

![obtener todos los libros](imagenes/obtener%20todos%20los%20libros.png)

Se realiza el préstamo de un libro a un usuario.

![prestar libro](imagenes/prestar%20libro.png)

Se devuelve un libro prestado y se actualiza el estado del préstamo.

![devolver libro](imagenes/devolvemos%20libro.png)

---

## Ejecución de las pruebas de los servicios

Se ejecutan todas las pruebas de los servicios y se verifica que pasen correctamente.

![pruebas de la libreria](imagenes/prueba%20test%20libreria.png)

---

## Cobertura de pruebas con JaCoCo

Reporte de cobertura generado por JaCoCo mostrando el porcentaje de código cubierto por las pruebas.

![jacoco test](imagenes/jacoco%20test.png)

Vista detallada del reporte de JaCoCo con los resultados por clase.

![test jacoco](imagenes/test%20jacoco.png)

---

## Análisis estático con SonarCloud

Resultado del análisis estático del proyecto en SonarCloud mostrando la calidad del código.

![cobertura sonarcloud](imagenes/cobertura%20nico.png)
