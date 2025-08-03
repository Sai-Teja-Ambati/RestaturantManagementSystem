-- Migration: Create migrations table
CREATE TABLE IF NOT EXISTS migrations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Migration: Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'waiter', 'customer')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP WITH TIME ZONE
);

-- Create an admin user (password in plain text)
INSERT INTO users (username, email, password, role)
VALUES ('admin', 'ambatisaiteja123@gmail.com', 'admin123', 'admin')
ON CONFLICT (email) DO NOTHING;

-- Create waiter users (password in plain text)
INSERT INTO users (username, email, password, role)
VALUES
    ('waiter1', 'waiter1@restaurant.com', '12345678', 'waiter'),
    ('waiter2', 'waiter2@restaurant.com', '12345678', 'waiter'),
    ('waiter3', 'waiter3@restaurant.com', '12345678', 'waiter'),
    ('waiter4', 'waiter4@restaurant.com', '12345678', 'waiter'),
    ('waiter5', 'waiter5@restaurant.com', '12345678', 'waiter')
ON CONFLICT (email) DO NOTHING;

-- Create a sample customer
INSERT INTO users (username, email, password, role)
VALUES ('customer1', 'customer1@example.com', 'customer1', 'customer')
ON CONFLICT (email) DO NOTHING;
