CREATE DATABASE customerdb;
\c customerdb;

-- Create the service_status table
CREATE TABLE IF NOT EXISTS service_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    current_state VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Indexes for faster queries
CREATE INDEX IF NOT EXISTS idx_service_name ON service_status(name);
CREATE INDEX IF NOT EXISTS idx_timestamp ON service_status(timestamp);