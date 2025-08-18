# Restaurant Management System API Documentation

This document contains curl examples for all endpoints in the Restaurant Management System API.

## Table of Contents
- [Authentication](#authentication)
- [Users](#users)
- [Menu](#menu)
- [Orders](#orders)
- [Tables](#tables)
- [Bookings](#bookings)

## Authentication

### Health Check
```bash
curl -X GET http://localhost:8080/api/auth/health
```

### Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "role": "CUSTOMER",
    "username": "johndoe",
    "password": "password123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

### Refresh Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### Get Current User Profile
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Change Password
```bash
curl -X PUT http://localhost:8080/api/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "currentPassword": "password123",
    "newPassword": "newpassword123"
  }'
```

### Reset User Password (Admin only)
```bash
curl -X PUT http://localhost:8080/api/auth/reset-password/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "newPassword": "resetpassword123"
  }'
```

### Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Check if Current User is Admin
```bash
curl -X GET http://localhost:8080/api/auth/is-admin \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Check if Current User is Manager
```bash
curl -X GET http://localhost:8080/api/auth/is-manager \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Check if Current User is Waiter
```bash
curl -X GET http://localhost:8080/api/auth/is-waiter \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Check if Current User is Chef
```bash
curl -X GET http://localhost:8080/api/auth/is-chef \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Users

### Create a New User (Admin only)
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "fullName": "Jane Smith",
    "email": "jane.smith@example.com",
    "role": "CHEF",
    "username": "janesmith",
    "password": "password123"
  }'
```

### Get All Users (Admin or Manager)
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get User by ID (Admin or Manager)
```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update User (Admin or Manager)
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "fullName": "Jane Smith Updated",
    "email": "jane.updated@example.com",
    "role": "CHEF",
    "username": "janesmith",
    "password": "password123"
  }'
```

### Delete User (Admin only)
```bash
curl -X DELETE http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Users by Role (Admin or Manager)
```bash
curl -X GET http://localhost:8080/api/users/role/WAITER \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get All Waiters (Admin, Manager, or Chef)
```bash
curl -X GET http://localhost:8080/api/users/waiters \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get All Chefs (Admin, Manager, or Chef)
```bash
curl -X GET http://localhost:8080/api/users/chefs \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get All Managers (Admin only)
```bash
curl -X GET http://localhost:8080/api/users/managers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Check if User Exists (Admin or Manager)
```bash
curl -X GET http://localhost:8080/api/users/1/exists \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Menu

### Create a New Menu Item (Admin or Manager)
```bash
curl -X POST http://localhost:8080/api/menu \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Margherita Pizza",
    "category": "Pizza",
    "price": 12.99
  }'
```

### Get All Menu Items (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/menu \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Menu Item by ID (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/menu/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update Menu Item (Admin or Manager)
```bash
curl -X PUT http://localhost:8080/api/menu/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Margherita Pizza",
    "category": "Pizza",
    "price": 14.99
  }'
```

### Delete Menu Item (Admin only)
```bash
curl -X DELETE http://localhost:8080/api/menu/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Menu Items by Category (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/menu/category/Pizza \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get All Categories (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/menu/categories \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Search Menu Items (Admin, Manager, or Waiter)
```bash
curl -X GET "http://localhost:8080/api/menu/search?name=Pizza&category=Italian" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Menu Items by Price Range (Admin, Manager, or Waiter)
```bash
curl -X GET "http://localhost:8080/api/menu/price-range?min=10.00&max=20.00" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Menu Items Under Price (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/menu/under-price/15.00 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Orders

### Create a New Order (Admin, Manager, or Waiter)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "tableNumber": 1,
    "waiterId": 2,
    "items": [
      {
        "menuItemId": 1,
        "quantity": 2
      },
      {
        "menuItemId": 3,
        "quantity": 1
      }
    ],
    "specialInstructions": "No onions on the pizza"
  }'
```

### Get All Orders (Admin, Manager, Waiter, or Chef)
```bash
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Order by ID (Admin, Manager, Waiter, or Chef)
```bash
curl -X GET http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update Order Status (Admin, Manager, Waiter, or Chef)
```bash
curl -X PUT http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "status": "READY"
  }'
```

### Get Orders by Table Number (Admin, Manager, Waiter, or Chef)
```bash
curl -X GET http://localhost:8080/api/orders/table/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Orders by Status (Admin, Manager, Waiter, or Chef)
```bash
curl -X GET http://localhost:8080/api/orders/status/PLACED \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Orders by Waiter ID (Admin, Manager, or specific Waiter)
```bash
curl -X GET http://localhost:8080/api/orders/waiter/2 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Current User's Orders (Authenticated User)
```bash
curl -X GET http://localhost:8080/api/orders/my-orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Orders by Date (Admin, Manager, Waiter, or Chef)
```bash
curl -X GET http://localhost:8080/api/orders/date/2025-08-14T00:00:00 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Kitchen Orders (Admin, Manager, Waiter, or Chef)
```bash
curl -X GET http://localhost:8080/api/orders/kitchen \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Move Order to Kitchen (Admin, Manager, or Waiter)
```bash
curl -X PUT http://localhost:8080/api/orders/1/kitchen \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Mark Order as Served (Admin, Manager, Waiter, or Chef)
```bash
curl -X PUT http://localhost:8080/api/orders/1/served \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Cancel Order (Admin, Manager, or Waiter)
```bash
curl -X DELETE http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Today's Orders (Admin, Manager, Waiter, or Chef)
```bash
curl -X GET http://localhost:8080/api/orders/today \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Tables

### Create a New Table (Admin or Manager)
```bash
curl -X POST http://localhost:8080/api/tables \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "capacity": 4
  }'
