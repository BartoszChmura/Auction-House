# Auction House

## Description
**Auction House** is a web application based on an auction platform concept, developed primarily using the Spring Boot framework. It allows users to create, manage, and bid on auctions. The application provides full support for user account management, bidding mechanisms, integration with payment systems, and advanced security features. This repository pertains to the backend portion of the application.

## Features
- User registration and authentication using JWT tokens
- Auction creation and management
- Bidding system
- Integration with external payment systems

## Technologies Used

### Technology Stack
- **Java**
- **Spring/Spring Boot**
- **Hibernate**
- **PostgreSQL**

### Testing Tools
- **JUnit**
- **Mockito**
- **H2 Database**
- **Spring Boot Test**

### Auxiliary Tools
- **Maven**
- **Git**
- **JWT**
- **Ngrok**

## Getting Started

### Requirements
- **Java 17+**
- **Maven**
- **PostgreSQL**
- **Git**

### Installation

1. **Clone the repository:**
    ```bash
    git clone https://github.com/BartoszChmura/auction-house.git
    cd auction-house
    ```

2. **Configure the PostgreSQL database:**
    - Create a database named `AuctionHouse`.
    - Configure the `application.properties` file with the database access credentials.
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/AuctionHouse
    spring.datasource.username=postgres
    spring.datasource.password=123
    spring.jpa.show-sql=true
    spring.jpa.hibernate.ddl-auto=update
    ```

3. **Build and run the application:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

4. **Access the API:**
    The service will be available on port 8080 by default.

### API Endpoints Documentation:
You can view and test the available endpoints through Swagger UI at: [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)

### Auction House Interface

A simple web interface demonstrating the application's functionality can be run locally: [Auction House Interface Repository](https://github.com/BartoszChmura/Auction-House-Interface).

### Payment System Integration

---To properly utilize the external payment system, you will need a tool to expose your localhost to the web, such as Ngrok.---
