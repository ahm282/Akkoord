# Akkoord

A structured document management application built with Spring Boot.

## Overview

Akkoord is a comprehensive document management system designed to streamline the process of creating, storing, searching, and collaborating on structured documents. The application provides a secure and efficient way to manage document workflows in professional environments.

## Features

-   **Secure Authentication**: JWT-based authentication and authorization system
-   **Document Management**: Create, read, update, and delete structured documents
-   **Version Control**: Track changes and maintain document history
-   **RESTful API**: Well-defined API for integration with other systems

## Technology Stack

-   **Backend**: Java 17 with Spring Boot
-   **Database**: PostgreSQL
-   **Security**: Spring Security with JWT authentication
-   **Build Tool**: Maven

## Getting Started

### Prerequisites

-   Java 17 or higher
-   Maven 3.6 or higher
-   PostgreSQL 12 or higher
-   Docker (optional, for containerized database)

### Database Setup

You can run PostgreSQL locally or use Docker:

```bash
docker run --name akkoord-postgres \
  -e POSTGRES_USER=username \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=db \
  -p 5432:5432 \
  -d postgres:latest
```

### Running the Application

Clone the repository:

```bash
git clone https://github.com/yourusername/Akkoord.git
cd Akkoord
```

Run the application with the dev profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will be available at `http://localhost:8081`

### Build for Production

```bash
./mvnw clean package -P prod
```

## Project Structure

The project follows a standard Spring Boot application structure:

-   `src/main/java/com/ahm282/Akkoord/`: Java source files
    -   `config/`: Application configuration
    -   `controller/`: REST controllers
    -   `dto/`: Data Transfer Objects
    -   `model/`: Domain models and entities
    -   `repository/`: Data access layer
    -   `security/`: Security configuration and JWT implementation
    -   `service/`: Business logic

## Configuration

The application uses profile-specific properties files:

-   `application.properties`: Default configuration
-   `application-dev.properties`: Development environment configuration
-   `application-prod.properties`: Production environment configuration (not included in repository)
