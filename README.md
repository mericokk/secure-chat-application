# Secure Chat Application (Backend)

## Project Overview

This project is the **backend** of the **Secure Chat Application**, built with **Java, Spring Boot, Spring GraphQL, and PostgreSQL**. It provides the authentication, group management, encrypted message storage, group-key distribution, authorization, and real-time messaging infrastructure used by the chat client.

The backend is designed around a **client-side encryption model**. Message plaintext and private encryption keys are not processed by the server. Instead, the server stores and delivers encrypted message payloads and user-specific wrapped group keys while enforcing authentication, group membership, and ownership rules.

The main application interface is **GraphQL**, supporting queries and mutations through `/graphql` and real-time message delivery through **GraphQL Subscriptions over WebSockets**. A small REST endpoint is also provided for refresh-token rotation at `/auth/refresh`.

## Features

- **Encrypted Message Storage**: Message content is stored as `encryptedContent` together with its IV and group-key version. The backend treats the encrypted content as an opaque value.
- **Per-User Group Keys**: Group keys are stored separately for each user and key version, allowing clients to receive only the wrapped key intended for them.
- **Group Key Versioning**: Group membership changes rotate the group-key version. Messages also carry their `keyVersion` so clients can resolve the correct historical key when decrypting older messages.
- **GraphQL API**: Typed queries and mutations cover authentication, users, groups, memberships, key distribution, and encrypted messaging.
- **Real-Time Messaging**: `messageAdded` provides live message updates through GraphQL Subscriptions over WebSockets.
- **JWT Authentication**: Stateless access-token authentication is used for protected GraphQL operations.
- **Refresh Tokens**: Login creates a refresh token stored in PostgreSQL. `/auth/refresh` rotates the refresh token and issues a new access token.
- **Authorization Rules**: Group membership is required for group-specific operations, while group ownership is required for member management and other privileged operations.
- **Password Hashing**: User passwords are stored using BCrypt through Spring Security's `PasswordEncoder`.
- **Rate Limiting Support**: Resilience4j is configured with a rate limiter for chat traffic.
- **Containerized Deployment**: Docker and Docker Compose configurations are included for running the backend together with PostgreSQL.

## Architecture

The application follows a layered architecture under `src/main/java/com/chat/app/`.

| Package | Role and Responsibility |
| :--- | :--- |
| **`controller`** | **Transport Layer:** GraphQL controllers for authentication, users, groups, group keys, and messages. Also contains the REST refresh-token endpoint. |
| **`service`** | **Business Logic:** Authentication, user management, group management, membership authorization, key handling, current-user resolution, and message processing. |
| **`repository`** | **Data Access Layer:** Spring Data JPA repositories for users, groups, members, messages, refresh tokens, and group keys. |
| **`security`** | **Authentication:** JWT creation/validation, HTTP JWT filtering, and WebSocket authentication. |
| **`config`** | **Security Configuration:** Password hashing and Spring Security filter-chain configuration. |
| **`model`** | **Domain Entities:** `User`, `ChatGroup`, `GroupMember`, `GroupKey`, `Message`, and `RefreshToken`. |
| **`dto`** | **Data Transfer Objects:** GraphQL input and output models. |
| **`event`** | **Application Events:** Membership-change events used to trigger group-key rotation. |
| **`exception`** | **Error Handling:** Custom exceptions and a GraphQL exception resolver that maps application errors to GraphQL errors. |

## Project Structure

```text
src/
├── main/
│   ├── java/com/chat/app/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── event/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── security/
│   │   ├── service/
│   │   └── ChatApplication.java
│   │
│   └── resources/
│       ├── graphql/
│       │   └── schema.graphqls
│       └── application.properties
│
├── test/
│   └── java/com/chat/app/
│       └── ChatApplicationTests.java
│
├── .env
├── Dockerfile
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## GraphQL API

The GraphQL schema is defined in `src/main/resources/graphql/schema.graphqls`.

### Queries

| Operation | Description |
| :--- | :--- |
| `getUser(username)` | Returns a user's public profile data and public encryption key. |
| `getAllGroups` | Returns the groups the authenticated user belongs to. |
| `getGroupMembers(groupId)` | Returns the users belonging to an authorized group. |
| `getGroupKeyForUser(groupId, keyVersion)` | Returns the wrapped group key for the authenticated user and requested key version. |
| `getGroupKeyVersion(groupId)` | Returns the current key version of an authorized group. |
| `getGroupChatHistory(groupId)` | Returns stored encrypted messages for an authorized group. |
| `getMyGroupMembership(groupId)` | Returns the authenticated user's membership record for a group. |

### Mutations

| Operation | Description |
| :--- | :--- |
| `login(input)` | Validates credentials and returns an access token plus refresh token. |
| `registerUser(input)` | Creates a user with a BCrypt password hash and public encryption key. |
| `updatePublicKey(input)` | Updates the authenticated user's public encryption key. |
| `createGroup(input)` | Creates a group and assigns the creator the `OWNER` role. |
| `addGroupMember(input)` | Adds a user to a group. The requester must be the group owner. |
| `removeGroupMember(input)` | Removes a group member. Owners can remove members; non-owners can remove themselves. |
| `saveGroupKey(input)` | Stores a user-specific encrypted group-key payload for a specific key version. |
| `sendMessage(input)` | Stores an encrypted message after validating membership and the current group-key version. |
| `rotateGroupKey(groupId)` | Manually increments the group-key version. The caller must be the group owner. |

### Subscriptions

| Operation | Description |
| :--- | :--- |
| `messageAdded(groupId)` | Streams newly stored messages for an authorized group over WebSockets. |
| `groupMemberAdded` | Declared in the GraphQL schema for membership notifications; membership events are published by the service layer. |

> **Implementation note:** the current controller implementation actively maps `messageAdded` as a GraphQL subscription. The `groupMemberAdded` field is present in the schema and membership events are published internally, but there is no corresponding `@SubscriptionMapping` method in the current backend source.

## Encryption Model

The encryption layer is intentionally split between the client and the backend.

```text
Client
  |
  | Encrypt message with current group key
  | Encrypt/wrap group key for individual users
  v
