# DOSW-Library

Sistema de gestión de biblioteca desarrollado con Spring Boot y Maven. Permite registrar usuarios, agregar libros con sus ejemplares disponibles, realizar préstamos y registrar devoluciones.

---

## Diagrama General

el sistema se divide en capas. el cliente manda peticiones HTTP que llegan a los controladores, cada controlador le pasa el trabajo al servicio que le corresponde. los servicios usan los validadores para revisar que los datos esten bien antes de tocar los modelos. los controladores usan los mappers para convertir entre los modelos internos y los DTOs que ve el cliente.

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

aca se ve paso a paso como funciona un prestamo. el cliente llama al `LoanController` con el id del usuario y el libro, ese le pasa la tarea al `LoanService`. el servicio valida los ids, busca el usuario y el libro, revisa que haya ejemplares disponibles y que el usuario no tenga mas de 3 prestamos activos. si todo esta bien le resta un ejemplar al libro y registra el prestamo, al final le devuelve un `LoanDTO` al cliente.

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

aca estan todas las clases del sistema con sus atributos, metodos y como se relacionan entre ellas. `Loan` es la clase mas importante porque conecta un `Book` con un `User` y guarda el estado del prestamo con `LoanStatus`. los servicios manejan las listas de cada entidad y cada uno tiene su validador. los controladores solo reciben la peticion HTTP y se la pasan al servicio.

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

## Análisis estático con SonarCloud

Resultado del análisis estático del proyecto en SonarCloud.

<img width="1699" height="856" alt="image" src="https://github.com/user-attachments/assets/5c860378-2300-4ef3-a5cc-92e5c989e168" />



## Evidencia de pruebas

### Pruebas unitarias
![prueba test libreria](imagenes/prueba%20test%20libreria.png)

### Cobertura JaCoCo
<img width="1288" height="862" alt="image" src="https://github.com/user-attachments/assets/91bccff8-16c6-4bc8-9d85-b477b01677e6" />

<img width="1060" height="532" alt="image" src="https://github.com/user-attachments/assets/f0f135c6-8141-44c0-94d9-43e4a8d25a53" />


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
