-- product.sql

-- Query for getAllProducts
SELECT
    p.id AS product_id, p.name AS product_name, p.price,
    c.id AS category_id, c.name AS category_name, c.description
FROM products p
JOIN categories c ON p.category_id = c.id;

-- Query for getProductById
SELECT
    p.id AS product_id, p.name AS product_name, p.price,
    c.id AS category_id, c.name AS category_name, c.description
FROM products p
JOIN categories c ON p.category_id = c.id
WHERE p.id = ?;

-- Query for addProduct
INSERT INTO products(name, price, category_id) VALUES(?, ?, ?);

-- Query for updateProduct
UPDATE products SET name = ?, price = ?, category_id = ? WHERE id = ?;

-- Query for deleteProduct
DELETE FROM products WHERE id = ?;