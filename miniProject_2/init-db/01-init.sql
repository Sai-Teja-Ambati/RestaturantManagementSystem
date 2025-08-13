-- Create database if it doesn't exist
SELECT 'CREATE DATABASE restaurant_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'restaurant_db')\gexec

-- Connect to the database
\c restaurant_db;

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE restaurant_db TO postgres;