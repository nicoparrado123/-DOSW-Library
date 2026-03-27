# DOSW-Library

Sistema de gestión de biblioteca desarrollado con Spring Boot y Maven. Permite registrar usuarios, agregar libros con inventario, realizar préstamos y registrar devoluciones. Incluye persistencia en PostgreSQL y seguridad basada en JWT con roles.

---

## Diagrama General

el sistema se divide en capas. el cliente manda peticiones HTTP que primero pasan por el filtro JWT. si el token es válido, la request llega al controlador correspondiente. cada controlador le pasa el trabajo al servicio. los servicios usan los validadores para revisar los datos, luego operan sobre los modelos de dominio y persisten los cambios a través de los repositorios JPA. los controladores usan los mappers para convertir entre modelos y DTOs.

```mermaid
graph TD
    Cliente -->|HTTP Request| JwtAuthFilter
    JwtAuthFilter -->|token válido| Controller
    JwtAuthFilter -->|token inválido| 401
    Controller -->|delega lógica| Service
    Service -->|valida entrada con| Validator
    Service -->|opera sobre| Model
    Service -->|persiste con| Repository
    Repository -->|accede a| Database[(PostgreSQL)]
    Controller -->|convierte con| Mapper
    Mapper -->|transforma a/desde| DTO
```

---

## Diagrama de Autenticación

el cliente manda sus credenciales al endpoint de login. el sistema las valida contra la base de datos, y si son correctas genera un JWT firmado con el id del usuario y su rol. el cliente usa ese token en cada request siguiente en el header Authorization.

```mermaid
sequenceDiagram
    participant Cliente
    participant AuthController
    participant UserRepository
    participant JwtService

    Cliente->>AuthController: POST /auth/login {username, password}
    AuthController->>UserRepository: findByUsername(username)
    UserRepository-->>AuthController: UserEntity
    AuthController->>AuthController: verificar password (BCrypt)
    AuthController->>JwtService: generateToken(userId, role)
    JwtService-->>AuthController: JWT firmado
    AuthController-->>Cliente: {token} (200 OK)

    Cliente->>AuthController: GET /libros
    Note over Cliente,AuthController: Authorization: Bearer token
    AuthController->>JwtService: isValid(token)
    JwtService-->>AuthController: true
    AuthController-->>Cliente: [libros] (200 OK)
```

---

## Diagrama Específico

aca se ve paso a paso como funciona un prestamo con persistencia. el cliente manda el token JWT, el filtro lo valida y carga el usuario en el contexto de seguridad. el `LoanController` delega al `LoanService`, que valida los ids, busca las entidades en la base de datos, verifica disponibilidad y límite de préstamos, actualiza el stock y persiste el préstamo.

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtAuthFilter
    participant LoanController
    participant LoanValidator
    participant UserService
    participant BookService
    participant LoanService
    participant LoanRepository
    participant BookRepository

    Cliente->>JwtAuthFilter: POST /prestamos/{idUsuario}/{idLibro} + Bearer token
    JwtAuthFilter->>JwtAuthFilter: validar token y cargar contexto
    JwtAuthFilter->>LoanController: request autorizada
    LoanController->>LoanService: prestar(idUsuario, idLibro)
    LoanService->>LoanValidator: validar(idUsuario, idLibro)
    LoanService->>UserService: buscarEntidadPorId(idUsuario)
    UserService->>LoanRepository: findById(idUsuario)
    LoanRepository-->>UserService: UserEntity
    UserService-->>LoanService: UserEntity
    LoanService->>BookService: buscarEntidadPorId(idLibro)
    BookService->>BookRepository: findById(idLibro)
    BookRepository-->>BookService: BookEntity
    BookService-->>LoanService: BookEntity
    LoanService->>LoanRepository: countByUsuarioIdAndEstado(ACTIVO)
    LoanRepository-->>LoanService: cantidad activos
    LoanService->>BookService: actualizarEjemplares(idLibro, stock - 1)
    BookService->>BookRepository: save(BookEntity)
    LoanService->>LoanRepository: save(LoanEntity)
    LoanRepository-->>LoanService: LoanEntity
    LoanService-->>LoanController: Loan
    LoanController-->>Cliente: LoanDTO (200 OK)
