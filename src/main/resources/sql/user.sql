-- user.sql

-- Query for getAllUsers
SELECT
    u.id AS user_id, u.name AS user_name, u.username, u.email, u.phone, u.website,
    a.id AS address_id, a.street, a.suite, a.city, a.zipcode,
    g.id AS geo_id, g.lat, g.lng
FROM users u
JOIN addresses a ON u.address_id = a.id
JOIN geos g ON a.geo_id = g.id;

-- Query for getUserById
SELECT
    u.id AS user_id, u.name AS user_name, u.username, u.email, u.phone, u.website,
    a.id AS address_id, a.street, a.suite, a.city, a.zipcode,
    g.id AS geo_id, g.lat, g.lng
FROM users u
JOIN addresses a ON u.address_id = a.id
JOIN geos g ON a.geo_id = g.id
WHERE u.id = ?;

-- Query for addUser
INSERT INTO users(name, username, email, phone, website, address_id) VALUES(?, ?, ?, ?, ?, ?);

-- Query for insertGeo
INSERT INTO geos(lat, lng) VALUES(?, ?);

-- Query for insertAddress
INSERT INTO addresses(street, suite, city, zipcode, geo_id) VALUES(?, ?, ?, ?, ?);

-- Query for updateUser
UPDATE users SET name = ?, username = ?, email = ?, phone = ?, website = ? WHERE id = ?;

-- Query for updateGeo
UPDATE geos SET lat = ?, lng = ? WHERE id = ?;

-- Query for updateAddress
UPDATE addresses SET street = ?, suite = ?, city = ?, zipcode = ? WHERE id = ?;

-- Query for selectAddressIdForDelete
SELECT address_id FROM users WHERE id = ?;

-- Query for selectGeoIdForDelete
SELECT geo_id FROM addresses WHERE id = ?;

-- Query for deleteUser
DELETE FROM users WHERE id = ?;

-- Query for deleteAddress
DELETE FROM addresses WHERE id = ?;

-- Query for deleteGeo
DELETE FROM geos WHERE id = ?;