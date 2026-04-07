# AGENTS.md - AI Coding Guide for ExpenseTracker

## Project Overview
ExpenseTracker is a Spring Boot REST API for personal expense management with JWT authentication and role-based access control (ADMIN/USER roles). Users can create, retrieve, filter, and delete expenses organized by date and category.

### Tech Stack
- **Framework**: Spring Boot 4.1.0-M2 (latest milestone)
- **Security**: Spring Security + JWT (JJWT 0.12.6) for stateless authentication
- **Data**: Spring Data JPA with MySQL (dialect: MySQLDialect)
- **ORM**: Hibernate (auto DDL: update)
- **Build**: Gradle with Lombok annotation processor
- **Port**: 8080

---

## Architecture Patterns

### Service-Controller-Repository Pattern
The codebase strictly follows a **3-layer architecture**:

1. **Controllers** (`/controller`): Handle HTTP requests, extract `Authentication` object to get username
   - `ExpenseController`: REST CRUD endpoints for `/expenses`
   - `AuthController`: Auth endpoints (`/signup`, `/login`)
   - All controllers use `@CrossOrigin(origins = "*")` for CORS

2. **Services** (interface + `Impl`): Business logic layer
   - Interface defines contract, `Impl` provides implementation
   - Example: `ExpenseService` → `ExpenseServiceImpl` (handles queries, filtering)
   - Services inject repositories and other services via constructor

3. **Repositories**: Spring Data JPA interfaces
   - `ExpenseRepository`: extends `JpaRepository<Expense, Long>`
   - `UserRepository`: extends `JpaRepository<AppUser, Long>`
   - Custom finder methods use `findByXxx` naming conventions

**When adding features**: Create service interface first, then implement in `Impl` class. Always inject via constructor, never use `@Autowired` fields.

### Data Isolation Pattern (User Context)
Controllers **always extract the authenticated username** and pass it through service methods to filter data by user:

```java
// Pattern used in ExpenseController:
String username = authentication.getName();
AppUser user = userService.findByUsename(username);  // Note: typo in method name!
return expenseService.getExpenseByDate(date, user.getId());  // Pass userId
```

**Critical**: Repository methods require `userId` parameter because expenses are user-scoped. See `ExpenseServiceImpl.getExpenseByDate()` - it filters by both date AND userId.

### Security & JWT Flow
1. **Stateless sessions**: `SessionCreationPolicy.STATELESS` in `SecurityConfig`
2. **JWT token generation**: `JwtUtil.generateToken(username)` returns 10-hour validity tokens
3. **Filter chain**: `JwtAuthFilter` extracts token from request headers before `UsernamePasswordAuthenticationFilter`
4. **Protected endpoints**: `/expenses/**` and `/admin/**` require valid JWT; `/signup`, `/login` are public
5. **Token payload**: Username is stored as JWT subject (extractable via `extractUsername()`)

**Important**: JWT secret key is randomly generated at runtime (`Jwts.SIG.HS256.key().build()`). Do not add hardcoded secrets for production.

---

## Project-Specific Conventions

### Method Naming Issue
`UserService.findByUsename()` has a typo (should be `findByUsername`), but it's used throughout the codebase. **Preserve this spelling when refactoring** to avoid breaking existing calls in controllers.

### Entity Relationships
- **AppUser ↔ Expense**: OneToMany with `cascade = CascadeType.ALL` and `orphanRemoval = true`
  - Deleting an expense automatically updates the user's expense list
  - Always set `expense.setUser(user)` before saving new expenses

### DTO Layer
DTOs are used for API contracts only (not persisted):
- `AppUserDTO`: User registration data (fullName, username, password)
- `AuthDTO`: Login credentials (username, password)
- `AuthResponseDTO`: API response (message, token, status)

### Expense Entity Fields
- `expenseType`: Integer type code (semantic meaning not documented—check usage)
- `date`: String format (appears to be "YYYY-MM-DD" based on filtering in `ExpenseServiceImpl`)
- `category`, `account`, `note`: String fields for classification