```

---

## Diagrama de Clases

el sistema tiene dos capas de modelo: los modelos de dominio (`Book`, `User`, `Loan`) que usan los servicios internamente, y las entidades JPA (`BookEntity`, `UserEntity`, `LoanEntity`) que se persisten en la base de datos. `UserEntity` ahora incluye `username`, `password` y `role`. la capa de seguridad tiene `JwtService` para generar y validar tokens, `JwtAuthFilter` que intercepta cada request, y `SecurityConfig` que define las reglas de acceso.

```mermaid
classDiagram
    class Book {
        -String id
        -String titulo
        -String autor
    }

    class User {
        -String id
        -String nombre
        -String username
        -String password
        -Role role
    }

    class Loan {
        -Book libro
        -User usuario
        -LocalDate fechaPrestamo
        -LocalDate fechaDevolucion
        -LoanStatus estado
    }

    class LoanStatus {
        <<enumeration>>
        ACTIVO
        DEVUELTO
    }

    class BookEntity {
        -String id
        -String titulo
        -String autor
        -int stockTotal
        -int stockDisponible
    }

    class UserEntity {
        -String id
        -String nombre
        -String username
        -String password
        -Role role
    }

    class Role {
        <<enumeration>>
        USER
        LIBRARIAN
    }

    class LoanEntity {
        -Long id
        -BookEntity libro
        -UserEntity usuario
        -LocalDate fechaPrestamo
        -LocalDate fechaDevolucion
        -LoanStatus estado
    }

    class BookRepository {
        +findById(String)
        +findAll()
        +save(BookEntity)
    }

    class UserRepository {
        +findById(String)
        +findByUsername(String)
        +save(UserEntity)
    }

    class LoanRepository {
        +findByUsuarioId(String)
        +countByUsuarioIdAndEstado(String, LoanStatus)
        +findByUsuarioIdAndLibroIdAndEstado(String, String, LoanStatus)
        +save(LoanEntity)
    }

    class BookService {
        +agregarLibro(Book, int)
        +obtenerTodos()
        +buscarPorId(String)
        +obtenerEjemplares(String)
        +actualizarEjemplares(String, int)
        +buscarEntidadPorId(String)
    }

    class UserService {
        +registrar(User)
        +obtenerTodos()
        +buscarPorId(String)
        +buscarEntidadPorId(String)
    }

    class LoanService {
        +prestar(String, String)
        +devolver(String, String)
        +obtenerTodos()
        +obtenerPorUsuario(String)
    }

    class JwtService {
        +generateToken(String, String)
        +extractUserId(String)
        +extractRole(String)
        +isValid(String)
    }

    class JwtAuthFilter {
        +doFilterInternal()
    }

    class SecurityConfig {
        +filterChain(HttpSecurity)
        +passwordEncoder()
    }

    class AuthController {
        +login(Map)
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
        +obtenerPorUsuario(String)
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
    LoanEntity --> BookEntity
    LoanEntity --> UserEntity
    LoanEntity --> LoanStatus
    UserEntity --> Role
    BookService --> BookRepository
    BookService --> BookValidator
    UserService --> UserRepository
    UserService --> UserValidator
    LoanService --> LoanRepository
    LoanService --> BookService
    LoanService --> UserService
    LoanService --> LoanValidator
    BookController --> BookService
    UserController --> UserService
    LoanController --> LoanService
    AuthController --> UserRepository
    AuthController --> JwtService
    JwtAuthFilter --> JwtService
    SecurityConfig --> JwtAuthFilter
```

---

## Diagrama Entidad-Relación

la base de datos tiene tres tablas. `users` guarda los datos del usuario incluyendo credenciales y rol. `books` guarda el libro con su stock total y disponible. `loans` conecta un usuario con un libro y guarda el estado del préstamo.

```mermaid
erDiagram
    USERS {
        varchar id PK
        varchar nombre
        varchar username
        varchar password
        varchar role
    }

    BOOKS {
        varchar id PK
        varchar titulo
        varchar autor
        int stock_total
        int stock_disponible
    }

    LOANS {
        bigint id PK
        varchar user_id FK
        varchar book_id FK
        date fecha_prestamo
        date fecha_devolucion
        varchar estado
    }

    USERS ||--o{ LOANS : "tiene"
    BOOKS ||--o{ LOANS : "es prestado en"
```

---

## Modelo No Relacional (MongoDB)

el modelo NoSQL usa tres colecciones. `books` y `users` son documentos independientes con toda su información embebida. `loans` referencia a ambos por id y embebe el historial de cambios de estado directamente dentro del documento del préstamo.

### Decisiones de diseño

- `metadata`, `disponibilidad` y `categorias` se **embeben** en `books` porque son datos propios del libro, siempre se leen juntos y no se comparten con otros documentos.
- `historial` se **embebe** en `loans` porque es exclusivo de ese préstamo y crece de forma acotada (pocos cambios de estado por préstamo).
- `usuario` y `libro` en `loans` se **referencian** por id porque son entidades independientes que pueden consultarse y modificarse por separado.

### Colección: books

```json
{
  "_id": "isbn-001",
  "titulo": "Clean Code",
  "autor": "Robert C. Martin",
  "isbn": "978-0132350884",
  "tipoPublicacion": "ebook",
  "fechaPublicacion": "2008-08-01",
  "fechaAgregado": "2024-01-15",
  "categorias": ["programacion", "buenas practicas"],
  "metadata": {
    "paginas": 431,
    "idioma": "ingles",
    "editorial": "Prentice Hall"
  },
  "disponibilidad": {
    "status": "DISPONIBLE",
    "totalCopias": 5,
    "copiasDisponibles": 3,
    "copiasPrestadas": 2
  }
}
```

### Colección: users

```json
{
  "_id": "u-001",
  "nombre": "Juan Perez",
  "username": "jperez",
  "password": "$2a$10$...",
  "email": "jperez@mail.com",
  "role": "USER",
  "membresia": "PLATINUM",
  "fechaRegistro": "2024-03-10"
}
```

### Colección: loans

```json
{
  "_id": "loan-001",
  "usuarioId": "u-001",
  "libroId": "isbn-001",
  "fechaPrestamo": "2024-11-01",
  "fechaDevolucion": "2024-11-15",
  "estado": "DEVUELTO",
  "historial": [
    { "status": "ACTIVO",    "fecha": "2024-11-01" },
    { "status": "DEVUELTO",  "fecha": "2024-11-15" }
  ]
}
```

### Diagrama de colecciones

```mermaid
graph TD
    LOANS["loans\n─────\n_id\nusuarioId ──ref──▶\nlibroId   ──ref──▶\nfechaPrestamo\nfechaDevolucion\nestado\nhistorial embebido"]
    USERS["users\n─────\n_id\nnombre\nusername\npassword\nemail\nrole\nmembresia\nfechaRegistro"]
    BOOKS["books\n─────\n_id\ntitulo\nautor\nisbn\ntipoPublicacion\nfechaPublicacion\nfechaAgregado\ncategorias[ ]\nmetadata embebido\ndisponibilidad embebido"]

    LOANS -->|referencia usuarioId| USERS
    LOANS -->|referencia libroId| BOOKS
```

---

## Análisis estático con SonarCloud

Resultado del análisis estático del proyecto en SonarCloud.

<img width="1699" height="856" alt="image" src="https://github.com/user-attachments/assets/5c860378-2300-4ef3-a5cc-92e5c989e168" />



## Evidencia de pruebas

### Pruebas unitarias

<img width="1060" height="532" alt="image" src="https://github.com/user-attachments/assets/f0f135c6-8141-44c0-94d9-43e4a8d25a53" />

### Cobertura JaCoCo
<img width="1288" height="862" alt="image" src="https://github.com/user-attachments/assets/91bccff8-16c6-4bc8-9d85-b477b01677e6" />



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
