-- Tạo database cho user-service
CREATE DATABASE user_service;

-- Tạo database cho order-service
CREATE DATABASE order_service;

-- Tạo user chung (hoặc có thể tạo user riêng cho mỗi service)
CREATE USER microservice_user WITH PASSWORD '1234';
GRANT ALL PRIVILEGES ON DATABASE user_service TO microservice_user;
GRANT ALL PRIVILEGES ON DATABASE order_service TO microservice_user;