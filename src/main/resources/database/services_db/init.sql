CREATE DATABASE customerdb;
\c customerdb;


-- Create the login_web_service_status table
CREATE TABLE IF NOT EXISTS login_web_service_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    current_state VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the activate_app_service_status table
CREATE TABLE IF NOT EXISTS activate_app_service_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    current_state VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the login_app_service_status table
CREATE TABLE IF NOT EXISTS login_app_service_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    current_state VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the deactivate_app_service_status table
CREATE TABLE IF NOT EXISTS deactivate_app_service_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    current_state VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the eidas_service_status table
CREATE TABLE IF NOT EXISTS eidas_service_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    current_state VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- Indexes for faster queries
-- Indexes for login_web_service_status
CREATE INDEX IF NOT EXISTS idx_login_web_service_status_name ON login_web_service_status(name);
CREATE INDEX IF NOT EXISTS idx_login_web_service_status_state ON login_web_service_status(current_state);
CREATE INDEX IF NOT EXISTS idx_login_web_service_status_timestamp ON login_web_service_status(timestamp);

-- Indexes for activate_app_service_status
CREATE INDEX IF NOT EXISTS idx_activate_app_service_status_name ON activate_app_service_status(name);
CREATE INDEX IF NOT EXISTS idx_activate_app_service_status_state ON activate_app_service_status(current_state);
CREATE INDEX IF NOT EXISTS idx_activate_app_service_status_timestamp ON activate_app_service_status(timestamp);

-- Indexes for login_app_service_status
CREATE INDEX IF NOT EXISTS idx_login_app_service_status_name ON login_app_service_status(name);
CREATE INDEX IF NOT EXISTS idx_login_app_service_status_state ON login_app_service_status(current_state);
CREATE INDEX IF NOT EXISTS idx_login_app_service_status_timestamp ON login_app_service_status(timestamp);

-- Indexes for deactivate_app_service_status
CREATE INDEX IF NOT EXISTS idx_deactivate_app_service_status_name ON deactivate_app_service_status(name);
CREATE INDEX IF NOT EXISTS idx_deactivate_app_service_status_state ON deactivate_app_service_status(current_state);
CREATE INDEX IF NOT EXISTS idx_deactivate_app_service_status_timestamp ON deactivate_app_service_status(timestamp);

-- Indexes for eidas_service_status
CREATE INDEX IF NOT EXISTS idx_eidas_service_status_name ON eidas_service_status(name);
CREATE INDEX IF NOT EXISTS idx_eidas_service_status_state ON eidas_service_status(current_state);
CREATE INDEX IF NOT EXISTS idx_eidas_service_status_timestamp ON eidas_service_status(timestamp);