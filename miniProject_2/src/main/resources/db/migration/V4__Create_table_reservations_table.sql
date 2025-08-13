-- Create table reservations table
CREATE TABLE IF NOT EXISTS table_reservations (
    id BIGSERIAL PRIMARY KEY,
    table_id BIGINT NOT NULL REFERENCES tables(id),
    customer_id BIGINT NOT NULL REFERENCES users(id),
    reservation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    notes TEXT,
    table_number INTEGER,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_reservation_time CHECK (end_time > start_time)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_reservations_table_id ON table_reservations(table_id);
CREATE INDEX IF NOT EXISTS idx_reservations_customer_id ON table_reservations(customer_id);
CREATE INDEX IF NOT EXISTS idx_reservations_status ON table_reservations(status);
CREATE INDEX IF NOT EXISTS idx_reservations_start_time ON table_reservations(start_time);
CREATE INDEX IF NOT EXISTS idx_reservations_table_number ON table_reservations(table_number);