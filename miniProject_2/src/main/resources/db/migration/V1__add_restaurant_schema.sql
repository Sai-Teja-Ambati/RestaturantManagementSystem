CREATE TABLE users (
  user_id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  role VARCHAR(50) NOT NULL,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE tables (
    table_id BIGSERIAL PRIMARY KEY,
    capacity INT NOT NULL CHECK (capacity >= 1),
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE'
);


CREATE TABLE menuitems (
    item_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price NUMERIC(10,2) NOT NULL CHECK (price > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    order_id BIGSERIAL PRIMARY KEY,
    table_number INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PLACED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    table_id BIGINT,
    waiter_id BIGINT,
    CONSTRAINT fk_orders_table FOREIGN KEY (table_id) REFERENCES tables(table_id),
    CONSTRAINT fk_orders_waiter FOREIGN KEY (waiter_id) REFERENCES users(user_id)
);

CREATE TABLE orderitems (
    order_item_id BIGSERIAL PRIMARY KEY,
    quantity INT NOT NULL CHECK (quantity >= 1),
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    CONSTRAINT fk_orderitems_order FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_orderitems_menuitem FOREIGN KEY (item_id) REFERENCES menuitems(item_id)
);


CREATE TABLE bookings (
    booking_id BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    booking_datetime TIMESTAMP NOT NULL,
    table_number INT NOT NULL,
    number_of_guests INT NOT NULL CHECK (number_of_guests >= 1),
    status VARCHAR(50) NOT NULL DEFAULT 'RESERVED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    table_id BIGINT,
    customer_id BIGINT,
    CONSTRAINT fk_bookings_table FOREIGN KEY (table_id) REFERENCES tables(table_id),
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) REFERENCES users(user_id)
);


-- Truncate and reset IDs
TRUNCATE TABLE orderitems, orders, bookings, menuitems, tables, users RESTART IDENTITY CASCADE;

-- Optional: restart sequences manually
ALTER SEQUENCE users_user_id_seq RESTART WITH 1;
ALTER SEQUENCE tables_table_id_seq RESTART WITH 1;
ALTER SEQUENCE menuitems_item_id_seq RESTART WITH 1;
ALTER SEQUENCE orders_order_id_seq RESTART WITH 1;
ALTER SEQUENCE orderitems_order_item_id_seq RESTART WITH 1;
ALTER SEQUENCE bookings_booking_id_seq RESTART WITH 1;