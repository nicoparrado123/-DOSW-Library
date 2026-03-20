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

El sistema está organizado en capas. El cliente envía peticiones HTTP que llegan a los controladores. Cada controlador delega la lógica de negocio al servicio correspondiente. Los servicios validan los datos de entrada usando los validadores antes de operar sobre los modelos. Los controladores usan los mappers para convertir entre los modelos internos y los DTOs que se exponen al cliente.

```mermaid
graph TD
    Cliente -->|HTTP Request| Controller
    Controller -->|delega lógica| Service
    Service -->|valida entrada con| Validator
    Service -->|opera sobre| Model
    Controller -->|convierte con| Mapper
    Mapper -->|transforma a/desde| DTO
```

---

## Diagrama Específico

Este diagrama muestra paso a paso el flujo de un préstamo. El cliente hace una petición POST al `LoanController`, que llama a `LoanService`. El servicio primero valida los IDs con `LoanValidator`, luego busca el usuario en `UserService` y el libro en `BookService`. Verifica que haya ejemplares disponibles y que el usuario no supere el límite de 3 préstamos activos. Si todo es válido, descuenta un ejemplar y registra el préstamo, devolviendo un `LoanDTO` al cliente.

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

Muestra todas las clases del sistema con sus atributos, métodos y relaciones. `Loan` es la clase central ya que asocia un `Book` con un `User` y lleva el estado del préstamo (`LoanStatus`). Los servicios gestionan las colecciones de cada entidad y usan sus respectivos validadores para verificar los datos antes de operar. Los controladores reciben las peticiones HTTP y delegan al servicio correspondiente.

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

    Loan "1" --> "1" Book : tiene
    Loan "1" --> "1" User : pertenece a
    Loan "1" --> "1" LoanStatus : tiene estado
    BookService "1" --> "0..*" Book : gestiona
    BookService "1" --> "1" BookValidator : usa
    UserService "1" --> "0..*" User : gestiona
    UserService "1" --> "1" UserValidator : usa
    LoanService "1" --> "0..*" Loan : gestiona
    LoanService "1" --> "1" BookService : usa
    LoanService "1" --> "1" UserService : usa
    LoanService "1" --> "1" LoanValidator : usa
    BookController "1" --> "1" BookService : delega en
    UserController "1" --> "1" UserService : delega en
    LoanController "1" --> "1" LoanService : delega en
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
