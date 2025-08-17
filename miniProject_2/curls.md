# Restaurant Management System API - cURL Commands

This document contains cURL commands for all available API endpoints in the Restaurant Management System.

## Base URL
```
http://localhost:8080
```

## Authentication

Most endpoints require authentication. First, login to get a JWT token:

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

### Register New User
**Note**: Public registration is restricted to non-privileged roles only (CUSTOMER, WAITER, CHEF). Admin and Manager accounts must be created by existing administrators.

```bash
# Register as Customer (default)
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "role": "CUSTOMER",
    "username": "johndoe",
    "password": "password123"
  }'

# Register as Waiter
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Jane Smith",
    "email": "jane@example.com",
    "role": "WAITER",
    "username": "janesmith",
    "password": "password123"
  }'

# Register as Chef
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Mike Johnson",
    "email": "mike@example.com",
    "role": "CHEF",
    "username": "mikejohnson",
    "password": "password123"
  }'

# ❌ This will FAIL - Cannot register as Admin
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Hacker",
    "email": "hacker@example.com",
    "role": "ADMIN",
    "username": "hacker",
    "password": "password123"
  }'
# Returns: "Cannot register with privileged role 'ADMIN'. Admin and Manager accounts must be created by existing administrators."
```

### Register Admin/Manager (Admin Only)
**Note**: This endpoint allows existing admins to create privileged accounts (Admin/Manager roles only).

```bash
# Register new Admin account (Admin only)
curl -X POST http://localhost:8080/auth/register-admin \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "New Admin",
    "email": "newadmin@example.com",
    "role": "ADMIN",
    "username": "newadmin",
    "password": "securepassword123"
  }'

# Register new Manager account (Admin only)
curl -X POST http://localhost:8080/auth/register-admin \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "New Manager",
    "email": "newmanager@example.com",
    "role": "MANAGER",
    "username": "newmanager",
    "password": "securepassword123"
  }'

# ❌ This will FAIL - Cannot use this endpoint for non-privileged roles
curl -X POST http://localhost:8080/auth/register-admin \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Regular User",
    "email": "user@example.com",
    "role": "WAITER",
    "username": "regularuser",
    "password": "password123"
  }'
# Returns: "This endpoint is only for creating Admin and Manager accounts. Use regular registration for other roles."
```

### Get Current User Profile
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Refresh Token
```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### Change Password
```bash
curl -X PUT http://localhost:8080/auth/change-password \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "oldpassword",
    "newPassword": "newpassword"
  }'
```

### Reset Password (Admin Only)
```bash
curl -X PUT http://localhost:8080/auth/reset-password/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "newPassword": "newpassword123"
  }'
```

### Logout
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Role Check Endpoints
```bash
# Check if current user is admin
curl -X GET http://localhost:8080/auth/is-admin \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Check if current user is manager
curl -X GET http://localhost:8080/auth/is-manager \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Check if current user is waiter
curl -X GET http://localhost:8080/auth/is-waiter \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Check if current user is chef
curl -X GET http://localhost:8080/auth/is-chef \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Health Check
```bash
curl -X GET http://localhost:8080/auth/health
```

---

## User Management

### Create User (Admin Only)
**Note**: This is the ONLY way to create Admin and Manager accounts. Public registration cannot create privileged roles.

```bash
# Create Admin account (Admin only)
curl -X POST http://localhost:8080/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "System Admin",
    "email": "admin@example.com",
    "role": "ADMIN",
    "username": "systemadmin",
    "password": "securepassword123"
  }'

# Create Manager account (Admin only)
curl -X POST http://localhost:8080/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Restaurant Manager",
    "email": "manager@example.com",
    "role": "MANAGER",
    "username": "manager",
    "password": "securepassword123"
  }'

# Create other roles (Admin only)
curl -X POST http://localhost:8080/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Jane Smith",
    "email": "jane@example.com",
    "role": "CHEF",
    "username": "janesmith",
    "password": "password123"
  }'
```

### Get All Users
```bash
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get User by ID
```bash
curl -X GET http://localhost:8080/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update User
```bash
curl -X PUT http://localhost:8080/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Jane Smith Updated",
    "email": "jane.updated@example.com",
    "role": "MANAGER",
    "username": "janesmith",
    "password": "newpassword123"
  }'
```

