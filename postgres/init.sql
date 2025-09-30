-- ===========================
-- TẠO USER & DATABASE
-- ===========================
CREATE USER microservice_user WITH PASSWORD '1234';

-- Tạo database cho user-service
CREATE DATABASE user_service;
GRANT ALL PRIVILEGES ON DATABASE user_service TO microservice_user;

-- Tạo database cho order-service
CREATE DATABASE order_service;
GRANT ALL PRIVILEGES ON DATABASE order_service TO microservice_user;


-- ===========================
-- USER-SERVICE: bảng users
-- ===========================
\connect user_service;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Cấp quyền cho user
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO microservice_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO microservice_user;

-- Dữ liệu mẫu
INSERT INTO users (name, email, created_at, updated_at) VALUES
('Alice', 'alice@example.com', NOW(), NOW()),
('Bob', 'bob@example.com', NOW(), NOW());


-- ===========================
-- ORDER-SERVICE: bảng orders
-- ===========================
\connect order_service;

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Cấp quyền cho user
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO microservice_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO microservice_user;

-- Dữ liệu mẫu
INSERT INTO orders (user_id, product_name, quantity, created_at, updated_at) VALUES
(1, 'Laptop', 1, NOW(), NOW()),
(2, 'Mouse', 2, NOW(), NOW());
