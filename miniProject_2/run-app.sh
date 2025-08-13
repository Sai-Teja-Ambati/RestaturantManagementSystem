#!/bin/bash

# Restaurant Management System - Docker Startup Script

echo "🍽️  Starting Restaurant Management System..."
echo "================================================"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose and try again."
    exit 1
fi

# Create logs directory
mkdir -p logs

echo "🔧 Building and starting services..."

# Build and start services
docker-compose up --build -d

echo "⏳ Waiting for services to be ready..."

# Wait for PostgreSQL to be ready
echo "📊 Waiting for PostgreSQL..."
until docker-compose exec postgres pg_isready -U postgres -d restaurant_db > /dev/null 2>&1; do
    echo "   PostgreSQL is starting up..."
    sleep 2
done
echo "✅ PostgreSQL is ready!"

# Wait for Spring Boot application to be ready
echo "🚀 Waiting for Spring Boot application..."
until curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; do
    echo "   Application is starting up..."
    sleep 5
done
echo "✅ Application is ready!"

echo ""
echo "🎉 Restaurant Management System is now running!"
echo "================================================"
echo "📱 Application URL: http://localhost:8080"
echo "🗄️  Database: PostgreSQL on localhost:5432"
echo ""
echo "📋 Default Users:"
echo "   Admin:    username=admin,     password=admin123"
echo "   Waiter:   username=waiter1,   password=waiter123"
echo "   Customer: username=customer1, password=customer123"
echo ""
echo "📚 API Documentation:"
echo "   Auth:     POST /api/auth/login"
echo "   Customer: GET  /api/customer/menu"
echo "   Waiter:   GET  /api/waiter/tables"
echo "   Admin:    GET  /api/admin/inventory"
echo ""
echo "🔧 Management Commands:"
echo "   View logs:    docker-compose logs -f restaurant-app"
echo "   Stop:         docker-compose down"
echo "   Restart:      docker-compose restart"
echo "   Clean up:     docker-compose down -v"
echo ""
echo "✨ Happy coding! 🍽️"