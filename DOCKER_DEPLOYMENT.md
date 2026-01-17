# Docker Deployment Guide

Hướng dẫn deploy ứng dụng Tara Academy API với Docker và Docker Compose.

## Yêu cầu

- Docker Engine 20.10+
- Docker Compose 2.0+
- Maven 3.6+ (để build JAR file)

## Cấu hình

### 1. Tạo file `.env`

Tạo file `.env` từ template dưới đây và điền các thông tin cấu hình:

```bash
# Spring Boot Configuration
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS=-Xms512m -Xmx1024m

# Server Ports
API_PORT=9999
NGINX_HTTP_PORT=80
NGINX_HTTPS_PORT=443

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://212.85.25.175:5432/taradb_new
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password_here

# MinIO Configuration
MINIO_ENDPOINT=https://miniotaraacademy.io.vn
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=your_minio_secret_key_here
MINIO_BUCKET_NAME=taraeducation

# JWT Configuration
CONSTANT_KEY_SIGNER_KEY=your_jwt_signer_key_here
```

**Lưu ý**: File `.env` sẽ chứa thông tin nhạy cảm, không commit vào Git!

### 2. Cấu hình trong `application.properties`

Các biến môi trường sẽ override các giá trị trong `application.properties`. Nếu không set biến môi trường, sẽ dùng giá trị mặc định trong `application.properties`.

## Deploy

### Cách 1: Sử dụng script deploy (Khuyến nghị)

```bash
chmod +x deploy.sh
./deploy.sh
```

Script này sẽ:
1. Build JAR file với Maven
2. Stop containers cũ
3. Build Docker images mới
4. Start containers
5. Hiển thị logs và status

### Cách 2: Deploy thủ công

#### Bước 1: Build JAR file

```bash
mvn clean package -DskipTests
```

#### Bước 2: Build và start containers

```bash
docker-compose up -d --build
```

#### Bước 3: Kiểm tra logs

```bash
docker-compose logs -f
```

## Các lệnh hữu ích

### Xem logs

```bash
# Tất cả services
docker-compose logs -f

# Chỉ API service
docker-compose logs -f tara-api

# Chỉ Nginx service
docker-compose logs -f nginx
```

### Kiểm tra status

```bash
docker-compose ps
```

### Restart services

```bash
# Restart tất cả
docker-compose restart

# Restart một service cụ thể
docker-compose restart tara-api
```

### Stop services

```bash
docker-compose down
```

### Stop và xóa volumes (cẩn thận!)

```bash
docker-compose down -v
```

### Rebuild và restart

```bash
docker-compose up -d --build --force-recreate
```

### Xem resource usage

```bash
docker stats
```

## Kiểm tra health

### Health check endpoint

```bash
curl http://localhost/actuator/health
```

Hoặc truy cập: http://localhost/actuator/health

### Kiểm tra containers

```bash
docker-compose ps
```

Tất cả containers phải có status `Up (healthy)`.

## Cấu trúc

```
├── Dockerfile              # Multi-stage build cho API
├── docker-compose.yml      # Orchestration cho tất cả services
├── nginx.conf              # Cấu hình Nginx reverse proxy
├── deploy.sh               # Script deploy tự động
├── .dockerignore           # Files bỏ qua khi build
└── .env                    # Environment variables (không commit)
```

## Troubleshooting

### Container không start

```bash
# Xem logs chi tiết
docker-compose logs tara-api

# Kiểm tra resource limits
docker stats
```

### Port đã được sử dụng

Thay đổi port trong `.env`:
```bash
API_PORT=9998
NGINX_HTTP_PORT=8080
```

### Database connection failed

Kiểm tra:
1. Database có thể truy cập được từ Docker container
2. Credentials trong `.env` đúng
3. Firewall cho phép connection

### Out of memory

Tăng memory limits trong `docker-compose.yml`:
```yaml
deploy:
  resources:
    limits:
      memory: 2G
```

## Production Checklist

- [ ] Đã tạo và cấu hình file `.env`
- [ ] Đã đổi passwords và secrets
- [ ] Đã cấu hình SSL certificates (nếu dùng HTTPS)
- [ ] Đã setup log rotation
- [ ] Đã cấu hình backup database
- [ ] Đã test health checks
- [ ] Đã setup monitoring và alerts

## URLs sau khi deploy

- API: http://localhost
- Swagger UI: http://localhost/swagger-ui.html
- API Docs: http://localhost/v3/api-docs
- Health Check: http://localhost/actuator/health

## Notes

- Dockerfile sử dụng multi-stage build để tối ưu kích thước image
- Nginx đóng vai trò reverse proxy và load balancer
- Health checks được cấu hình tự động
- Logs được rotate tự động (10MB per file, 3 files max)
