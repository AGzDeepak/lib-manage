# Library Management System (Java + MySQL)

This is a Spring Boot web application for managing:
- Books
- Members
- Loans (issue and return flow)

## Live Demo
- Live Link: `https://lib-manage.onrender.com`
- Live link : 'https://lib-manage-402a.onrender.com'


## Tech Stack
- Java 21+
- Spring Boot
- Spring MVC + Thymeleaf
- Spring Data JPA (Hibernate)
- MySQL

## Features
- Dashboard with quick stats and recent loans
- Book CRUD (add/edit/delete)
- Member CRUD (add/edit/delete)
- Issue a book to a member with due date
- Return a book and update inventory automatically
- Overdue status is auto-updated on dashboard/loan views
- Login page with role-based access (`ADMIN`, `USER`)

## Database Profiles
The app now supports two profiles:
- `h2` (default): runs without MySQL
- `mysql`: uses local MySQL

## Configure MySQL
MySQL settings are in `src/main/resources/application-mysql.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

Update username/password as needed for your local MySQL.

## Run the App
Use Maven wrapper (no global Maven install required):

```bash
mvnw.cmd spring-boot:run
```

Open:
`http://localhost:8081`

Default users:
- Admin: `admin / admin123` (full access)
- User: `user / user123` (dashboard and list pages)

To run with MySQL profile:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

## Deploy (Render)
This repo is pre-configured for Render with `Dockerfile` and `render.yaml`.

Steps:
1. Click the `Deploy to Render` button above.
2. Authorize Render and create the web service.
3. Open the generated Render URL and update the `Live Link` line in this README if Render assigns a different subdomain.

## Test

```bash
mvnw.cmd test
```

Tests use in-memory H2 database from `src/test/resources/application.properties`.
