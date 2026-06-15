-- schema.sql

-- Enable foreign key support
PRAGMA foreign_keys = ON;

-- Create geos table
CREATE TABLE IF NOT EXISTS geos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    lat TEXT NOT NULL,
    lng TEXT NOT NULL
);

-- Create addresses table
CREATE TABLE IF NOT EXISTS addresses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    street TEXT NOT NULL,
    suite TEXT NOT NULL,
    city TEXT NOT NULL,
    zipcode TEXT NOT NULL,
    geo_id INTEGER,
    FOREIGN KEY (geo_id) REFERENCES geos(id) ON DELETE CASCADE
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    phone TEXT NOT NULL,
    website TEXT NOT NULL,
    address_id INTEGER,
    FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE CASCADE
);

-- Create categories table
CREATE TABLE IF NOT EXISTS categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT NOT NULL
);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    price REAL NOT NULL,
    category_id INTEGER,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);