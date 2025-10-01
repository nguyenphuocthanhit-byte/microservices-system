-- Tạo databases (cách tương thích với mọi version)
SELECT 'CREATE DATABASE user_service'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'user_service')\gexec

SELECT 'CREATE DATABASE order_service'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'order_service')\gexec

-- Cấp quyền
GRANT ALL PRIVILEGES ON DATABASE user_service TO microservice_user;
GRANT ALL PRIVILEGES ON DATABASE order_service TO microservice_user;

-- Kết nối và tạo bảng cho user_service
\c user_service;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO microservice_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO microservice_user;

-- INSERT dữ liệu mẫu cho user_service
INSERT INTO users (name, email) VALUES
('Alice', 'alice@example.com'),
('Bob', 'bob@example.com')
ON CONFLICT (email) DO NOTHING;

-- Kết nối và tạo bảng cho order_service
\c order_service;

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

-- INSERT dữ liệu mẫu cho order_service
INSERT INTO orders (user_id, product_name, quantity) VALUES
(1, 'Laptop', 1),
(2, 'Mouse', 2)
ON CONFLICT (id) DO NOTHING;