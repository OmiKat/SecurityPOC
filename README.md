# SecurityPOC

A **Java authentication proof-of-concept** built with Spring Boot 4.0. It demonstrates JWT-based access/refresh token flow, role-based API security, and user registration with a PostgreSQL backend.

## Features

- **Sign up** – Register with email, password, and role (ADMIN, MANAGER, USER).
- **Sign in** – HTTP Basic auth; returns JWT access token and sets HTTP-only refresh token cookie.
- **Refresh token** – Exchange a valid refresh token for a new access token.
- **Logout** – Revoke refresh token (send refresh token in `Authorization` header).
- **Protected API** – `/api/**` endpoints require a valid Bearer access token and respect JWT scopes (READ, WRITE, DELETE) derived from roles.

## Tech Stack

| Layer        | Technology                          |
|-------------|-------------------------------------|
| Runtime     | Java 21                             |
| Framework   | Spring Boot 4.0.2                   |
| Security    | Spring Security, OAuth2 Resource Server, JWT (Nimbus) |
| Data        | Spring Data JPA, PostgreSQL         |
| Validation  | Bean Validation (Jakarta)           |
| Build       | Maven                               |

## Project Structure

```
src/main/java/com/omi/SeXurityPOC/
├── SeXurityPocApplication.java      # Entry point
├── config/
│   ├── SecurityConfig.java          # Filter chains: /api, /sign-in, /refresh-token, /logout, /sign-up
│   ├── RSAKeyRecord.java            # JWT RSA key configuration
│   └── jwtConfig/
│       ├── JwtAccessTokenFilter.java   # Validates access token for /api/**
│       ├── JwtRefreshTokenFilter.java  # Validates refresh token for /refresh-token
│       ├── JwtTokenGenerator.java      # Builds access (15 min) and refresh (15 days) JWTs
│       └── JwtTokenUtils.java          # JWT validation and user lookup
├── controller/
│   ├── AuthController.java          # sign-in, sign-up, refresh-token
│   └── UserController.java          # /api/welcome_msg, msg-manager, msg-admin
├── service/
│   ├── AuthService.java             # Login, registration, refresh, cookie handling
│   └── LogoutHandlerService.java    # Revokes refresh token on logout
├── repo/
│   ├── UserRepo.java
│   └── RefreshTokenRepo.java
├── pojos/
│   ├── User.java                    # USER_INFO entity
│   ├── RefreshTokenEntity.java      # REFRESH_TOKENS entity
│   ├── UserRegistrationDto.java     # Sign-up request body
│   ├── Roles.java                   # ADMIN, MANAGER, USER
│   └── TokenType.java               # Bearer
├── dto/
│   └── AuthResponseDto.java         # access_token, expiry, user_name, token_type
├── mapper/
│   └── UserInfoMapper.java          # DTO → User entity (with password encoding)
├── config/userConfig/
│   ├── UserInfoConfig.java          # UserDetailsService for sign-in
│   └── UserConfig.java              # UserDetails implementation
└── cmdrunner/
    └── CmdLineRunner.java           # Seeds admin, manager, user (password: "password")
```

## Prerequisites

- **Java 21**
- **Maven 3.6+**
- **PostgreSQL** (local or hosted, e.g. Neon)

## Configuration

Configure `src/main/resources/application.properties` (or use environment variables / profiles). **Do not commit real credentials.**

### Database

```properties
spring.datasource.url=jdbc:postgresql://<host>:<port>/<database>?sslmode=require
spring.datasource.username=<user>
spring.datasource.password=<password>
```

Optional Hikari tuning (already present in the project):

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.max-lifetime=300000
spring.datasource.hikari.idle-timeout=60000
spring.datasource.hikari.connection-timeout=30000
```

### JPA

```properties
spring.jpa.hibernate.ddl-auto=create-drop   # or update for persistent schema
spring.jpa.show-sql=true
```

### JWT (RSA keys)

PEM files are loaded from the classpath:

```properties
jwt.rsa-private-key=classpath:certs/privateKey.pem
jwt.rsa-public-key=classpath:certs/publicKey.pem
```

Generate a key pair if needed:

```bash
# Example: generate RSA key pair (e.g. 2048-bit)
openssl genrsa -out keypair.pem 2048
openssl rsa -in keypair.pem -pubout -out publicKey.pem
openssl pkcs8 -topk8 -inform PEM -in keypair.pem -out privateKey.pem -nocrypt
```

Place `privateKey.pem` and `publicKey.pem` under `src/main/resources/certs/`.

## Build and Run

```bash
# Build
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar target/SeXurityPOC-0.0.1-SNAPSHOT.jar
```

By default the app runs on port **8080** (override with `server.port` if needed).

## API Reference

Base URL: `http://localhost:8080` (unless changed).

