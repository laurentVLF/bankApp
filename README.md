# Bank Transaction Management System

## Table of Contents

- [Description](#description)
- [Installation](#installation)
- [Running Project](#running-project)
- [Running Tests](#running-tests)
- [Project Architecture](#project-architecture)
- [Technologies used](#technologies-used)

## Description

This project implements a banking transaction management system, which allows users to:
- Make deposits and withdrawals from their bank accounts.
- View their transaction history.

The system is developed in Kotlin with Ktor for the API, Koin for the injection, and uses MockK for unit testing. 
It also applies the principles of Domain-Driven Design (DDD) for the project architecture.

### EndPoints

   -  POST /api/v1/bank-account/{accountNumber}/transaction/deposit : Make a deposit..
   -  POST /api/v1/bank-account/{accountNumber}/transaction/withdraw : Make a withdraw.
   -  GET /api/v1/bank-account/{accountNumber}/transaction/historic : Get the transactions historic.

## Installation

### Prerequisite

- [JDK 17](https://adoptium.net/temurin/releases) ou version supérieure.
- [Gradle 8.x](https://gradle.org/install/)

### Steps installation

1. Clone repository :
   ```bash
   git clone https://github.com/laurentVLF/bankApp.git
   cd bankApp
   
2. Installing dependencies :
   ./gradlew build

## Running project

./gradlew run

## Running Tests

### Unit Tests

   ./gradlew test
   
### Integration Tests

   ./gradlew cucumber

## Project architecture
```plaintext
   src
   ├── main
   │   ├── kotlin
   │   │   ├── fr.bank
   │   │   │   ├── api                 # API Rest (Ktor, koin)
   │   │   │   ├── domain              # Business domaine (Aggregate, Value Objects, interface)
   │   │   │   └── infrastructure      # Repository Implementations
   │   └── resources
   │       └── logback.xml             # Logs file configuration
   └── test
      ├── kotlin
      │   ├── fr.bank
      │   │   ├── steps                 # Cucumber Tests
      │   │   └── service               # Service Tests
      └── resources
          └── features                  # Scenaries Gherkin for Cucumber
```
## Technologies Used

   - Kotlin: Main language of the project.
   - Ktor: Framework for creating REST APIs.
   - Koin: Dependency injection framework.
   - MockK: Mock framework for unit tests.
   - JUnit 5: Test framework.
   - Cucumber: Behavioral testing framework (BDD).