### Delete User (Admin Only)
```bash
curl -X DELETE http://localhost:8080/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Users by Role
```bash
curl -X GET http://localhost:8080/users/role/WAITER \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get All Waiters
```bash
curl -X GET http://localhost:8080/users/waiters \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get All Chefs
```bash
curl -X GET http://localhost:8080/users/chefs \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get All Managers
```bash
curl -X GET http://localhost:8080/users/managers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Check if User Exists
```bash
curl -X GET http://localhost:8080/users/1/exists \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Menu Management

### Create Menu Item
```bash
curl -X POST http://localhost:8080/menu \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Margherita Pizza",
    "category": "Pizza",
    "price": 12.99
  }'
```

### Get All Menu Items
```bash
curl -X GET http://localhost:8080/menu \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Menu Item by ID
```bash
curl -X GET http://localhost:8080/menu/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update Menu Item
```bash
curl -X PUT http://localhost:8080/menu/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Margherita Pizza Deluxe",
    "category": "Pizza",
    "price": 15.99
  }'
```

### Delete Menu Item (Admin Only)
```bash
curl -X DELETE http://localhost:8080/menu/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Menu Items by Category
```bash
curl -X GET http://localhost:8080/menu/category/Pizza \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get All Categories
```bash
curl -X GET http://localhost:8080/menu/categories \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Search Menu Items
```bash
# Search by name
curl -X GET "http://localhost:8080/menu/search?name=Pizza" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Search by category
curl -X GET "http://localhost:8080/menu/search?category=Appetizer" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Search by name and category
curl -X GET "http://localhost:8080/menu/search?name=Pizza&category=Pizza" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Menu Items by Price Range
```bash
curl -X GET "http://localhost:8080/menu/price-range?min=10.00&max=20.00" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Menu Items Under Price
```bash
curl -X GET http://localhost:8080/menu/under-price/15.00 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Menu Items by Category Ordered by Price
```bash
curl -X GET http://localhost:8080/menu/category/Pizza/ordered \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Check if Menu Item Exists
```bash
curl -X GET http://localhost:8080/menu/1/exists \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Table Management

### Create Table
```bash
curl -X POST http://localhost:8080/tables \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "capacity": 4
  }'
```

### Get All Tables
```bash
curl -X GET http://localhost:8080/tables \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Table by ID
```bash
curl -X GET http://localhost:8080/tables/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update Table Capacity
```bash
curl -X PUT http://localhost:8080/tables/1/capacity \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "capacity": 6
  }'
```

### Update Table Status
```bash
curl -X PUT http://localhost:8080/tables/1/status \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "OCCUPIED"
  }'
```

### Mark Table as Reserved
```bash
curl -X PUT http://localhost:8080/tables/1/reserve \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Mark Table as Occupied
```bash
curl -X PUT http://localhost:8080/tables/1/occupy \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Mark Table as Available
```bash
curl -X PUT http://localhost:8080/tables/1/free \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Delete Table (Admin Only)
```bash
curl -X DELETE http://localhost:8080/tables/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Tables by Status
```bash
curl -X GET http://localhost:8080/tables/status/AVAILABLE \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Available Tables
```bash
curl -X GET http://localhost:8080/tables/available \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Reserved Tables
```bash
curl -X GET http://localhost:8080/tables/reserved \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Occupied Tables
```bash
curl -X GET http://localhost:8080/tables/occupied \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Tables by Capacity
```bash
curl -X GET http://localhost:8080/tables/capacity/4 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Tables with Minimum Capacity
```bash
curl -X GET http://localhost:8080/tables/min-capacity/4 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Available Tables with Minimum Capacity
```bash
curl -X GET http://localhost:8080/tables/available/min-capacity/4 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Find Best Available Table
```bash
curl -X GET http://localhost:8080/tables/best-available/4 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Table Statistics
```bash
curl -X GET http://localhost:8080/tables/statistics \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Tables by Capacity Range
```bash
curl -X GET "http://localhost:8080/tables/capacity-range?min=2&max=6" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Available Tables Ordered by Capacity
```bash
curl -X GET http://localhost:8080/tables/available/ordered \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Order Management

### Create Order
```bash
curl -X POST http://localhost:8080/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tableNumber": 1,
    "items": [
      {
        "menuItemId": 1,
        "quantity": 2
      },
      {
        "menuItemId": 2,
        "quantity": 1
      }
    ]
  }'
```

### Get All Orders
```bash
curl -X GET http://localhost:8080/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Order by ID
```bash
curl -X GET http://localhost:8080/orders/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update Order Status
```bash
curl -X PUT http://localhost:8080/orders/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IN_KITCHEN"
  }'
