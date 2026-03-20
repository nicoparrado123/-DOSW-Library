# DOSW-Library

Sistema de gestión de biblioteca desarrollado con Spring Boot y Maven. Permite registrar usuarios, agregar libros con sus ejemplares disponibles, realizar préstamos y registrar devoluciones.

## Configuración del Proyecto

- **GroupId**: edu.eci.dosw
- **ArtifactId**: DOSW-Library
- **Java**: 17
- **Spring Boot**: 3.2.0

## Dependencias

- Spring Boot Starter Web
- Spring Boot Starter Test (JUnit 5)
- Springdoc OpenAPI (Swagger UI)
- JaCoCo
- SonarQube

---

## Diagrama General

Muestra cómo interactúan las capas principales del sistema. El cliente hace peticiones HTTP a los controladores, estos delegan la lógica a los servicios, y los servicios usan los modelos para representar los datos.

```mermaid
graph TD
    Cliente -->|HTTP Request| Controller
    Controller -->|usa| Service
    Service -->|valida con| Validator
    Service -->|opera sobre| Model
    Controller -->|transforma con| Mapper
    Mapper -->|convierte a/desde| DTO
```

---

## Diagrama Específico

Muestra el flujo completo de un préstamo: desde que el cliente llama al endpoint hasta que se registra el préstamo y se actualiza el stock de ejemplares.

```mermaid
sequenceDiagram
    participant Cliente
    participant LoanController
    participant LoanValidator
    participant UserService
    participant BookService
    participant LoanService

    Cliente->>LoanController: POST /prestamos/{idUsuario}/{idLibro}
    LoanController->>LoanService: prestar(idUsuario, idLibro)
    LoanService->>LoanValidator: validar(idUsuario, idLibro)
    LoanService->>UserService: buscarPorId(idUsuario)
    UserService-->>LoanService: User
    LoanService->>BookService: buscarPorId(idLibro)
    BookService-->>LoanService: Book
    LoanService->>BookService: obtenerEjemplares(idLibro)
    BookService-->>LoanService: cantidad
    LoanService->>BookService: actualizarEjemplares(idLibro, cantidad - 1)
    LoanService-->>LoanController: Loan
    LoanController-->>Cliente: LoanDTO (200 OK)
```

---

## Diagrama de Clases

Muestra las clases del sistema, sus atributos, métodos y relaciones entre ellas.

```mermaid
classDiagram
    class Book {
        -String id
        -String titulo
        -String autor
        +getId()
        +getTitulo()
        +getAutor()
    }

    class User {
        -String id
        -String nombre
        +getId()
        +getNombre()
    }

    class Loan {
        -Book libro
        -User usuario
        -LocalDate fechaPrestamo
        -LocalDate fechaDevolucion
        -LoanStatus estado
        +getEstado()
        +setEstado()
        +setFechaDevolucion()
    }

    class LoanStatus {
        <<enumeration>>
        ACTIVO
        DEVUELTO
    }

    class BookService {
        +agregarLibro(Book, int)
        +obtenerTodos()
        +buscarPorId(String)
        +obtenerEjemplares(String)
        +actualizarEjemplares(String, int)
    }

    class UserService {
        +registrar(User)
        +obtenerTodos()
        +buscarPorId(String)
    }

    class LoanService {
        +prestar(String, String)
        +devolver(String, String)
        +obtenerTodos()
        +obtenerPorUsuario(String)
    }

    class BookController {
        +obtenerTodos()
        +obtenerPorId(String)
        +agregar(BookDTO)
    }

    class UserController {
        +obtenerTodos()
        +obtenerPorId(String)
        +registrar(UserDTO)
    }

    class LoanController {
        +obtenerTodos()
        +prestar(String, String)
        +devolver(String, String)
    }

    class BookValidator {
        +validar(Book)
    }

    class UserValidator {
        +validar(String, String)
    }

    class LoanValidator {
        +validar(String, String)
    }

    Loan --> Book
    Loan --> User
    Loan --> LoanStatus
    BookService --> Book
    BookService --> BookValidator
    UserService --> User
    UserService --> UserValidator
    LoanService --> Loan
    LoanService --> BookService
    LoanService --> UserService
    LoanService --> LoanValidator
    BookController --> BookService
    UserController --> UserService
    LoanController --> LoanService
```

---

## Endpoints de la API

### Libros
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/libros` | Obtener todos los libros |
| GET | `/libros/{id}` | Buscar libro por ID |
| POST | `/libros` | Agregar un libro |

Body POST:
```json
{
  "id": "lib-001",
  "titulo": "Clean Code",
  "autor": "Martin",
  "copies": 3
}
```

### Usuarios
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/usuarios` | Obtener todos los usuarios |
| GET | `/usuarios/{id}` | Buscar usuario por ID |
| POST | `/usuarios` | Registrar un usuario |

Body POST:
```json
{
  "id": "usr-001",
  "nombre": "Nico"
}
```

### Préstamos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/prestamos` | Obtener todos los préstamos |
| POST | `/prestamos/{idUsuario}/{idLibro}` | Realizar un préstamo |
| PUT | `/prestamos/devolver/{idUsuario}/{idLibro}` | Devolver un libro |

---

## Documentación Swagger

Con la aplicación corriendo, acceder a:

```
http://localhost:8080/swagger-ui.html
```

---

## Ejecución de pruebas

```bash
mvn test
```

## Cobertura con JaCoCo

```bash
mvn test jacoco:report
```

El reporte se genera en `target/site/jacoco/index.html`.

---

## Análisis estático con SonarCloud

Resultado del análisis estático del proyecto en SonarCloud.

![cobertura sonarcloud](imagenes/cobertura%20nico.png)


## Evidencia de pruebas

### Pruebas unitarias
![prueba test libreria](imagenes/prueba%20test%20libreria.png)

### Cobertura JaCoCo
![jacoco test](imagenes/jacoco%20test.png)
![test jacoco](imagenes/test%20jacoco.png)

---

## Evidencia de pruebas funcionales

### Agregar libro
![agregar libros](imagenes/agregar%20libros.png)

### Agregar usuario
![agregar usuario](imagenes/agregar%20usuario%20ahora%20si.png)

### Obtener todos los libros
![obtener todos los libros](imagenes/obtener%20todos%20los%20libros.png)

### Prestar libro
![prestar libro](imagenes/prestar%20libro.png)

### Devolver libro
![devolvemos libro](imagenes/devolvemos%20libro.png)