---

## Key Integration Points

### Authentication Flow
1. POST `/signup` → `AuthController.signup()` → `AuthService.registerUser(AppUserDTO)` → creates AppUser with `Role.USER`
2. POST `/login` → `AuthController.login()` → `AuthService.loginUser(AuthDTO)` → returns JWT token
3. Subsequent requests: Include token in `Authorization: Bearer <token>` header

### Expense CRUD Operations
- **Create**: `POST /expenses` (sets authenticated user automatically)
- **Read**: `GET /expenses`, `GET /expenses/{id}`, `GET /expenses/day/{date}`, `GET /expenses/category/{category}/month?month=YYYY-MM`
- **Update**: `PUT /expenses/{id}` (validates ownership via userId)
- **Delete**: `DELETE /expenses/{id}` (soft delete pattern: checks ownership then deletes)

### Admin Endpoints
Protected by `hasRole("ADMIN")` in `SecurityConfig`. Location of admin endpoints: `AdminController` (not shown in analysis, but exists in codebase).

---

## Common Development Tasks

### Building & Running
```bash
./gradlew build          # Full build with tests
./gradlew bootRun        # Run application on port 8080
./gradlew clean build    # Clean rebuild
```

### Database Setup
- Application expects MySQL server at `localhost/expensedb`
- Credentials: `root` / `Pas$4MyLocalSQL` (configured in `application.yml`)
- Hibernate DDL is set to `update` (auto-creates/modifies schema on startup)

### Adding New Endpoints
1. Create method in service interface (e.g., `ExpenseService`)
2. Implement in `ExpenseServiceImpl` (inject `ExpenseRepository`)
3. Add controller method in `ExpenseController` with `Authentication` parameter
4. Extract username → get user → call service with userId

### Querying Expenses by User
Use repository methods that accept `userId` parameter:
- `ExpenseRepository.findByUserIdOrderByDateDesc(userId)`: Get all user expenses sorted by date
- Filter results in service layer using Java streams (`.filter()`, `.distinct()`, `.toList()`)

---

## Data Files & Initialization
- `application.yml`: Server config, datasource, JPA/Hibernate settings
- `expenses.json`: Sample data file in resources (reference only, not auto-loaded by default—see commented code in `Main.java`)
- `DataInitializer`: Creates default admin user (`admin`/`admin123`) on startup if not exists

---

## Testing Notes
- Test framework: JUnit 5 (Jupiter) configured in `build.gradle`
- Test location: `src/test/java` (minimal tests currently)
- Run tests: `./gradlew test`

---

## Common Gotchas & Edge Cases

1. **User Isolation**: Every expense query must pass `userId` to prevent unauthorized access. Check all service calls validate user ownership.
2. **Token Expiration**: JWT tokens expire after 10 hours. Implement refresh token logic if needed.
3. **Missing Validation**: No validation annotations on DTOs—add `@NotNull`, `@NotBlank` if validation is needed.
4. **Lazy Loading**: Expense entity uses `FetchType.LAZY` for user relation—access user only when needed to avoid N+1 queries.
5. **CORS Enabled**: `@CrossOrigin(origins = "*")` allows all origins—restrict in production.
6. **Password Encoding**: Always use `PasswordEncoder.encode()` before saving passwords; BCryptPasswordEncoder is configured.

---

## File Reference Guide

| File | Purpose |
|------|---------|
| `SecurityConfig.java` | JWT filter chain, endpoint authorization, session policy |
| `JwtUtil.java` | JWT token generation/validation/extraction logic |
| `AuthService(Impl).java` | User registration & login orchestration |
| `ExpenseService(Impl).java` | Expense CRUD business logic with user isolation |
| `ExpenseController.java` | REST endpoints for expense operations |
| `AppUser.java` | User entity with role and expense relation |
| `Expense.java` | Expense entity with date, category, amount fields |
| `application.yml` | Server port, DB connection, Hibernate config |


