#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER IF NOT EXISTS microservice_user WITH PASSWORD '1234';
    CREATE DATABASE IF NOT EXISTS user_service;
    CREATE DATABASE IF NOT EXISTS order_service;
    GRANT ALL PRIVILEGES ON DATABASE user_service TO microservice_user;
    GRANT ALL PRIVILEGES ON DATABASE order_service TO microservice_user;
EOSQL

# Init user_service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "user_service" <<-EOSQL
    CREATE TABLE IF NOT EXISTS users (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        email VARCHAR(255) UNIQUE NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO microservice_user;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO microservice_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO microservice_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON SEQUENCES TO microservice_user;

    INSERT INTO users (name, email) VALUES
    ('Alice', 'alice@example.com'),
    ('Bob', 'bob@example.com')
    ON CONFLICT (email) DO NOTHING;
EOSQL

# Init order_service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "order_service" <<-EOSQL
    CREATE TABLE IF NOT EXISTS orders (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL,
        product_name VARCHAR(255) NOT NULL,
        quantity INT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO microservice_user;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO microservice_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO microservice_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON SEQUENCES TO microservice_user;

    INSERT INTO orders (user_id, product_name, quantity) VALUES
    (1, 'Laptop', 1),
    (2, 'Mouse', 2)
    ON CONFLICT DO NOTHING;
EOSQL