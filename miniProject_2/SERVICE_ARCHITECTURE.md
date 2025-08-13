# Service Layer Architecture

## Overview
The service layer follows the **Interface-Implementation** pattern for better maintainability, testability, and loose coupling.

## Service Interfaces

### Core Service Interfaces

1. **UserService** - User management and authentication
   - Extends `UserDetailsService` for Spring Security integration
   - Handles user CRUD operations, role management, and authentication

2. **AuthService** - Authentication and authorization
   - User registration and login
   - JWT token generation and validation

3. **JwtService** - JWT token management
   - Token generation, validation, and claims extraction
   - Security key management

4. **MenuService** - Menu and recipe management
   - Menu retrieval and recipe information
   - Recipe availability checking

5. **OrderService** - Order processing
   - Order creation, status updates, and history
   - Inventory integration for ingredient deduction

6. **TableService** - Table management
   - Table availability, occupancy, and service status
   - Table operations (occupy, serve, free)

7. **TableReservationService** - Reservation management
   - Reservation creation, conflict checking
   - Reservation status management

8. **InventoryService** - Inventory management
   - Stock tracking, low stock alerts
   - File-based inventory operations

## Implementation Classes

All service implementations follow the naming convention:
- Interface: `[ServiceName]Service`
- Implementation: `[ServiceName]ServiceImpl`

### Benefits of Interface-Implementation Pattern

1. **Loose Coupling**: Controllers depend on interfaces, not concrete implementations
2. **Testability**: Easy to mock services for unit testing
3. **Maintainability**: Can swap implementations without changing dependent code
4. **SOLID Principles**: Follows Dependency Inversion Principle
5. **Future Extensibility**: Easy to add new implementations (e.g., caching layers)

## Dependency Injection

All controllers and services now inject interfaces:

```java
@Autowired
private UserService userService;  // Interface injection

@Autowired
private OrderService orderService;  // Interface injection
```

## Service Layer Structure

```
src/main/java/com/restaurant/service/
├── UserService.java (interface)
├── AuthService.java (interface)
├── JwtService.java (interface)
├── MenuService.java (interface)
├── OrderService.java (interface)
├── TableService.java (interface)
├── TableReservationService.java (interface)
├── InventoryService.java (interface)
└── impl/
    ├── UserServiceImpl.java (implements UserService)
    ├── AuthServiceImpl.java (implements AuthService)
    ├── JwtServiceImpl.java (implements JwtService)
    ├── MenuServiceImpl.java (implements MenuService)
    ├── OrderServiceImpl.java (implements OrderService)
    ├── TableServiceImpl.java (implements TableService)
    ├── TableReservationServiceImpl.java (implements TableReservationService)
    └── InventoryServiceImpl.java (implements InventoryService)
```

## Key Features

- **@Service** annotation on implementation classes
- **@Override** annotations on all implemented methods
- **Interface segregation** - each interface has a focused responsibility
- **Consistent method signatures** across all services
- **Proper exception handling** in all implementations

## Testing Benefits

With this architecture, you can easily create mock implementations:

```java
@MockBean
private UserService userService;

@MockBean
private OrderService orderService;
```

This makes unit testing much more straightforward and isolated.