```

### Get All Tables (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/tables \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Table by ID (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/tables/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update Table Capacity (Admin or Manager)
```bash
curl -X PUT http://localhost:8080/api/tables/1/capacity \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "capacity": 6
  }'
```

### Update Table Status (Admin or Manager)
```bash
curl -X PUT http://localhost:8080/api/tables/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "status": "OCCUPIED"
  }'
```

### Mark Table as Reserved (Admin, Manager, or Waiter)
```bash
curl -X PUT http://localhost:8080/api/tables/1/reserve \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Reserved Tables (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/tables/reserved \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Delete Table (Admin only)
```bash
curl -X DELETE http://localhost:8080/api/tables/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Tables by Status (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/tables/status/AVAILABLE \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Available Tables (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/tables/available \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Bookings

### Create a New Booking (Admin, Manager, or Waiter)
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "customerName": "Alice Johnson",
    "customerPhone": "+1234567890",
    "customerEmail": "alice@example.com",
    "tableNumber": 1,
    "partySize": 4,
    "bookingTime": "2025-08-15T19:00:00",
    "specialRequests": "Window table preferred"
  }'
```

### Get All Bookings (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/bookings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Booking by ID (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/bookings/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update Booking (Admin, Manager, or Waiter)
```bash
curl -X PUT http://localhost:8080/api/bookings/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "customerName": "Alice Johnson",
    "customerPhone": "+1234567890",
    "customerEmail": "alice@example.com",
    "tableNumber": 2,
    "partySize": 5,
    "bookingTime": "2025-08-15T20:00:00",
    "specialRequests": "Window table preferred"
  }'
```

### Cancel Booking (Admin or Manager)
```bash
curl -X DELETE http://localhost:8080/api/bookings/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Delete Booking Permanently (Admin only)
```bash
curl -X DELETE http://localhost:8080/api/bookings/1/permanent \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Bookings by Date (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/bookings/date/2025-08-15T00:00:00 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Bookings by Table Number (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/bookings/table/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Bookings by Status (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/bookings/status/CONFIRMED \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Mark Booking as Completed (Admin, Manager, or Waiter)
```bash
curl -X PUT http://localhost:8080/api/bookings/1/complete \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Today's Bookings (Admin, Manager, or Waiter)
```bash
curl -X GET http://localhost:8080/api/bookings/today \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```