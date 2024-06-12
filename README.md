# Auction House

## Przegląd
Auction House to aplikacja webowa na zasadach portalu aukcyjnego, opracowana przy użyciu Spring Boot. Aplikacja obsługuje wszystkie operacje związane z aukcjami, takie jak zarządzanie użytkownikami, tworzenie i obsługa aukcji, licytacje i przetwarzanie transakcji. Repozytorium dotyczy jedynie części backendowej.

## Funkcje
- Rejestracja i uwierzytelnianie użytkowników
- Tworzenie i zarządzanie aukcjami
- System licytacji
- Obsługa transakcji
- RESTful API

## Wykorzystane Technologie
- **Java**
- **Spring Boot**
- **PostgreSQL**
- **Hibernate**
- **Spring Security**
- **Git**

## Pierwsze Kroki

### Wymagania
- Java 17+
- Maven
- PostgreSQL
- Git

### Instalacja

1. **Sklonuj repozytorium:**
    ```bash
    git clone https://github.com/BartoszChmura/auction-house.git
    cd auction-house
    ```

2. **Skonfiguruj bazę danych PostgreSQL:**
    - Utwórz bazę danych o nazwie `AuctionHouse`.
    - Skonfiguruj plik `application.properties` z danymi dostępowymi do bazy danych.
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/AuctionHouse
    spring.datasource.username=postgres
    spring.datasource.password=123
    spring.jpa.show-sql=true
    spring.jpa.hibernate.ddl-auto=update
    ```

3. **Zbuduj i uruchom aplikację:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

4. **Dostęp do API:**
    Serwis backendowy będzie dostępny pod adresem `http://localhost:8080`.

### System płatności

---Do poprawnego działania systemu płatności wymagane będzie wykorzystanie narzędzia do przekierowania localhosta na domenę, np. ngrok.---