```

### Get Orders by Table Number
```bash
curl -X GET http://localhost:8080/orders/table/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Orders by Status
```bash
curl -X GET http://localhost:8080/orders/status/PLACED \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Orders by Waiter ID
```bash
curl -X GET http://localhost:8080/orders/waiter/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Current User's Orders
```bash
curl -X GET http://localhost:8080/orders/my-orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Orders by Date
```bash
curl -X GET http://localhost:8080/orders/date/2024-08-16T00:00:00 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Kitchen Orders
```bash
curl -X GET http://localhost:8080/orders/kitchen \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Move Order to Kitchen
```bash
curl -X PUT http://localhost:8080/orders/1/kitchen \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Mark Order as Served
```bash
curl -X PUT http://localhost:8080/orders/1/served \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Cancel Order
```bash
curl -X DELETE http://localhost:8080/orders/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Today's Orders
```bash
curl -X GET http://localhost:8080/orders/today \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Placed Orders
```bash
curl -X GET http://localhost:8080/orders/placed \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get In-Kitchen Orders
```bash
curl -X GET http://localhost:8080/orders/in-kitchen \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Served Orders
```bash
curl -X GET http://localhost:8080/orders/served \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Booking Management

### Create Booking
```bash
curl -X POST http://localhost:8080/bookings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Smith",
    "bookingTime": "2024-08-20T19:00:00",
    "tableNumber": 1,
    "numberOfGuests": 4
  }'
```

### Get All Bookings
```bash
curl -X GET http://localhost:8080/bookings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Booking by ID
```bash
curl -X GET http://localhost:8080/bookings/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update Booking
```bash
curl -X PUT http://localhost:8080/bookings/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Smith Updated",
    "bookingTime": "2024-08-20T20:00:00",
    "tableNumber": 2,
    "numberOfGuests": 6
  }'
```

### Cancel Booking
```bash
curl -X DELETE http://localhost:8080/bookings/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Delete Booking Permanently (Admin Only)
```bash
curl -X DELETE http://localhost:8080/bookings/1/permanent \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Bookings by Date
```bash
curl -X GET http://localhost:8080/bookings/date/2024-08-20T00:00:00 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Bookings by Table Number
```bash
curl -X GET http://localhost:8080/bookings/table/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Bookings by Status
```bash
curl -X GET http://localhost:8080/bookings/status/RESERVED \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Complete Booking
```bash
curl -X PUT http://localhost:8080/bookings/1/complete \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Today's Bookings
```bash
curl -X GET http://localhost:8080/bookings/today \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Active Bookings
```bash
curl -X GET http://localhost:8080/bookings/active \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Upcoming Bookings
```bash
curl -X GET http://localhost:8080/bookings/upcoming \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Notes

1. **Replace `YOUR_JWT_TOKEN`** with the actual JWT token received from the login endpoint.
2. **Date Format**: Use ISO 8601 format for dates: `YYYY-MM-DDTHH:MM:SS`
3. **Roles**: Available roles are `ADMIN`, `MANAGER`, `WAITER`, `CHEF`, `CUSTOMER`
4. **Table Status**: Available statuses are `AVAILABLE`, `RESERVED`, `OCCUPIED`
5. **Order Status**: Available statuses are `PLACED`, `IN_KITCHEN`, `SERVED`
6. **Booking Status**: Available statuses are `RESERVED`, `CANCELLED`, `COMPLETED`
7. **Privileged Account Creation**: 
   - Regular users can register as `CUSTOMER`, `WAITER`, or `CHEF` via `/auth/register`
   - Only existing admins can create `ADMIN` or `MANAGER` accounts via `/auth/register-admin`
   - Alternatively, admins can use `/users` endpoint to create any role

## Example Workflow

1. **Login as Admin**:
   ```bash
   curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "admin", "password": "password"}'
   ```

2. **Create a Menu Item**:
   ```bash
   curl -X POST http://localhost:8080/menu \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"name": "Caesar Salad", "category": "Salad", "price": 8.99}'
   ```

3. **Create a Table**:
   ```bash
   curl -X POST http://localhost:8080/tables \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"capacity": 4}'
   ```

4. **Create an Order**:
   ```bash
   curl -X POST http://localhost:8080/orders \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"tableNumber": 1, "items": [{"menuItemId": 1, "quantity": 2}]}'
   ```

5. **Create a Booking**:
   ```bash
   curl -X POST http://localhost:8080/bookings \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"customerName": "Alice Johnson", "bookingTime": "2024-08-20T19:00:00", "tableNumber": 1, "numberOfGuests": 2}'
   ```