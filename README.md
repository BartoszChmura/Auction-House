# Auction House

## Opis
Auction House to aplikacja webowa na zasadach portalu aukcyjnego, opracowana głównie za pomocą frameworku Spring Boot, która umożliwia użytkownikom tworzenie, zarządzanie i licytowanie aukcji. Aplikacja oferuje pełne wsparcie dla zarządzania kontami użytkowników, mechanizmy licytacji, integrację z systemami płatności oraz zaawansowane funkcje zabezpieczeń. Repozytorium dotyczy części backendowej aplikacji.

## Funkcje
- Rejestracja i uwierzytelnianie użytkowników za pomocą tokena JWT
- Tworzenie i zarządzanie aukcjami
- System licytacji
- Integracja z zewnętrznym systemem płatności

## Wykorzystane Technologie

### Główny Stack Technologiczny
- **Java**
- **Spring/Spring Boot**
- **Hibernate**
- **PostgreSQL**

### Narzędzia do testowania
- **JUnit**
- **Mockito**
- **H2 Database**
- **Spring Boot Test***

### Narzędzia pomocnicze
- **Maven**
- **Git**
- **JWT**
- **Ngrok**

## Pierwsze Kroki

### Wymagania
- **Java 17+**
- **Maven**
- **PostgreSQL**
- **Git**

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
    Serwis domyślnie będzie dostępny na porcie 8080.

### System płatności

---Do poprawnego działania zewnętrznego systemu płatności wymagane będzie wykorzystanie narzędzia do przekierowania localhosta na domenę, np. ngrok.---

