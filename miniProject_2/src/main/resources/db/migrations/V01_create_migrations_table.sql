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

-- Migration: Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    order_id VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT REFERENCES users(id),
    order_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    items JSONB NOT NULL, -- Stores food items and their prices as JSON
    bill_subtotal NUMERIC(10, 2) NOT NULL,
    cgst_sgst NUMERIC(10, 2) DEFAULT 0, -- Combined CGST and SGST charges
    service_charge NUMERIC(10, 2) DEFAULT 0,
    bill_total NUMERIC(10, 2) NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    order_status VARCHAR(20) DEFAULT 'PENDING',
    table_number INT,
    special_instructions TEXT
);

-- Create index on order_id for faster lookups
CREATE INDEX IF NOT EXISTS idx_orders_order_id ON orders (order_id);

-- Create index on customer_id for faster customer order lookups
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders (customer_id);

-- Create trigger function to automatically calculate CGST and SGST charges
CREATE OR REPLACE FUNCTION calculate_taxes_and_total()
RETURNS TRIGGER AS $$
BEGIN
    -- Calculate service charge (2%)
    NEW.service_charge = ROUND(NEW.bill_subtotal * 0.02, 2);

    -- Add CGST and SGST charges (fixed Rs.25 if bill > 150)
    IF NEW.bill_subtotal > 150 THEN
        NEW.cgst_sgst = 25.00;
    ELSE
        NEW.cgst_sgst = 0.00;
    END IF;

    -- Calculate total bill
    NEW.bill_total = NEW.bill_subtotal + NEW.service_charge + NEW.cgst_sgst;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically calculate taxes and total before insert
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger
        WHERE tgname = 'calculate_order_totals'
    ) THEN
        CREATE TRIGGER calculate_order_totals
        BEFORE INSERT OR UPDATE ON orders
        FOR EACH ROW
        EXECUTE FUNCTION calculate_taxes_and_total();
    END IF;
END $$;

-- Insert a sample order, but only if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM orders WHERE items::text LIKE '%Paneer Tikka%') THEN
        INSERT INTO orders (order_id, customer_id, items, bill_subtotal)
        VALUES (
            'ORD-' || SUBSTRING(MD5(RANDOM()::TEXT) FROM 1 FOR 8),
            (SELECT id FROM users WHERE email = 'customer1@example.com' LIMIT 1),
            '[
                {"category": "Veg Starters", "itemName": "Paneer Tikka", "price": 249, "quantity": 1},
                {"category": "Indian Breads", "itemName": "Butter Naan", "price": 50, "quantity": 2}
            ]'::JSONB,
            349.00
        );
    END IF;
END $$;

-- Create tables table for tracking restaurant tables with capacity column
CREATE TABLE IF NOT EXISTS tables (
    id SERIAL PRIMARY KEY,
    table_number INT UNIQUE NOT NULL,
    capacity INT NOT NULL DEFAULT 4,
    is_occupied BOOLEAN DEFAULT FALSE,
    is_served BOOLEAN DEFAULT FALSE,
    booking_start_time TIMESTAMP,
    booking_end_time TIMESTAMP
);

-- Create order_tables table for linking orders to tables
CREATE TABLE IF NOT EXISTS order_tables (
    id SERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    table_id INT NOT NULL REFERENCES tables(id),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Add index for faster lookups
CREATE INDEX IF NOT EXISTS idx_order_tables_order_id ON order_tables(order_id);
CREATE INDEX IF NOT EXISTS idx_order_tables_table_id ON order_tables(table_id);

-- Migration: Create table_reservations table
CREATE TABLE IF NOT EXISTS table_reservations (
    id SERIAL PRIMARY KEY,
    table_id INT NOT NULL REFERENCES tables(id),
    customer_id BIGINT NOT NULL REFERENCES users(id),
    reservation_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    notes TEXT,
    table_number INT,
    start_time TIMESTAMP,
    end_time TIMESTAMP
);

-- Create indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_table_reservations_table_id ON table_reservations(table_id);
CREATE INDEX IF NOT EXISTS idx_table_reservations_customer_id ON table_reservations(customer_id);
CREATE INDEX IF NOT EXISTS idx_table_reservations_table_number ON table_reservations(table_number);

-- Create index for faster lookups by table_number
CREATE INDEX IF NOT EXISTS idx_tables_table_number ON tables(table_number);

-- Insert restaurant tables with various capacities if they don't exist
DO $$
BEGIN
    -- Table 1: Small table for two
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 1) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (1, 2, FALSE, FALSE);
    END IF;

    -- Table 2: Standard 4-person table
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 2) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (2, 4, FALSE, FALSE);
    END IF;

    -- Table 3: Standard 4-person table
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 3) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (3, 4, FALSE, FALSE);
    END IF;

    -- Table 4: Larger 6-person table
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 4) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (4, 6, FALSE, FALSE);
    END IF;

    -- Table 5: Standard 4-person table
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 5) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (5, 4, FALSE, FALSE);
    END IF;

    -- Table 6: Large group table
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 6) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (6, 8, FALSE, FALSE);
    END IF;

    -- Table 7: Small table for two
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 7) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (7, 2, FALSE, FALSE);
    END IF;

    -- Table 8: Standard 4-person table
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 8) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (8, 4, FALSE, FALSE);
    END IF;

    -- Table 9: Standard 4-person table
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 9) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (9, 4, FALSE, FALSE);
    END IF;

    -- Table 10: Larger 6-person table
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 10) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (10, 6, FALSE, FALSE);
    END IF;

    -- Table 11: Very large group table
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 11) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (11, 10, FALSE, FALSE);
    END IF;

    -- Table 12: Standard 4-person table
    IF NOT EXISTS (SELECT 1 FROM tables WHERE table_number = 12) THEN
        INSERT INTO tables (table_number, capacity, is_occupied, is_served)
        VALUES (12, 4, FALSE, FALSE);
    END IF;
END $$;