# DOSW-Library

Sistema de gestión de biblioteca desarrollado con Spring Boot y Maven. Permite registrar usuarios, agregar libros con inventario, realizar préstamos y registrar devoluciones. Incluye persistencia en PostgreSQL y seguridad basada en JWT con roles.

---

## Diagrama General

el cliente manda peticiones HTTP que pasan por el filtro JWT. si el token es válido llega al controlador, que delega al servicio. el servicio valida, opera sobre el modelo de dominio y persiste a través de una interfaz de repositorio. según el perfil activo (`mongo` o `relational`), Spring inyecta la implementación correspondiente.

```mermaid
graph TD
    Cliente -->|HTTP Request| JwtAuthFilter
    JwtAuthFilter -->|token válido| Controller
    JwtAuthFilter -->|token inválido| 401
    Controller -->|delega lógica| Service
    Service -->|valida con| Validator
    Service -->|usa interfaz| RepositoryPort
    RepositoryPort -->|perfil mongo| MongoImpl
    RepositoryPort -->|perfil relational| JpaImpl
    MongoImpl -->|accede a| MongoDB[(MongoDB Atlas)]
    JpaImpl -->|accede a| PostgreSQL[(PostgreSQL)]
    Controller -->|convierte con| Mapper
    Mapper -->|transforma a/desde| DTO
```

---

## Diagrama de Autenticación

el cliente manda sus credenciales al endpoint de login. el sistema las valida contra la base de datos activa (MongoDB o PostgreSQL según el perfil), genera un JWT firmado y el cliente lo usa en cada request siguiente.

```mermaid
sequenceDiagram
    participant Cliente
    participant AuthController
    participant UserRepositoryPort
    participant JwtService

    Cliente->>AuthController: POST /auth/login {username, password}
    AuthController->>UserRepositoryPort: findByUsername(username)
    UserRepositoryPort-->>AuthController: User
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

el cliente manda el token JWT, el filtro lo valida. el `LoanController` delega al `LoanService`, que valida los ids, busca los modelos de dominio, verifica disponibilidad y límite, actualiza el stock y persiste el préstamo a través de la interfaz `LoanRepositoryPort`.

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtAuthFilter
    participant LoanController
    participant LoanService
    participant UserService
    participant BookService
    participant LoanRepositoryPort
    participant DB[(MongoDB o PostgreSQL)]

    Cliente->>JwtAuthFilter: POST /prestamos/{idUsuario}/{idLibro} + Bearer token
    JwtAuthFilter->>JwtAuthFilter: validar token y cargar contexto
    JwtAuthFilter->>LoanController: request autorizada
    LoanController->>LoanService: prestar(idUsuario, idLibro)
    LoanService->>UserService: buscarEntidadPorId(idUsuario)
    UserService-->>LoanService: User
    LoanService->>BookService: buscarPorId(idLibro)
    BookService-->>LoanService: Book
    LoanService->>LoanRepositoryPort: countByUsuarioIdAndEstado(ACTIVO)
    LoanRepositoryPort-->>LoanService: cantidad activos
    LoanService->>BookService: actualizarEjemplares(idLibro, stock - 1)
    LoanService->>LoanRepositoryPort: save(Loan)
    LoanRepositoryPort->>DB: persistir
    DB-->>LoanRepositoryPort: Loan guardado
    LoanRepositoryPort-->>LoanService: Loan
    LoanService-->>LoanController: Loan
    LoanController-->>Cliente: LoanDTO (200 OK)
```

---

## Diagrama de Clases

el sistema tiene modelos de dominio (`Book`, `User`, `Loan`) que usan los servicios. las interfaces de repositorio (`BookRepositoryPort`, `UserRepositoryPort`, `LoanRepositoryPort`) desacoplan los servicios de la persistencia. según el perfil activo, Spring inyecta la implementación JPA o MongoDB.

```mermaid
classDiagram
    class Book {
        -String id
        -String titulo
        -String autor
        -int stockDisponible
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
    class Role {
        <<enumeration>>
        USER
        LIBRARIAN
    }
    class BookRepositoryPort {
        <<interface>>
        +save(Book, int)
        +findById(String)
        +findAll()
        +getStock(String)
        +updateStock(String, int)
    }
    class UserRepositoryPort {
        <<interface>>
        +save(User)
        +findById(String)
        +findByUsername(String)
        +findAll()
    }
    class LoanRepositoryPort {
        <<interface>>
        +save(Loan)
        +findAll()
        +findByUsuarioId(String)
        +countByUsuarioIdAndEstado(String, LoanStatus)
        +findByUsuarioIdAndLibroIdAndEstado(String, String, LoanStatus)
    }
    class BookRepositoryJpaImpl {
        <<Profile relational>>
    }
    class BookRepositoryMongoImpl {
        <<Profile mongo>>
    }
    class UserRepositoryJpaImpl {
        <<Profile relational>>
    }
    class UserRepositoryMongoImpl {
        <<Profile mongo>>
    }
    class LoanRepositoryJpaImpl {
        <<Profile relational>>
    }
    class LoanRepositoryMongoImpl {
        <<Profile mongo>>
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
    class JwtService {
        +generateToken(String, String)
        +isValid(String)
    }
    class JwtAuthFilter {
        +doFilterInternal()
    }
    class SecurityConfig {
        +filterChain(HttpSecurity)
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

    Loan --> Book
    Loan --> User
    Loan --> LoanStatus
    User --> Role
    BookRepositoryPort <|.. BookRepositoryJpaImpl
    BookRepositoryPort <|.. BookRepositoryMongoImpl
    UserRepositoryPort <|.. UserRepositoryJpaImpl
    UserRepositoryPort <|.. UserRepositoryMongoImpl
    LoanRepositoryPort <|.. LoanRepositoryJpaImpl
    LoanRepositoryPort <|.. LoanRepositoryMongoImpl
    BookService --> BookRepositoryPort
    UserService --> UserRepositoryPort
    LoanService --> LoanRepositoryPort
    LoanService --> BookService
    LoanService --> UserService
    BookController --> BookService
    UserController --> UserService
    LoanController --> LoanService
    AuthController --> UserRepositoryPort
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

---

## Evidencia Parte 3 - Hackathon

### Video pruebas funcionales con Swagger (Reto 5)
https://youtu.be/Sx4snV0t0pA

### Video persistencia en MongoDB (Reto 5)
https://youtu.be/azH0CyXqULU

---

## Evidencia workflow 

<img width="1919" height="857" alt="image" src="https://github.com/user-attachments/assets/6085057f-50ff-4fb5-b3a5-7f6c2f8713ee" />

---


## Análisis estático con SonarCloud

Resultado del análisis estático del proyecto en SonarCloud.

<img width="1699" height="856" alt="image" src="https://github.com/user-attachments/assets/5c860378-2300-4ef3-a5cc-92e5c989e168" />



## Evidencia de pruebas

### Pruebas unitarias

<img width="1060" height="532" alt="image" src="https://github.com/user-attachments/assets/f0f135c6-8141-44c0-94d9-43e4a8d25a53" />

### Cobertura JaCoCo
<img width="1288" height="862" alt="image" src="https://github.com/user-attachments/assets/91bccff8-16c6-4bc8-9d85-b477b01677e6" />

### video funcionamiento seguridad

https://youtu.be/g_eFTXSoLVs



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
