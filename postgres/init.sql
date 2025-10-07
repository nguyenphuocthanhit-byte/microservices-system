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
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO microservice_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO microservice_user;

-- INSERT dữ liệu mẫu cho user_service
INSERT INTO users (first_name, last_name, email, password, role, created_at, updated_at)
VALUES
    ('Alice', 'Nguyen', 'alice@example.com', 'password123', 'ROLE_USER', NOW(), NOW()),
    ('Bob', 'Tran', 'bob@example.com', 'password123', 'ROLE_ADMIN', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    user_id BIGINT UNIQUE,  -- vì quan hệ OneToOne
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);


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


