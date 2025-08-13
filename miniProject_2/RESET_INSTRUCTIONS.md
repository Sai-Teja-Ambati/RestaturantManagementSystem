# 🔥 COMPLETE RESET INSTRUCTIONS

## The Problem
PostgreSQL is using existing data with different authentication settings, causing:
- Database "restaurant_db" does not exist
- Password authentication failed

## The Solution
Complete reset with new configuration.

## Steps to Fix:

### 1. Stop Everything
```bash
docker-compose down -v
```

### 2. Remove Problematic Volume
```bash
docker volume rm miniproject_2_postgres_data
```

### 3. Remove All Related Containers
```bash
docker rm -f restaurant-postgres restaurant-flyway restaurant-app
```

### 4. Clean Up
```bash
docker container prune -f
docker volume prune -f
```

### 5. Start Fresh
```bash
docker-compose up --build
```

## What Was Fixed:
- ✅ Removed persistent volume causing authentication conflicts
- ✅ Added `POSTGRES_HOST_AUTH_METHOD: md5` for proper authentication
- ✅ Added `init-db.sql` to ensure database initialization
- ✅ Simplified configuration to avoid conflicts
- ✅ Proper health checks and dependencies

## Expected Result:
1. PostgreSQL starts with fresh data
2. Database "restaurant_db" is created automatically
3. Flyway migrations run successfully
4. Spring Boot application connects without errors
5. Application available at http://localhost:8080

## If Still Having Issues:
Check logs with:
```bash
docker-compose logs postgres
docker-compose logs flyway
docker-compose logs restaurant-app
```