### 1. Sign up (public)

**POST** `/sign-up`

Request body (JSON):

```json
{
  "userName": "jane",
  "userMobileNo": "555-1234",
  "userEmail": "jane@example.com",
  "userPassword": "secret123",
  "userRole": "USER"
}
```

- `userRole`: one of `ADMIN`, `MANAGER`, `USER`.
- Validations: non-empty `userName`, `userEmail`, `userPassword`, `userRole`; `userEmail` must be valid format.

Response: same shape as sign-in (access token, expiry, user name, token type). A refresh token is also set in an HTTP-only cookie.

---

### 2. Sign in (HTTP Basic)

**POST** `/sign-in`

- **Authorization:** Basic base64(email:password)
- Response (JSON): `access_token`, `access_token_expiry` (seconds), `user_name`, `token_type` (Bearer).
- Response **Set-Cookie:** `refresh_token` (HTTP-only, secure).

Example with curl:

```bash
curl -X POST http://localhost:8080/sign-in \
  -u "user@user.com:password" \
  -v
```

---

### 3. Refresh access token

**POST** `/refresh-token`

- **Authorization:** Bearer \<refresh_token\>
- Refresh token can be taken from the cookie (e.g. in a browser) or from the sign-in response if stored by the client.
- Response: new access token and metadata (same shape as sign-in, no new refresh token in body; cookie may be reused).

Example:

```bash
curl -X POST http://localhost:8080/refresh-token \
  -H "Authorization: Bearer <refresh_token>"
```

---

### 4. Logout

**POST** `/logout`

- **Authorization:** Bearer \<token\>
- Typically send the **refresh token** so the server can revoke it. The filter accepts a valid JWT (e.g. refresh token) for this endpoint.
- Response: 200; the matching refresh token is marked revoked in the database.

---

### 5. Protected API (Bearer access token)

All require **Authorization: Bearer \<access_token\>**.

| Method | Endpoint           | Scope required | Description        |
|--------|--------------------|----------------|--------------------|
| GET    | `/api/welcome_msg` | SCOPE_READ     | Generic welcome    |
| GET    | `/api/msg-manager` | SCOPE_READ     | Manager message    |
| GET    | `/api/msg-admin`   | SCOPE_WRITE    | Admin message      |

Role → scope mapping (in token):

- **ADMIN:** READ, WRITE, DELETE  
- **MANAGER:** READ  
- **USER:** READ  

Example:

```bash
curl -X GET http://localhost:8080/api/welcome_msg \
  -H "Authorization: Bearer <access_token>"
```

---

### Auth response shape (sign-in / sign-up / refresh-token)

```json
{
  "access_token": "eyJ...",
  "access_token_expiry": 900,
  "token_type": "Bearer",
  "user_name": "user"
}
```

- Access token expiry: 15 minutes (900 seconds) for sign-in/sign-up; 5 minutes (300 seconds) for refresh response in code.
- Refresh token: 15 days, stored in DB and (optionally) in HTTP-only cookie.

## Seeded Users

`CmdLineRunner` creates three users (password for all: **password**):

| Email             | Role    |
|-------------------|--------|
| admin@admin.com   | ADMIN  |
| manager@manager.com | MANAGER |
| user@user.com     | USER   |

Schema is recreated on each run if `spring.jpa.hibernate.ddl-auto=create-drop` is used.

## Running Tests

```bash
./mvnw test
```

Requires a valid database and JWT configuration (e.g. test profile or in-memory DB if you add one) for `@SpringBootTest`.

## Security Notes (POC)

- **Credentials:** Do not commit real database URLs, passwords, or private keys. Use env vars or secure config for production.
- **HTTPS:** Use TLS in production; cookies are set with `Secure` and `HttpOnly`.
- **CORS/CSRF:** CSRF is disabled for the stateless API; configure CORS and CSRF appropriately for production.
- **Rate limiting / account lockout:** Not implemented in this POC.

## License

See repository or project metadata.
