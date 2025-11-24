Spring Boot item CRUD with security and H2
A clean, end-to-end Spring Boot application demonstrating modular CRUD, role-based access control (USER/ADMIN), MDC-based logging, and an H2 in-memory database. Built to be readable, testable, and a strong foundation for real-world backend development.
Overview
This project implements:
- Item management: Full CRUD endpoints for items.
- User management: Create and list users for authentication/authorization.
- Security: HTTP Basic auth with role-based access rules (USER/ADMIN) and a custom UserDetailsService backed by JPA.
- Database: H2 in-memory DB (with optional file persistence).
- Logging: MDC tagging with authenticated username in logs.
- Developer experience: H2 console, SQL logging, and clear error messages.

Features
- CRUD endpoints: Create, read, update, delete items via REST.
- Role-based access: Protects endpoints with hasRole("USER") and hasRole("ADMIN").
- User registration: Public endpoint to create users with roles (ROLE_USER, ROLE_ADMIN).
- MDC logging: Adds user into MDC for per-request log tagging.
- H2 console: Inspect data live during development.
- Extensible architecture: Controllers → Services → Repositories → Entities.

Project structure
- Config:
- SecurityConfig — Security filter chain, MDC logging filter, UserDetailsService, and password encoder.
- Controller:
- AppUserController — Endpoints to create and manage users.
- ItemController — Endpoints to manage items (CRUD).
- Service:
- AppUserService, ItemService — Business logic and validations.
- Repository:
- AppUserRepository, ItemRepository — Spring Data JPA interfaces.
- Model:
- AppUser — JPA entity for users.
- Item — JPA entity for items.
- Resources:
- application.properties — H2 configuration, Hibernate, logging.