Backend
  |
  | Stores encryptedContent + IV + keyVersion
  | Stores encryptedGroupKey + IV + senderPublicKey
  | Enforces membership / ownership rules
  v
PostgreSQL
```

### Messages

A message contains:

- `encryptedContent`
- `iv`
- `keyVersion`
- `senderId`
- `senderName`
- `groupId`
- `createdAt`

The backend does not decrypt `encryptedContent`. It only validates that the sender belongs to the group and that the submitted `keyVersion` matches the group's current key version.

### Group Keys

A stored `GroupKey` record contains:

- `groupId`
- `userId`
- `keyVersion`
- `encryptedGroupKey`
- `iv`
- `senderPublicKey`

A unique constraint on `(group_id, user_id, key_version)` prevents duplicate key records for the same user and version.

### Key Rotation

Every group starts with `keyVersion = 1`.

When a group membership is added or removed, `GroupMemberService` publishes a `GroupMembershipEvent`. `GroupKeyService` listens for that event and increments the group key version.

This means the backend can maintain key-version history while the client remains responsible for generating, wrapping, distributing, and decrypting the actual cryptographic material.

## Authentication and Authorization

### JWT Authentication

Protected HTTP requests use the standard header:

```http
Authorization: Bearer <access-token>
```

JWT validation is handled by `JwtAuthenticationFilter`. The token contains the authenticated username, user ID (`uid`), role information, issue time, and expiration time.

The current access-token lifetime in `JwtTokenProvider` is **24 hours**.

### Refresh Tokens

The login flow returns:

```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

Refresh tokens are stored in the `refresh_tokens` table and expire after **7 days**. Calling:

```http
POST /auth/refresh
Content-Type: application/json
```

with:

```json
{
  "refreshToken": "..."
}
```

replaces the existing refresh token and returns a new token pair.

### WebSocket Authentication

GraphQL subscriptions use the WebSocket endpoint:

```text
ws://localhost:8080/graphql
```

The WebSocket authentication interceptor expects the JWT to be sent during connection initialization using an `Authorization` value beginning with `Bearer `.

## Authorization Rules

The backend applies group-level access checks in the service layer.

| Action | Requirement |
| :--- | :--- |
| Read group members | Authenticated user must be a member of the group. |
| Read group history | Authenticated user must be a member of the group. |
| Read a group key | Authenticated user must be a member of the group. |
| Send a message | Authenticated user must be a member and use the current group-key version. |
| Add a member | Requester must be the group owner. |
| Remove another member | Requester must be the group owner. |
| Remove self | Allowed for members, except the owner cannot remove themselves. |
| Rotate group key manually | Requester must be the group owner. |
| Save a group key for another member | Requester must be the group owner and the target must be a group member. |

## Database Model

The application uses PostgreSQL with Spring Data JPA.

| Entity | Purpose |
| :--- | :--- |
| **`users`** | User credentials, username, and public encryption key. |
| **`chat_groups`** | Group metadata and current key version. |
| **`group_members`** | User-to-group membership and role (`OWNER` / `MEMBER`). |
| **`group_keys`** | User-specific encrypted group keys by key version. |
| **`messages`** | Encrypted group messages and their key versions. |
| **`refresh_tokens`** | Stored refresh tokens and expiration timestamps. |

The current configuration uses:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

This means the database schema is recreated from the JPA entities when the application starts and dropped when the application shuts down. This is convenient for development but should be replaced with a migration strategy for production deployments.

## Getting Started

### Prerequisites

- **Java 17+**
- **Maven 3.8+** or the included Maven Wrapper
- **PostgreSQL 14+** or Docker
- **Docker & Docker Compose** for containerized setup

The Dockerfile uses **Eclipse Temurin 21**, while the Maven project targets **Java 17**.

### Method 1: Local Maven Run

Start PostgreSQL and configure the database connection, then run:

```bash
# Linux / macOS
./mvnw clean package
./mvnw spring-boot:run

# Windows
mvnw.cmd clean package
mvnw.cmd spring-boot:run
```

