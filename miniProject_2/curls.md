# Restaurant Management System - API Testing Guide

This document contains all possible curl commands to test the Restaurant Management System APIs, organized by workflows and user roles.

## Prerequisites
- Application running on `http://localhost:8080`
- PostgreSQL database initialized with default users

## Quick Start - Health Check
```bash
curl http://localhost:8080/actuator/health
```
Expected: `{"status":"UP"}`

---

## 🔐 Authentication Workflows

### 1. User Registration
```bash
# Register new customer
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newcustomer",
    "password": "password123",
    "email": "newcustomer@example.com",
    "role": "CUSTOMER"
  }'

# Register new waiter
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newwaiter",
    "password": "password123",
    "email": "newwaiter@example.com",
    "role": "WAITER"
  }'

# Register new admin
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newadmin",
    "password": "password123",
    "email": "newadmin@example.com",
    "role": "ADMIN"
  }'
```

### 2. User Login (Get JWT Tokens)
```bash
# Login as Admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Login as Customer
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"customer1","password":"customer123"}'

# Login as Waiter
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"waiter1","password":"waiter123"}'

# Login as Waiter 2
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"waiter2","password":"waiter123"}'

# Login as Waiter 3
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"waiter3","password":"waiter123"}'

# Login as Waiter 4
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"waiter4","password":"waiter123"}'

# Login as Waiter 5
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"waiter5","password":"waiter123"}'
```

**Note:** Save the JWT token from login response for authenticated requests below.

---

## 👤 Customer Workflows

### Environment Setup
```bash
# Set your customer JWT token
export CUSTOMER_TOKEN="your_jwt_token_here"
```

### Menu & Ordering
```bash
# View menu
curl -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  http://localhost:8080/api/customer/menu

# Place order
curl -X POST http://localhost:8080/api/customer/orders \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "menuItemId": 1,
        "quantity": 2,
        "specialInstructions": "Extra spicy"
      },
      {
        "menuItemId": 3,
        "quantity": 1,
        "specialInstructions": "No onions"
      }
    ],
    "tableNumber": 5,
    "orderType": "DINE_IN"
  }'

# View my orders
curl -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  http://localhost:8080/api/customer/orders

# View specific order
curl -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  http://localhost:8080/api/customer/orders/1
```

### Table Management
```bash
# View available tables
curl -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  http://localhost:8080/api/customer/tables/available

# View available tables for specific date/time
curl -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  "http://localhost:8080/api/customer/tables/available?date=2025-08-14&time=19:00"

# Book table reservation
curl -X POST http://localhost:8080/api/customer/reservations \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tableNumber": 3,
    "reservationDate": "2025-08-14",
    "reservationTime": "19:00",
    "partySize": 4,
    "specialRequests": "Window seat preferred"
  }'

# View my reservations
curl -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  http://localhost:8080/api/customer/reservations

# Cancel reservation
curl -X DELETE http://localhost:8080/api/customer/reservations/1 \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"
```

---

## 👨‍🍳 Waiter Workflows

### Environment Setup
```bash
# Set your waiter JWT token
export WAITER_TOKEN="your_jwt_token_here"
```

### Table Management
```bash
# View all tables
curl -H "Authorization: Bearer $WAITER_TOKEN" \
  http://localhost:8080/api/waiter/tables

# View vacant tables only
curl -H "Authorization: Bearer $WAITER_TOKEN" \
  http://localhost:8080/api/waiter/tables/vacant

# Mark table as occupied
curl -X POST http://localhost:8080/api/waiter/tables/3/occupy \
  -H "Authorization: Bearer $WAITER_TOKEN"

# Mark table as served
curl -X POST http://localhost:8080/api/waiter/tables/3/serve \
  -H "Authorization: Bearer $WAITER_TOKEN"

# Free table
curl -X POST http://localhost:8080/api/waiter/tables/3/free \
  -H "Authorization: Bearer $WAITER_TOKEN"

# Get table status
curl -H "Authorization: Bearer $WAITER_TOKEN" \
  http://localhost:8080/api/waiter/tables/3/status
```

### Order Management
```bash
# View active orders
curl -H "Authorization: Bearer $WAITER_TOKEN" \
  http://localhost:8080/api/waiter/orders/active

# View all orders
curl -H "Authorization: Bearer $WAITER_TOKEN" \
  http://localhost:8080/api/waiter/orders

# Update order status to PREPARING
curl -X PUT http://localhost:8080/api/waiter/orders/1/status \
  -H "Authorization: Bearer $WAITER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "PREPARING"}'

# Update order status to READY
curl -X PUT http://localhost:8080/api/waiter/orders/1/status \
  -H "Authorization: Bearer $WAITER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "READY"}'

# Update order status to SERVED
curl -X PUT http://localhost:8080/api/waiter/orders/1/status \
  -H "Authorization: Bearer $WAITER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "SERVED"}'

# Update order status to COMPLETED
curl -X PUT http://localhost:8080/api/waiter/orders/1/status \
  -H "Authorization: Bearer $WAITER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}'

# Get order details
curl -H "Authorization: Bearer $WAITER_TOKEN" \
  http://localhost:8080/api/waiter/orders/1
```

### Reservation Management
```bash
# View today's reservations
curl -H "Authorization: Bearer $WAITER_TOKEN" \
  http://localhost:8080/api/waiter/reservations/today

# View all reservations
curl -H "Authorization: Bearer $WAITER_TOKEN" \
  http://localhost:8080/api/waiter/reservations

# Check in reservation
curl -X PUT http://localhost:8080/api/waiter/reservations/1/checkin \
  -H "Authorization: Bearer $WAITER_TOKEN"
```

