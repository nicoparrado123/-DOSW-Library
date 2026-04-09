# High Level Design - DOSW Library System

## 1. Introducción

DOSW Library es un sistema de gestión de biblioteca desarrollado con Spring Boot. Permite registrar usuarios, agregar libros con inventario, realizar préstamos y registrar devoluciones. El sistema expone una API REST con autenticación basada en JWT y soporte para dos motores de persistencia: PostgreSQL y MongoDB, seleccionables mediante perfiles de Spring.

---

## 2. Objetivos de Arquitectura

- Separar la lógica de negocio de la capa de persistencia mediante interfaces
- Soportar múltiples bases de datos sin modificar el núcleo del sistema
- Proteger los endpoints mediante autenticación JWT y autorización por roles
- Automatizar el despliegue mediante un pipeline CI/CD con GitHub Actions y Azure

---

## 3. Arquitectura General

El sistema sigue una arquitectura por capas donde cada capa tiene una responsabilidad clara:

- **Capa de seguridad**: valida el token JWT en cada request antes de llegar al controlador
- **Capa de controladores**: recibe las peticiones HTTP y delega la lógica al servicio correspondiente
- **Capa de servicios**: contiene toda la lógica de negocio (validaciones, reglas de préstamo, inventario)
- **Capa de repositorios**: abstrae el acceso a datos mediante interfaces, con implementaciones separadas para JPA y MongoDB
- **Capa de persistencia**: según el perfil activo, Spring inyecta la implementación JPA (PostgreSQL) o MongoDB

---

## 4. Componentes Principales

### Seguridad
- **JwtService**: genera y valida tokens JWT firmados
- **JwtAuthFilter**: intercepta cada request y carga el contexto de seguridad
- **SecurityConfig**: define qué endpoints requieren autenticación y qué rol necesitan

### Controladores
- **AuthController**: maneja el login y retorna el token JWT
- **BookController**: gestión de libros
- **UserController**: gestión de usuarios
- **LoanController**: gestión de préstamos y devoluciones

### Servicios
- **BookService**: maneja el inventario y validaciones de stock
- **UserService**: registra usuarios con contraseña hasheada
- **LoanService**: valida disponibilidad, límite de préstamos activos y actualiza el stock

### Repositorios
Cada entidad tiene una interfaz (port) y dos implementaciones (adapters):

| Interfaz | Implementación JPA | Implementación MongoDB |
|----------|-------------------|----------------------|
| BookRepositoryPort | BookRepositoryJpaImpl | BookRepositoryMongoImpl |
| UserRepositoryPort | UserRepositoryJpaImpl | UserRepositoryMongoImpl |
| LoanRepositoryPort | LoanRepositoryJpaImpl | LoanRepositoryMongoImpl |

---

## 5. Modelo de Datos

### Relacional (PostgreSQL)

| Tabla | Descripción |
|-------|-------------|
| users | Datos del usuario: id, nombre, username, password, role |
| books | Datos del libro: id, titulo, autor, stock_total, stock_disponible |
| loans | Préstamo: id, user_id, book_id, fecha_prestamo, fecha_devolucion, estado |

### No Relacional (MongoDB)

| Colección | Descripción |
|-----------|-------------|
| users | Documento con todos los datos del usuario embebidos |
| books | Documento con disponibilidad embebida (copias totales y disponibles) |
| loans | Documento con historial de estados embebido y referencias a user y book por id |

---

## 6. Seguridad

El sistema implementa tres conceptos fundamentales:

- **Autenticación**: el usuario envía sus credenciales al endpoint `/auth/login` y recibe un token JWT firmado
- **Autorización**: cada endpoint verifica el rol del usuario antes de ejecutar la operación
- **Integridad**: el token JWT está firmado digitalmente, lo que garantiza que no fue alterado

### Reglas de acceso

| Endpoint | USER | LIBRARIAN |
|----------|------|-----------|
| GET /libros | Si | Si |
| POST /libros | No | Si |
| GET /usuarios | No | Si |
| POST /usuarios | No | Si |
| POST /prestamos | Si | Si |
| GET /prestamos | No | Si |
| GET /prestamos/usuario/{id} | Si (solo el propio) | Si |

---

## 7. Infraestructura y Despliegue

La aplicación se despliega automáticamente en Azure App Service cada vez que se hace un pull request a la rama main, siguiendo este pipeline:

| Job | Descripcion | Depende de |
|-----|-------------|------------|
| build | Compila el proyecto | - |
| test | Ejecuta todos los tests con base de datos en memoria (H2) | build |
| analysis | Analisis estatico con SonarCloud | test |
| deploy | Despliega el JAR en Azure App Service | test |

La base de datos en produccion es MongoDB Atlas, configurada mediante variables de entorno en Azure App Service.

---

## 8. Tecnologias Utilizadas

| Categoria | Tecnologia |
|-----------|-----------|
| Framework | Spring Boot 3.2 |
| Seguridad | Spring Security + JWT |
| Persistencia relacional | Spring Data JPA + PostgreSQL |
| Persistencia no relacional | Spring Data MongoDB |
| Documentacion API | Swagger UI (SpringDoc) |
| Testing | JUnit 5 + Mockito |
| Cobertura | JaCoCo |
| Analisis estatico | SonarCloud |
| CI/CD | GitHub Actions |
| Despliegue | Azure App Service |
| Base de datos produccion | MongoDB Atlas |

---

## 9. URL de Produccion

Aplicacion desplegada en Azure:
`https://dosw-library-nico-c8chc3fydnfzdtcm.brazilsouth-01.azurewebsites.net`

Swagger UI:
`https://dosw-library-nico-c8chc3fydnfzdtcm.brazilsouth-01.azurewebsites.net/swagger-ui/index.html`
