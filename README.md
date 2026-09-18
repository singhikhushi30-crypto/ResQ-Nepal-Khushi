# ResQ Nepal — Disaster Management & Rescue Coordination System

## Overview

ResQ Nepal is a Java desktop application that we built as a simulation of a disaster-response coordination system. The main idea was to see how a central team could manage several rescue requests when the number of rescue teams, equipment, and shelter spaces is limited.

The application keeps information about victims, rescue requests, rescue teams, resources, shelters, missing persons, and rescue operations in one place. It also uses the severity of a request and the location of available teams to decide which request should be handled and which team is suitable.

This is a college-level educational project. The data used in the application is fictional and the system is not connected to any real emergency service.

## Problem Statement

During a major disaster, many people may need help at the same time. Since the available teams and resources are limited, every request cannot simply be handled in the order in which it arrives.

ResQ Nepal tries to simulate this situation. A rescue request is given a severity level and placed in a `PriorityQueue`. When the request is processed, the system looks for a team that is available, has the required specialization, and is reasonably close to the incident. Resources and shelter capacity are also checked before they are used.

## Features

- **Dashboard**: Shows useful information such as rescue activity, team availability, and shelter capacity.
- **Rescue Requests**: Allows victims to be registered and rescue requests to be created. Requests are then placed in the priority queue according to their severity.
- **Priority Processing**: Uses a Java `PriorityQueue` with a custom `Comparator` to decide which rescue request should be processed first.
- **Rescue Teams**: Stores information about teams, their members, locations, availability, and specialization.
- **Resources**: Keeps track of rescue equipment and supports allocating and releasing resources.
- **Shelters**: Stores shelter information and checks the available capacity before admitting evacuated people.
- **Missing Persons**: Maintains records of missing people and allows their status to be updated.
- **Rescue Operations**: Keeps track of ongoing and completed rescue operations.
- **Case Study Simulation**: Provides fictional disaster scenarios so that the working of the system can be demonstrated without using real emergency data.

## Architecture

We used a JavaFX MVC structure with separate service and database layers. This makes it easier to keep the user interface, application rules, and database work separate.

- **View**: FXML files are used to create the JavaFX screens.
- **Controller**: Controllers receive user actions, validate input, and update the screens. They do not directly contain the main business logic or SQL operations.
- **Service Layer**: This is where the main rules are handled, including request prioritization, team assignment, resource checks, and shelter-capacity checks.
- **DatabaseManager**: A central JDBC utility that communicates with the SQLite database. It uses `try-with-resources` and enables `PRAGMA foreign_keys = ON`.
- **Database**: The application stores its data locally in `data/resqnepal.db`.

## Java Concepts Used

- **Object-Oriented Programming**: We used models to represent the different entities and services to keep the application logic organized.
- **Data Structures**: A `PriorityQueue` and custom `Comparator` are used to process rescue requests according to severity.
- **JavaFX**: The interface uses event-driven programming with JavaFX and FXML.
- **JDBC / SQLite**: JDBC is used to connect the Java application with the SQLite database and execute SQL queries using `PreparedStatement`.
- **Exception Handling**: Custom exceptions such as `InvalidRequestException` and `NoTeamAvailableException` are used for situations that need specific error handling.
- **Unit Testing**: JUnit 5 is used to test the important business rules separately from the graphical interface.

## Technologies

- Java 17
- JavaFX 17.0.2
- SQLite (`sqlite-jdbc`)
- Maven
- JUnit 5

## Database

The application uses a local SQLite database. The main tables are:

- `victims`
- `rescue_requests`
- `rescue_teams`
- `rescue_operations`
- `resources`
- `shelters`
- `missing_persons`
- `disasters`

## How to Run

### Prerequisites

Make sure JDK 17 or newer and Maven are installed.

### Steps

1. Clone or download the repository.
2. Open a terminal in the project folder.
3. Start the application with:

```bash
mvn javafx:run
```

## How to Test

The project contains 23 automated JUnit tests covering important parts of the system, including priority processing, team assignment, resource limits, and shelter-capacity rules.

Run the tests with:

```bash
mvn clean test
```

## Project Structure

```text
src/
├── main/
│   ├── java/com/resqnepal/
│   │   ├── Main.java                 # Application entry point
│   │   ├── AppContext.java           # Shared services for JavaFX screens
│   │   ├── controller/               # JavaFX controllers
│   │   ├── exception/                # Custom exceptions
│   │   ├── model/                    # Domain/model classes
│   │   ├── repository/               # DatabaseManager
│   │   ├── service/                  # Business logic
│   │   └── util/                     # Utility classes such as DistanceUtil
│   └── resources/com/resqnepal/      # FXML view files
└── test/
    └── java/com/resqnepal/service/   # JUnit tests
```

## Disclaimer

ResQ Nepal is an educational simulation created for a university project. The information and scenarios shown by the application are fictional and should not be treated as official Nepal disaster statistics or as a real emergency-response deployment.
