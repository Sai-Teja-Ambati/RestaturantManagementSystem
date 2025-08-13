-- Create restaurant tables table
CREATE TABLE IF NOT EXISTS tables (
    id BIGSERIAL PRIMARY KEY,
    table_number INTEGER UNIQUE NOT NULL,
    capacity INTEGER NOT NULL DEFAULT 4 CHECK (capacity >= 1),
    is_occupied BOOLEAN DEFAULT FALSE,
    is_served BOOLEAN DEFAULT FALSE,
    booking_start_time TIMESTAMP,
    booking_end_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_tables_number ON tables(table_number);
CREATE INDEX IF NOT EXISTS idx_tables_occupied ON tables(is_occupied);
CREATE INDEX IF NOT EXISTS idx_tables_capacity ON tables(capacity);

-- Insert default tables
INSERT INTO tables (table_number, capacity) VALUES 
(1, 2),
(2, 4),
(3, 4),
(4, 6),
(5, 4),
(6, 8),
(7, 2),
(8, 4),
(9, 4),
(10, 6),
(11, 10),
(12, 4)
ON CONFLICT (table_number) DO NOTHING;