Security
Authentication and authorization
- Auth type: HTTP Basic.
- User source: Database via UserDetailsService loading AppUser.
- Password encoder: NoOpPasswordEncoder (plain text for dev only).
- Roles: Must be stored as ROLE_USER or ROLE_ADMIN in DB.
- Access rules:
- POST /users — Permit all (register).
- GET /users, GET /users/{id}, DELETE /users/{id} — Admin only.
- /items/** — USER or ADMIN.
MDC request tagging
- Filter: Adds MDC.put("user", <username>) when authenticated.
- Benefit: Every log line can include the authenticated user for traceability.
Configuration
application.properties (H2 in-memory)
spring.application.name=ItemDataLoad

# H2 Database settings
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA settings
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Enable H2 console
spring.h2.console.enabled=true

spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false

# App logs
logging.level.com.example.demo=INFO

# SQL logs (optional)
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
Optional: Persist H2 to file
spring.datasource.url=jdbc:h2:file:./data/itemdb


- 
spring.datasource.url=jdbc:h2:file:./data/itemdb

- Keeps data between restarts (creates ./data/itemdb.mv.db).

Entities
AppUser
- Fields:
- id: Long (PK)
- username: unique, not null
- password: plain text (dev only)
- role: ROLE_USER or ROLE_ADMIN
Item
- Fields:
- id: Long (PK)
- name: not null
- description: optional
- price: numeric

Key classes
SecurityConfig (core segments)
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .addFilterBefore((request, response, chain) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                MDC.put("user", auth.getName());
            }
            chain.doFilter(request, response);
            MDC.clear();
        }, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/items/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/users").permitAll()
            .requestMatchers("/users/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .httpBasic();
    return http.build();
}

@Bean
public UserDetailsService userDetailsService() {
    return username -> {
        AppUser user = appUserRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().replace("ROLE_", "")) // ROLE_ADMIN -> ADMIN
            .build();
    };
}

@Bean
public static NoOpPasswordEncoder passwordEncoder() {
    return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
}


AppUserController (core segments)
@RestController
@RequestMapping("/users")
public class AppUserController {

    @PostMapping
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        return ResponseEntity.ok(appUserService.saveUser(user));
    }

    @GetMapping
    public List<AppUser> getAllUsers() {
        return appUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public AppUser getUserById(@PathVariable Long id) {
        return appUserService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        appUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}



How to run
- Prerequisites: Java 17+, Maven, Git (optional).
- Start app:
- From IDE: Run the Spring Boot main class.
- From terminal:
mvn spring-boot:run
- H2 console:
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (leave blank)

Quick start: create users and test endpoints
Create admin and user (permit all)
- POST /users
{
  "username": "admin",
  "password": "adminpass",
  "role": "ROLE_ADMIN"
}


{
  "username": "user",
  "password": "userpass",
  "role": "ROLE_USER"
}


Authenticate in Postman
- Auth tab: Basic Auth
- admin: admin / adminpass
- user: user / userpass
Items (requires USER or ADMIN)
- GET /items — list items
- POST /items — add item
{
  "name": "Notebook",
  "description": "200 pages",
  "price": 99.0
}


- PUT /items/{id} — update item
- DELETE /items/{id} — delete item (ADMIN only if configured)

Logging and observability
- MDC user tag: Logs include the authenticated user when available.
- SQL logs: Enabled for visibility during development.
- Tip: Configure logback to print %X{user} in your pattern for MDC.
Example logback pattern snippet:
<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - user=%X{user} %msg%n</pattern>



Common pitfalls and fixes
- 401 on protected endpoints:
- Cause: No auth or wrong credentials.
- Fix: Use Basic Auth with a user existing in DB.
- 401 on /users POST:
- Cause: Path mismatch or security rule not permitting.
- Fix: Ensure .requestMatchers("/users").permitAll() is present.
- Role issues:
- Cause: Stored role is ADMIN instead of ROLE_ADMIN.
- Fix: Always store roles as ROLE_*.
- MDC NPE:
- Cause: Accessing auth.getName() when unauthenticated.
- Fix: Null check before MDC put (already added).
- H2 data disappears after restart:
- Cause: In-memory DB.
- Fix: Switch to jdbc:h2:file:./data/itemdb for persistence.

Git basics (for this repo)
- Initial push:
git init
git add .
git commit -m "initial commit"
git branch -M main
git remote add origin https://github.com/Hemanthsivasai-2074/springboot-itemdb.git
git push -u origin main


- Update code:
git add .
git commit -m "update: items, security, logging"
git push



Roadmap
- Switch to BCryptPasswordEncoder for production safety.
- Add JWT for stateless auth.
- Add validation annotations in DTOs and global exception handling.
- Add Swagger/OpenAPI for API documentation.
- Add tests (unit/integration) for controllers and services.
- Add logback configuration for structured JSON logs.

License
- License: MIT (optional). Add LICENSE if you want open-source usage.

Credits
- Author: Hemanth Sivasai
- Stack: Spring Boot, Spring Security, Spring Data JPA, H2, Maven
If you want me to tailor this README further to your exact package names, endpoints, or add screenshots (H2 console, Postman), say the word and I’ll refine it to perfection.
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .addFilterBefore((request, response, chain) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                MDC.put("user", auth.getName());
            }
            chain.doFilter(request, response);
            MDC.clear();
        }, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/items/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/users").permitAll()
            .requestMatchers("/users/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .httpBasic();
    return http.build();
}

@Bean
public UserDetailsService userDetailsService() {
    return username -> {
        AppUser user = appUserRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().replace("ROLE_", "")) // ROLE_ADMIN -> ADMIN
            .build();
    };
}

@Bean
public static NoOpPasswordEncoder passwordEncoder() {
    return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
}

AppUserController (core segments)
@RestController
@RequestMapping("/users")
public class AppUserController {

    @PostMapping
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        return ResponseEntity.ok(appUserService.saveUser(user));
    }

    @GetMapping
    public List<AppUser> getAllUsers() {
        return appUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public AppUser getUserById(@PathVariable Long id) {
        return appUserService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        appUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}


How to run
- Prerequisites: Java 17+, Maven, Git (optional).
- Start app:
- From IDE: Run the Spring Boot main class.
- From terminal:
mvn spring-boot:run
- 
mvn spring-boot:run
- H2 console:
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (leave blank)



Quick start: create users and test endpoints
Create admin and user (permit all)
- POST /users
{
  "username": "admin",
  "password": "adminpass",
  "role": "ROLE_ADMIN"

}

{
  "username": "user",
  "password": "userpass",
  "role": "ROLE_USER"
}

Authenticate in Postman
- Auth tab: Basic Auth
- admin: admin / adminpass
- user: user / userpass
Items (requires USER or ADMIN)
- GET /items — list items
- POST /items — add item

{
  "name": "Notebook",
  "description": "200 pages",
  "price": 99.0
}

- PUT /items/{id} — update item
- DELETE /items/{id} — delete item (ADMIN only if configured)

Logging and observability
- MDC user tag: Logs include the authenticated user when available.
- SQL logs: Enabled for visibility during development.
- Tip: Configure logback to print %X{user} in your pattern for MDC.
Example logback pattern snippet:
<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - user=%X{user} %msg%n</pattern>



Common pitfalls and fixes
- 401 on protected endpoints:
- Cause: No auth or wrong credentials.
- Fix: Use Basic Auth with a user existing in DB.
- 401 on /users POST:
- Cause: Path mismatch or security rule not permitting.
- Fix: Ensure .requestMatchers("/users").permitAll() is present.
- Role issues:
- Cause: Stored role is ADMIN instead of ROLE_ADMIN.
- Fix: Always store roles as ROLE_*.
- MDC NPE:
- Cause: Accessing auth.getName() when unauthenticated.
- Fix: Null check before MDC put (already added).
- H2 data disappears after restart:
- Cause: In-memory DB.
- Fix: Switch to jdbc:h2:file:./data/itemdb for persistence.

Git basics (for this repo)
- Initial push:
git init
git add .
git commit -m "initial commit"
git branch -M main
git remote add origin https://github.com/Hemanthsivasai-2074/springboot-itemdb.git
git push -u origin main


- Update code:
git add .
git commit -m "update: items, security, logging"
git push



Roadmap
- Switch to BCryptPasswordEncoder for production safety.
- Add JWT for stateless auth.
- Add validation annotations in DTOs and global exception handling.
- Add Swagger/OpenAPI for API documentation.
- Add tests (unit/integration) for controllers and services.
- Add logback configuration for structured JSON logs.

License
- License: MIT (optional). Add LICENSE if you want open-source usage.

Credits
- Author: Hemanth Sivasai
- Stack: Spring Boot, Spring Security, Spring Data JPA, H2, Maven
If you want me to tailor this README further to your exact package names, endpoints, or add screenshots (H2 console, Postman), say the word and I’ll refine it to perfection.

Logging and observability
- MDC user tag: Logs include the authenticated user when available.
- SQL logs: Enabled for visibility during development.
- Tip: Configure logback to print %X{user} in your pattern for MDC.
Example logback pattern snippet:
<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - user=%X{user} %msg%n</pattern>

Common pitfalls and fixes
- 401 on protected endpoints:
- Cause: No auth or wrong credentials.
- Fix: Use Basic Auth with a user existing in DB.
- 401 on /users POST:
- Cause: Path mismatch or security rule not permitting.
- Fix: Ensure .requestMatchers("/users").permitAll() is present.
- Role issues:
- Cause: Stored role is ADMIN instead of ROLE_ADMIN.
- Fix: Always store roles as ROLE_*.
- MDC NPE:
- Cause: Accessing auth.getName() when unauthenticated.
- Fix: Null check before MDC put (already added).
- H2 data disappears after restart:
- Cause: In-memory DB.
- Fix: Switch to jdbc:h2:file:./data/itemdb for persistence.

Git basics (for this repo)
- Initial push:
git init
git add .
git commit -m "initial commit"
git branch -M main
git remote add origin https://github.com/Hemanthsivasai-2074/springboot-itemdb.git
git push -u origin main

- Update code:

git add .
git commit -m "update: items, security, logging"
git push

Roadmap
- Switch to BCryptPasswordEncoder for production safety.
- Add JWT for stateless auth.
- Add validation annotations in DTOs and global exception handling.
- Add Swagger/OpenAPI for API documentation.
- Add tests (unit/integration) for controllers and services.
- Add logback configuration for structured JSON logs.

License
- License: MIT (optional). Add LICENSE if you want open-source usage.

Credits
- Author: Hemanth Sivasai
- Stack: Spring Boot, Spring Security, Spring Data JPA, H2, Maven
If you want me to tailor this README further to your exact package names, endpoints, or add screenshots (H2 console, Postman), say the word and I’ll refine it to perfection.



