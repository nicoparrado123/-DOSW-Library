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


## Comandos Maven

- Compilar: `mvn clean compile`
- Ejecutar tests: `mvn test`
- Generar reporte de cobertura: `mvn jacoco:report`
- Análisis SonarQube: `mvn sonar:sonar`
- Ejecutar aplicación: `mvn spring-boot:run`
