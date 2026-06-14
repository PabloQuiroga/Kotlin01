-- Insert initial Geo data
INSERT INTO geos (lat, lng) VALUES ('34.0522', '-118.2437'); -- Los Angeles
INSERT INTO geos (lat, lng) VALUES ('51.5074', '0.1278');    -- London
INSERT INTO geos (lat, lng) VALUES ('35.6895', '139.6917');  -- Tokyo

-- Insert initial Address data (linking to Geo IDs)
-- Assuming Geo IDs start from 1 and increment
INSERT INTO addresses (street, suite, city, zipcode, geo_id) VALUES ('101 Hollywood Blvd', 'Suite 100', 'Los Angeles', '90028', 1);
INSERT INTO addresses (street, suite, city, zipcode, geo_id) VALUES ('221B Baker St', 'Apt 5', 'London', 'NW1 6XE', 2);
INSERT INTO addresses (street, suite, city, zipcode, geo_id) VALUES ('1-1-1 Shibuya', 'Floor 7', 'Tokyo', '150-0002', 3);

-- Insert initial User data (linking to Address IDs)
-- Assuming Address IDs start from 1 and increment
INSERT INTO users (name, username, email, phone, website, address_id) VALUES ('Alice Smith', 'alices', 'alice.s@example.com', '555-1111', 'alicesmith.com', 1);
INSERT INTO users (name, username, email, phone, website, address_id) VALUES ('Bob Johnson', 'bobj', 'bob.j@example.com', '555-2222', 'bobjohnson.net', 2);
INSERT INTO users (name, username, email, phone, website, address_id) VALUES ('Charlie Brown', 'charlieb', 'charlie.b@example.com', '555-3333', 'charliebrown.org', 3);

-- Insert initial Category data
INSERT INTO categories (name, description) VALUES ('Electronics', 'Devices and gadgets');
INSERT INTO categories (name, description) VALUES ('Books', 'Printed and digital literature');
INSERT INTO categories (name, description) VALUES ('Home & Kitchen', 'Appliances and decor for home');

-- Insert initial Product data (linking to Category IDs)
-- Assuming Category IDs start from 1 and increment
INSERT INTO products (name, price, category_id) VALUES ('Laptop X1', 1200.00, 1);
INSERT INTO products (name, price, category_id) VALUES ('Smartphone Pro', 800.00, 1);
INSERT INTO products (name, price, category_id) VALUES ('The Hitchhiker''s Guide to the Galaxy', 15.50, 2);
INSERT INTO products (name, price, category_id) VALUES ('Clean Code', 45.00, 2);
INSERT INTO products (name, price, category_id) VALUES ('Coffee Maker Deluxe', 75.99, 3);