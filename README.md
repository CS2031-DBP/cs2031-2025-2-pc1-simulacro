## 🧩 **Pregunta práctica (12 ptos.)**

Una universidad local requiere desarrollar **Aula Connect**, un servicio REST para gestionar la asignación de estudiantes a salones y tutores de forma organizada.
El sistema permitirá que los tutores administren sus grupos de clase y que los estudiantes visualicen la información de su salón.
Su implementación debe usar **arquitectura en 3 capas (Controller, Service, Repository)**, **DTOs para entrada/salida**, **manejo correcto de excepciones** y **seguridad JWT**.

---

### 🧱 **Entidades**

**Tutor**

| Campo        | Tipo                             | Descripción            |
| ------------ | -------------------------------- | ---------------------- |
| id           | Long (autogenerado)              | Identificador único    |
| nombre       | String (obligatorio)             | Nombre del tutor       |
| especialidad | String                           | Área o curso principal |
| email        | String (único, obligatorio)      | Correo institucional   |
| password     | String (obligatorio, encriptado) |                        |
| rol          | String (default: ROLE_TUTOR)     |                        |

---

**Salon**

| Campo   | Tipo                      | Descripción                         |
| ------- | ------------------------- | ----------------------------------- |
| id      | Long (autogenerado)       | Identificador único                 |
| codigo  | String (obligatorio)      | Código del salón (por ejemplo “A1”) |
| grado   | String                    | Nivel o ciclo académico             |
| tutorId | Long (referencia a Tutor) | Tutor asignado al salón             |

---

**Estudiante**

| Campo    | Tipo                              | Descripción            |
| -------- | --------------------------------- | ---------------------- |
| id       | Long (autogenerado)               | Identificador único    |
| nombre   | String (obligatorio)              | Nombre completo        |
| edad     | Integer (obligatorio, 17-60)      | Edad del estudiante    |
| email    | String (único, obligatorio)       | Correo institucional   |
| password | String (obligatorio, encriptado)  |                        |
| rol      | String (default: ROLE_ESTUDIANTE) |                        |
| salonId  | Long (referencia a Salon)         | Salón al que pertenece |

---

### 📦 **Endpoints**

#### 1️⃣ Registro de usuario (Estudiante)

**POST** `/api/auth/register`
**Request DTO:**

```json
{
  "nombre": "Carlos Ramos",
  "edad": 20,
  "email": "carlos@utec.edu.pe",
  "password": "Clave123"
}
```

**Response DTO:**

```json
{
  "id": 1,
  "nombre": "Carlos Ramos",
  "edad": 20,
  "email": "carlos@utec.edu.pe",
  "rol": "ESTUDIANTE"
}
```

**Status Code:** `201 Created`
**Validaciones:** email único, password ≥ 8 caracteres.

---

#### 2️⃣ Login

**POST** `/api/auth/login`
**Request DTO:**

```json
{
  "email": "carlos@utec.edu.pe",
  "password": "Clave123"
}
```

**Response DTO:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 3600
}
```

**Status Code:** `200 OK`
**Validaciones:** credenciales correctas, usuario existente.

---

#### 3️⃣ Listar salones

**GET** `/api/aulas/salones`
**Headers:** `Authorization: Bearer {{token}}`

**Response DTO:**

```json
[
  {
    "id": 1,
    "codigo": "A1",
    "grado": "Tercer ciclo",
    "tutorNombre": "Prof. García"
  },
  {
    "id": 2,
    "codigo": "B1",
    "grado": "Cuarto ciclo",
    "tutorNombre": "Ing. Rojas"
  }
]
```

**Status Code:** `200 OK`
**Acceso:** Tutor o estudiante autenticado.

---

#### 4️⃣ Ver estudiantes por salón

**GET** `/api/aulas/salones/{id}/estudiantes`
**Headers:** `Authorization: Bearer {{token}}`

**Response DTO:**

```json
[
  {
    "id": 10,
    "nombre": "Ana López",
    "edad": 21,
    "email": "ana@utec.edu.pe",
    "rol": "ESTUDIANTE",
    "salonId": 1
  }
]
```

**Status Code:** `200 OK`
**Excepción:** `SalonNotFoundException → 404 Not Found` si el salón no existe.

---

#### 5️⃣ Asignar estudiante a salón (solo Tutor)

**POST** `/api/aulas/tutores/{tutorId}/asignar/{estudianteId}/salon/{salonId}`
**Headers:** `Authorization: Bearer {{token}}`
**Response DTO:**

```json
{
  "message": "Estudiante Ana López asignado al salón A1 por el tutor Prof. García"
}
```

**Status Code:** `200 OK`
**Excepción:** `UnauthorizedAssignmentException → 403 Forbidden` si el tutor no pertenece a ese salón.

---

#### 6️⃣ Ver el salón del estudiante

**GET** `/api/aulas/estudiantes/{id}/salon`
**Headers:** `Authorization: Bearer {{token}}`
**Response DTO:**

```json
{
  "id": 1,
  "codigo": "A1",
  "grado": "Tercer ciclo",
  "tutorNombre": "Prof. García"
}
```

**Status Code:** `200 OK`
**Excepción:** `StudentNotFoundException → 404 Not Found`

---

### ⚙️ **Requerimientos Técnicos**

#### Seguridad

* Endpoints `/api/auth/**` son **públicos**.
* Todos los demás requieren **JWT válido**.
* Implementar `UserDetailsService` y `SecurityFilterChain`.
* Roles soportados: `ROLE_ESTUDIANTE`, `ROLE_TUTOR`.

#### Persistencia

* Base de datos: PostgreSQL.
* Spring Data JPA con repositorios para `Tutor`, `Salon` y `Estudiante`.
* Restricciones:

  * `email` único para estudiantes y tutores.
  * Relaciones `@ManyToOne` y `@OneToMany` correctas.

#### Validaciones

* `@NotBlank`, `@Email`, `@Size`, `@Min`, `@Max` en DTOs.
* `@Valid` en controladores.

#### Manejo de Excepciones

* `@ControllerAdvice` global (`GlobalExceptionHandler`).
* Respuestas consistentes con campos:

  ```json
  {
    "timestamp": "2025-10-04T14:23:00",
    "status": 404,
    "error": "Recurso no encontrado",
    "message": "Salón no encontrado"
  }
  ```
* Códigos HTTP correctos (`400`, `401`, `403`, `404`, `409`).

---

### 🚨 **Excepciones mínimas requeridas**

| Excepción                         | Status | Caso                                           |
| --------------------------------- | ------ | ---------------------------------------------- |
| `UserNotFoundException`           | 404    | Usuario o estudiante inexistente               |
| `UserAlreadyExistsException`      | 409    | Registro duplicado por email                   |
| `InvalidCredentialsException`     | 401    | Login con credenciales incorrectas             |
| `SalonNotFoundException`          | 404    | Salón inexistente                              |
| `UnauthorizedAssignmentException` | 403    | Tutor intenta asignar alumno fuera de su salón |
| `UnauthorizedException`           | 401    | Acceso sin token o token inválido              |
