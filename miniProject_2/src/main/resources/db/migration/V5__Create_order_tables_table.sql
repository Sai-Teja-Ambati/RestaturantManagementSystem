-- Create order_tables junction table
CREATE TABLE IF NOT EXISTS order_tables (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    table_id BIGINT NOT NULL REFERENCES tables(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(order_id, table_id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_order_tables_order_id ON order_tables(order_id);
CREATE INDEX IF NOT EXISTS idx_order_tables_table_id ON order_tables(table_id);