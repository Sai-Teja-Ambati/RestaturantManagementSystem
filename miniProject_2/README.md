# Restaurant Management System

Comprehensive restaurant backend system managing orders, inventory, table bookings, and user roles with robust authentication and authorization.

---

## 🚀 Quick Start with Docker

### Prerequisites
- Docker and Docker Compose installed (or Rancher Desktop)
- Git installed

### Initial Setup and Build
For the **first-time build** or if you want to rebuild images, run:

```docker-compose --profile migrations up --build```


This command builds Docker images, launches PostgreSQL, runs database migrations, and starts the backend app.

### Starting Containers (No Rebuild)
For **subsequent runs** without the need to rebuild images, simply run:

```docker-compose --profile migrations up```


### Stopping Containers
Gracefully stop all running containers:

```docker-compose down```


### Stopping and Cleaning Up
To stop running containers **and remove associated volumes and orphaned containers**, run:

```docker-compose down -v --remove-orphans```


---

# README

## Restaurant Management System

A fully featured backend system for restaurant management enabling secure, role-based control over users, orders, inventory, tables, and bookings.

### Table of Contents
- [Installation](#installation)
- [Usage](#usage)
- [System Architecture](#system-architecture)
- [Features](#features)
- [Database Structure](#database-structure)
- [Workflow](#workflow)
- [Sequence Diagrams](#sequence-diagrams)
- [Technical Specifications](#technical-specifications)
- [Project Structure](#project-structure)

---

## Installation

### Requirements
- Rancher Desktop or Docker Desktop for container management
- Git version control
- Java 17 or later for local builds (optional if using Docker)
- PostgreSQL 13+ (handled automatically in Docker)
- Maven 3.6+ (optional if using Docker)

### Setup Steps
1. Clone the repository:
```git clone git@github.com:Sai-Teja-Ambati/RestaurantManagementSystem.git```


2. Setup database credentials in `application.properties` or environment variables as needed.

3. Build and run:
- For first-time build with migrations:
  ```
  docker-compose --profile migrations up --build
  ```
- For restarting containers without rebuild:
  ```
  docker-compose --profile migrations up
  ```

4. Access the backend REST API at:

```http://localhost:8080/```


---

## Usage

### Default Accounts

| Role    | Username            | Password   |
|---------|---------------------|------------|
| Admin   | admin@example.com   | admin123   |
| Waiter  | waiter1@example.com | waiter123  |
| Customer| customer@example.com| customer123|

### Roles and Permissions

- **Admin:** Full access including user management, inventory, and analytics.
- **Manager:** Limited administrative functions.
- **Waiter:** Manage orders, tables, and reservations.
- **Customer:** Browse menu, place orders, book tables.

---

## System Architecture

- **Controller Layer:** Exposes REST API endpoints secured by Spring Security.
- **Service Layer:** Encapsulates business logic for authentication, user, order, table, and booking management.
- **Repository Layer:** Interfaces with PostgreSQL via Spring Data JPA.
- **Security:** JWT-based stateless authentication with role-based authorization.
- **Validation and Exception Handling:** Bean validation with custom exceptions and global handlers.
- **Transaction Management:** Ensures data integrity in multi-step operations.

---

## Features

- **Authentication & Authorization**
- JWT token issuance and validation
- Role-restricted endpoint access
- User registration with role validation

- **User Management**
- CRUD operations for users by Admin
- User role assignment and checks

- **Menu Management**
- Add, update, delete menu items & categories
- Search and filter by price and category

- **Order Processing**
- Order creation with item and quantity validation
- Status transitions (Placed → InKitchen → Served)
- Billing calculations and inventory updates

- **Table Management**
- Create, update, remove tables
- Track table statuses: Available, Occupied, Reserved
- Find best fit table for required capacity

- **Reservation System**
- Time-slot based table bookings with conflict checks
- Update and cancel bookings with effects on table status

- **Inventory Management**
- Fetch current inventory and auto-update after orders
- Restore inventory from saved files
- Alert on low and out-of-stock items

- **Analytics and Reporting**
- Order statistics, inventory levels, and usage patterns
- Dashboard accessible to Admin users

---

## Database Structure

- **Users:** Holds user details and their role.
- **MenuItems:** Catalog of menu dishes with pricing.
- **Orders:** Track customer orders and status.
- **OrderItems:** Join orders with ordered menu items.
- **RestaurantTables:** Details including capacity and status.
- **TableReservations:** Records for booking and reservation management.
- **InventoryItems:** Tracks ingredient quantities.

---

## Workflow Overview

### User Authentication

1. User submits login credentials.
2. System authenticates and returns JWT token if valid.
3. Role-based menu and API access is enforced using token claims.

### Order Lifecycle

1. Waiter creates new order assigned to a table.
2. Items are added with quantity and availability checks.
3. Inventory quantities decrease as order progresses.
4. Order moves through statuses (Placed → In Kitchen → Served).
5. Table status updates accordingly.

### Table Booking Flow

1. Customer requests a booking for a table at a specific time.
2. System verifies availability without conflicts.
3. Booking stored; table status updated if booking is current.
4. Bookings can be modified or cancelled respecting time constraints.

---

## Sequence Diagrams

- **Admin:** Manages inventory, users, and views analytics.
- **Waiter:** Creates and manages orders, updates table status.
- **Customer:** Browses menu, places orders, books tables.

*See diagrams in `docs/sequences/` folder in the repository.*

---

## Technical Specifications

- **Framework:** Spring Boot 3.x
- **Security:** Spring Security with JWT
- **Database:** PostgreSQL 13+
- **Build:** Maven 3.6+
- **Containerization:** Docker and Docker Compose
- **Testing:** JUnit 5 with H2 for in-memory tests

---

## Project Structure

```declarative
RestaurantManagementSystem/
├── docker-compose.yml # Docker orchestration
├── Dockerfile # Docker image build
├── pom.xml # Maven dependencies and build config
├── README.md # Project documentation
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/restaurant/ # Java source code by package
│ │ └── resources/
│ │ ├── application.properties # Main config
│ │ ├── db/migration/ # Flyway migration scripts
│ │ └── seed-data/ # Initial data files
└── tests/ # Tests and test resources
```


For detailed class diagrams, REST API specifications, and workflow charts, please refer to the `docs/` directory in the repository.

---

## Contribution & Support

Please open issues or submit pull requests on GitHub to contribute or report problems:  
[https://github.com/Sai-Teja-Ambati/RestaurantManagementSystem](https://github.com/Sai-Teja-Ambati/RestaurantManagementSystem)

---

**Thank you for using the Restaurant Management System!**  
