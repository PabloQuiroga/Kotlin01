-- category.sql

-- Query for getAllCategories
SELECT id, name, description FROM categories;

-- Query for getCategoryById
SELECT id, name, description FROM categories WHERE id = ?;

-- Query for addCategory
INSERT INTO categories(name, description) VALUES(?, ?);

-- Query for updateCategory
UPDATE categories SET name = ?, description = ? WHERE id = ?;

-- Query for deleteCategory
DELETE FROM categories WHERE id = ?;