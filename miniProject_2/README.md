# Restaurant Management System

A comprehensive Spring Boot backend application for restaurant management with JWT authentication.

## Features

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (Admin, Waiter, Customer)
- Secure password encryption with BCrypt

### Customer Features
- View menu with categories and prices
- Book tables for specific time slots
- Place orders (dine-in or takeaway)
- View order history and reservation history
- Real-time order status tracking

### Waiter Features
- View all tables and their status (vacant/occupied/served)
- Manage table occupancy and service status
- View and update order status
- Manage reservations
- Serve tables and mark them as completed

### Admin Features
- Complete inventory management
- Restore inventory from files
- View low stock and out-of-stock items
- User management (view all users by role)
- Order analytics and reporting
- Table management
- Dashboard with key statistics

### Core Functionality
- **Menu Management**: Dynamic menu with recipes and pricing
- **Order Processing**: Complete order lifecycle with inventory deduction
- **Table Management**: Real-time table availability and booking
- **Inventory Tracking**: Automatic ingredient deduction based on recipes
- **Reservation System**: Time-slot based table reservations

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with JWT
- **Database**: PostgreSQL (with JPA/Hibernate)
- **Authentication**: JWT tokens
- **Validation**: Bean Validation
- **Code Generation**: Lombok
- **Testing**: JUnit 5, H2 (for tests)
- **Containerization**: Docker & Docker Compose

## Project Structure

```
src/main/java/com/restaurant/
├── config/          # Configuration classes
├── controller/      # REST Controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA Entities
├── exception/      # Exception handling
├── repository/     # JPA Repositories
├── security/       # Security configuration
├── service/        # Business logic
└── util/           # Utility classes

src/main/resources/
├── application.yml
├── CurrentInventory.txt
└── InitialInventory.txt
```

## Database Schema

### Core Entities
- **User**: Authentication and user management
- **RestaurantTable**: Table management with capacity
- **Order**: Order processing with JSON items
- **TableReservation**: Time-based table bookings
- **InventoryItem**: Ingredient tracking
- **OrderTable**: Many-to-many relationship between orders and tables

### Relationships
- User (1) → (N) Orders
- User (1) → (N) TableReservations
- RestaurantTable (1) → (N) TableReservations
- RestaurantTable (1) → (N) OrderTables
- Order (1) → (N) OrderTables

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Customer APIs
- `GET /api/customer/menu` - View menu
- `GET /api/customer/tables/available` - View available tables
- `POST /api/customer/reservations` - Book table
- `GET /api/customer/reservations` - View my reservations
- `POST /api/customer/orders` - Place order
- `GET /api/customer/orders` - View my orders

### Waiter APIs
- `GET /api/waiter/tables` - View all tables
- `GET /api/waiter/tables/vacant` - View vacant tables
- `POST /api/waiter/tables/{tableNumber}/serve` - Mark table as served
- `POST /api/waiter/tables/{tableNumber}/occupy` - Mark table as occupied
- `POST /api/waiter/tables/{tableNumber}/free` - Free table
- `GET /api/waiter/orders/active` - View active orders
- `PUT /api/waiter/orders/{orderId}/status` - Update order status

### Admin APIs
- `GET /api/admin/inventory` - View all inventory
- `GET /api/admin/inventory/low-stock` - View low stock items
- `POST /api/admin/inventory/restore` - Restore inventory from file
- `PUT /api/admin/inventory/{itemName}/quantity` - Update inventory quantity
- `GET /api/admin/users` - View all users
- `GET /api/admin/dashboard/stats` - Dashboard statistics

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Database Setup
1. Create PostgreSQL database:
```sql
CREATE DATABASE restaurant_db;
```

2. Update `application.yml` with your database credentials:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/restaurant_db
    username: your_username
    password: your_password
```

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Default Users
The application creates default users on startup:
- **Admin**: username=`admin`, password=`admin123`
- **Waiters**: username=`waiter1-5`, password=`waiter123`
- **Customer**: username=`customer1`, password=`customer123`

## Testing

Run tests with:
```bash
mvn test
```

## Key Features Implementation

### JWT Authentication
- Secure token-based authentication
- Role-based access control
- Token expiration handling

### Inventory Management
- Automatic ingredient deduction based on recipes
- Low stock alerts
- File-based inventory restoration

### Order Processing
- Real-time inventory validation
- Automatic tax and service charge calculation
- Order status tracking

### Table Management
- Real-time availability checking
- Time-slot based reservations
- Conflict prevention

### Exception Handling
- Global exception handler
- Validation error responses
- Proper HTTP status codes

## Recipe System

The application includes a comprehensive recipe system with:
- 50+ predefined recipes
- Ingredient-based inventory deduction
- Menu categorization with pricing
- Automatic availability checking

## File Structure

### Inventory Files
- `CurrentInventory.txt`: Current stock levels
- `InitialInventory.txt`: Default stock levels for restoration

Both files follow the format:
```
Item Name - Quantity
```

## Security Features

- Password encryption with BCrypt
- JWT token validation
- Role-based endpoint protection
- CORS configuration
- Authentication entry point handling

## Error Handling

Comprehensive error handling with:
- Global exception handler
- Validation error responses
- Custom error messages
- Proper HTTP status codes

## Future Enhancements

- Payment integration
- Real-time notifications
- Advanced reporting
- Mobile app support
- Multi-restaurant support