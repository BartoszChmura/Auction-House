# Auction House

## Description
Auction House is a web-based auction platform developed using the Java Spring Boot framework. The application allows users to create, manage, and participate in auctions. It provides robust features such as user account management, real-time bidding, integration with payment gateways, and advanced security mechanisms. This repository contains the backend code of the application.

## Features
- User registration and authentication via JWT tokens
- Creation and management of auctions
- Real-time bidding system
- Integration with external payment services
- Secure user sessions and data handling

## Technologies Used

### Core Technologies
- **Java**
- **Spring/Spring Boot**
- **Hibernate**
- **PostgreSQL**

### Testing Tools
- **JUnit**
- **Mockito**
- **H2 Database**
- **Spring Boot Test**

### Additional Tools
- **Maven**
- **Git**
- **JWT (JSON Web Tokens)**
- **Ngrok**

## Getting Started

### Prerequisites
To run this project, ensure that you have the following software installed:
- **Java 17+**
- **Maven**
- **PostgreSQL**
- **Git**

### Installation

1. **Clone the repository:**
    ```bash
    git clone https://github.com/BartoszChmura/auction-house.git
    ```

2. **Configure the PostgreSQL database:**
    - Create a PostgreSQL database named `AuctionHouse`.
    - Edit the `application.properties` file to include your database credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/AuctionHouse
    spring.datasource.username=postgres
    spring.datasource.password=your_password
    spring.jpa.show-sql=true
    spring.jpa.hibernate.ddl-auto=update
    ```

3. **Build and run the application:**
    - Compile and package the application:
      ```bash
      mvn clean install
      ```
    - Start the application using Spring Boot:
      ```bash
      mvn spring-boot:run
      ```

4. **Access the API:**
    The application will be available by default at `http://localhost:8080`.

### Auction House Frontend

A simple web interface that demonstrates the functionality of the application is available here: [Auction House Interface](https://github.com/BartoszChmura/Auction-House-Interface). Follow the instructions in that repository to run the frontend locally.

### Payment System Integration

For integrating external payment systems, a tool like **Ngrok** is required to expose your local application to the internet for testing payment callbacks and other external interactions. Ngrok creates a secure tunnel from a public URL to your local machine, allowing external systems to communicate with your local environment.

## Development and Testing

To run the tests, use the following command:
```bash
mvn test

