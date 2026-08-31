# 🔐 Auth & IAM Service - Architecture & Service Overview

**Author & Creator**: Pranob Jyoti Kalita  
**Domain**: [`pranobkalitalabs.co.uk`](https://pranobkalitalabs.co.uk)  
**Microservice Name**: `auth-service` (Port `8081`)

---

## 📌 Executive Summary

The **Authentication & Identity Access Management (IAM) Service** is an enterprise-grade, containerized Spring Boot 3 microservice providing:
1. **Stateless JWT Authentication**: Secure access tokens (15m expiry) and refresh tokens (7d expiry) with HMAC-SHA256 signature verification.
2. **User Registration with UK Address Geocoding**: Automatically enriches registered user profiles with geo coordinates (latitude/longitude), region, and district by calling **`address-service`** over HTTP.
3. **Role-Based Access Control (RBAC)**: Fine-grained security roles (`ROLE_USER`, `ROLE_ADMIN`) protecting administrative endpoints.
4. **Password Reset Workflow**: Secure, time-limited password recovery tokens with email notifications.
5. **Database Persistence**: PostgreSQL relational database with automated schema migration and JPA entities.

---

## 🏛️ System Architecture

```
                    Client (Web UI / Mobile / Postman)
                                   │
                                   ▼
                    +-----------------------------+
                    |        auth-service         |
                    |    (Spring Boot 3 / JRE 21) |
                    +-----------------------------+
                            /               \
         (User Registration)                 (User & Token Storage)
                        /                             \
                       v                               v
            +--------------------+           +--------------------+
            |  address-service   |           |     PostgreSQL     |
            |  (Port 8082 / Geo) |           | (Port 5432 / DB)   |
            +--------------------+           +--------------------+
```

---

## 🛡️ Security Architecture & RBAC

- **Password Hashing**: BCrypt hashing with configurable work factor (12 rounds).
- **JWT Signing**: HS256 algorithm with 256-bit secret key.
- **Roles**:
  - `ROLE_USER`: Standard authenticated user permissions (`GET /api/v1/users/me`, `PUT /api/v1/users/me`).
  - `ROLE_ADMIN`: Administrative permissions (`GET /api/v1/users`, `PUT /api/v1/users/{id}/roles`, `DELETE /api/v1/users/{id}`).

---

## 📑 Next Reading
- 📊 [**Visual Workflow Diagrams**](./WORKFLOW_DIAGRAMS.md) - Sequence diagrams for registration, login, and password recovery.
- 🧪 [**QA & Tester Guide**](./TESTER_GUIDE.md) - Test credentials, Postman test guide, and `.env` variables table.
- ☁️ [**GCP Deployment Guide**](./GCP_DEPLOYMENT.md) - Deploying to Google Cloud Run and Cloud PostgreSQL.
- 🏠 [**Back to Main README**](../README.md)