---

## 👨‍💼 Admin Workflows

### Environment Setup
```bash
# Set your admin JWT token
export ADMIN_TOKEN="your_jwt_token_here"
```

### Inventory Management
```bash
# View all inventory
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/inventory

# View low stock items
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/inventory/low-stock

# View specific inventory item
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/inventory/tomatoes

# Update inventory quantity
curl -X PUT http://localhost:8080/api/admin/inventory/tomatoes/quantity \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"quantity": 50}'

# Add new inventory item
curl -X POST http://localhost:8080/api/admin/inventory \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "new_ingredient",
    "quantity": 100,
    "unit": "kg",
    "minimumStock": 10
  }'

# Restore inventory from file
curl -X POST http://localhost:8080/api/admin/inventory/restore \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Bulk update inventory
curl -X PUT http://localhost:8080/api/admin/inventory/bulk-update \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "updates": [
      {"name": "tomatoes", "quantity": 25},
      {"name": "onions", "quantity": 30},
      {"name": "chicken", "quantity": 15}
    ]
  }'
```

### User Management
```bash
# View all users
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/users

# View users by role
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  "http://localhost:8080/api/admin/users?role=WAITER"

curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  "http://localhost:8080/api/admin/users?role=CUSTOMER"

# Get user details
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/users/1

# Update user role
curl -X PUT http://localhost:8080/api/admin/users/1/role \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"role": "WAITER"}'

# Deactivate user
curl -X PUT http://localhost:8080/api/admin/users/1/deactivate \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Activate user
curl -X PUT http://localhost:8080/api/admin/users/1/activate \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### Dashboard & Analytics
```bash
# Get dashboard statistics
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/dashboard/stats

# Get daily sales report
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  "http://localhost:8080/api/admin/reports/sales?date=2025-08-13"

# Get monthly sales report
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  "http://localhost:8080/api/admin/reports/sales?month=2025-08"

# Get popular menu items
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/reports/popular-items

# Get table utilization
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/reports/table-utilization
```

### System Management
```bash
# View system health
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/system/health

# Clear cache
curl -X POST http://localhost:8080/api/admin/system/clear-cache \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Backup database
curl -X POST http://localhost:8080/api/admin/system/backup \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## 🔄 Complete Workflow Examples

### 1. Customer Journey: Registration → Login → Order → Track
```bash
# Step 1: Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "role": "CUSTOMER"
  }'

# Step 2: Login (save the token)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"password123"}'

# Step 3: Set token and view menu
export TOKEN="your_jwt_token_here"
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/customer/menu

# Step 4: Place order
curl -X POST http://localhost:8080/api/customer/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [{"menuItemId": 1, "quantity": 1}],
    "tableNumber": 3,
    "orderType": "DINE_IN"
  }'

# Step 5: Track order
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/customer/orders
```

### 2. Waiter Journey: Login → Check Tables → Serve Order
```bash
# Step 1: Login as waiter
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"waiter1","password":"waiter123"}'

# Step 2: Set token and check tables
export WAITER_TOKEN="your_jwt_token_here"
curl -H "Authorization: Bearer $WAITER_TOKEN" \
  http://localhost:8080/api/waiter/tables

# Step 3: Check active orders
curl -H "Authorization: Bearer $WAITER_TOKEN" \
  http://localhost:8080/api/waiter/orders/active

# Step 4: Update order status
curl -X PUT http://localhost:8080/api/waiter/orders/1/status \
  -H "Authorization: Bearer $WAITER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "SERVED"}'
```

### 3. Admin Journey: Login → Check Inventory → Manage Users
```bash
# Step 1: Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Step 2: Set token and check inventory
export ADMIN_TOKEN="your_jwt_token_here"
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/inventory

# Step 3: Check low stock
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/inventory/low-stock

# Step 4: View dashboard
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/dashboard/stats
```

---

## 🛠️ Testing & Debugging

### Error Testing
```bash
# Test unauthorized access
curl http://localhost:8080/api/admin/inventory

# Test invalid credentials
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"invalid","password":"wrong"}'

# Test expired token (use old token)
curl -H "Authorization: Bearer expired_token" \
  http://localhost:8080/api/customer/menu
```

### Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connectivity (if endpoint exists)
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/system/health
```

---

## 📝 Notes

1. **JWT Tokens**: Save tokens from login responses and use them in subsequent requests
2. **Role-based Access**: Each role (CUSTOMER, WAITER, ADMIN) has different endpoint access
3. **Default Users**: Use the pre-created users for testing:
   - Admin: `admin` / `admin123`
   - Waiters: `waiter1-5` / `waiter123`
   - Customer: `customer1` / `customer123`
4. **Environment Variables**: Use `export TOKEN="..."` to avoid repeating long tokens
5. **Response Formats**: All responses are in JSON format
6. **Error Handling**: Check HTTP status codes for success/failure

## 🚀 Quick Test Script

Save this as `test-api.sh`:
```bash
#!/bin/bash

echo "Testing Restaurant Management System API..."

# Health check
echo "1. Health Check:"
curl -s http://localhost:8080/actuator/health | jq .

# Login as admin
echo -e "\n2. Admin Login:"
ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')
echo $ADMIN_RESPONSE | jq .

# Extract token (requires jq)
ADMIN_TOKEN=$(echo $ADMIN_RESPONSE | jq -r '.token')

# Test admin endpoint
echo -e "\n3. Admin Inventory:"
curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/inventory | jq .

echo -e "\nAPI testing complete!"
```

Run with: `chmod +x test-api.sh && ./test-api.sh`