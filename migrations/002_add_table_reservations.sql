-- Migration: Create table_reservations table
CREATE TABLE IF NOT EXISTS table_reservations (
    id SERIAL PRIMARY KEY,
    table_id INT NOT NULL REFERENCES tables(id),
    customer_id BIGINT NOT NULL REFERENCES users(id),
    reservation_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    notes TEXT
);

-- Create indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_table_reservations_table_id ON table_reservations(table_id);
CREATE INDEX IF NOT EXISTS idx_table_reservations_customer_id ON table_reservations(customer_id);