The backend will be available at:

```text
http://localhost:8080
```

GraphQL endpoint:

```text
http://localhost:8080/graphql
```

GraphiQL:

```text
http://localhost:8080/graphiql
```

### Method 2: Docker

Build the backend image:

```bash
docker build -t secure-chat-backend .
```

Run it with the required database environment variables:

```bash
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=password \
  secure-chat-backend
```

### Method 3: Docker Compose

The included `docker-compose.yml` starts both PostgreSQL and the backend.

```bash
docker-compose up -d --build
```

View logs with:

```bash
docker-compose logs -f
```

Stop the environment with:

```bash
docker-compose down
```

The PostgreSQL service uses the named Docker volume `postgres_data` for persistent database storage.

## Environment Variables

The application reads database settings from environment variables and provides local development defaults.

| Variable | Default | Description |
| :--- | :--- | :--- |
| `DB_HOST` | `localhost` | PostgreSQL hostname. In Docker Compose this is `postgres-db`. |
| `DB_PORT` | `5432` | PostgreSQL port. |
| `DB_USERNAME` | `postgres` | PostgreSQL username. |
| `DB_PASSWORD` | `password` | PostgreSQL password. |
| `JWT_SECRET` | Generated at runtime | Base64-encoded JWT signing key. A configured persistent secret is required for production. |

The included `.env` currently contains the PostgreSQL username and password used by Docker Compose.

## Configuration

The main configuration lives in `src/main/resources/application.properties`.

Important settings include:

```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/chatdb
spring.graphql.graphiql.enabled=true
spring.graphql.graphiql.path=/graphiql
spring.graphql.websocket.path=/graphql
spring.graphql.cors.allowed-origins=http://localhost:5173
```

The current CORS configuration is intended for a local frontend running on `http://localhost:5173`.

## Rate Limiting

Resilience4j is configured with a chat rate limiter:

```properties
resilience4j.ratelimiter.instances.chatRateLimiter.limit-for-period=30
resilience4j.ratelimiter.instances.chatRateLimiter.limit-refresh-period=1m
resilience4j.ratelimiter.instances.chatRateLimiter.timeout-duration=0s
```

The configured policy allows up to **30 requests per refresh period of 1 minute** for components using this rate limiter.

## Error Handling

GraphQL errors are normalized through `GraphQLErrorHandler`.

Known application errors include:

- `USERNAME_TAKEN`
- `USER_NOT_FOUND`
- `INVALID_CREDENTIALS`
- `NOT_GROUP_MEMBER`
- `OWNER_REQUIRED`
- `GROUP_KEY_VERSION_OUTDATED`
- `INVALID_KEY_VERSION`
- `USER_ALREADY_MEMBER`
- `OWNER_CANNOT_REMOVE_SELF`
- `RATE_LIMIT_EXCEEDED`

These errors are exposed through the GraphQL error response instead of leaking stack traces to the client.

## Development Notes

### API Endpoints

| Endpoint | Transport | Purpose |
| :--- | :--- | :--- |
| `/graphql` | HTTP | GraphQL queries and mutations. |
| `/graphql` | WebSocket | GraphQL subscriptions. |
| `/graphiql` | HTTP | Interactive GraphQL development interface. |
| `/auth/refresh` | HTTP REST | Refresh-token rotation. |

### Build and Test

Run the available test suite with:

```bash
./mvnw test
```

The current repository includes a Spring Boot context-loading test in `src/test/java/com/chat/app/ChatApplicationTests.java`.

## Security Notes

- Keep `JWT_SECRET` configured as a strong, persistent secret in production. The current implementation falls back to an ephemeral generated key when `JWT_SECRET` is not provided, which invalidates previously issued tokens after application restart.
- Do not log plaintext messages or private encryption keys.
- Keep encrypted message and group-key fields opaque to backend business logic.
- Replace `spring.jpa.hibernate.ddl-auto=create-drop` with a controlled database migration strategy before production use.
- Replace default PostgreSQL credentials with secure deployment-specific credentials.
- Restrict CORS origins to trusted frontend deployments.
- Use secure WebSocket transport (`wss://`) behind TLS in production.
- Review token storage and rotation policies before exposing the service publicly.

## Technologies

| Technology | Purpose |
| :--- | :--- |
| **Java 17** | Application language and target runtime. |
| **Spring Boot 4.0.8** | Application framework. |
| **Spring GraphQL** | GraphQL API and subscriptions. |
| **Spring WebSocket** | Real-time GraphQL subscription transport. |
| **Spring Security** | Authentication and authorization. |
| **Spring Data JPA / Hibernate** | Persistence and ORM. |
| **PostgreSQL** | Relational database. |
| **JJWT 0.11.5** | JWT creation and validation. |
| **BCrypt** | Password hashing. |
| **Resilience4j** | Rate limiting support. |
| **Docker** | Containerization. |

## Project Status

This repository contains the **backend service** of the Secure Chat Application. The backend provides the core GraphQL API, authentication, group authorization, encrypted-data persistence, key-version management, and real-time message delivery required by the client application.
