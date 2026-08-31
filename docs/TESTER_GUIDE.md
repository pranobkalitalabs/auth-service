# 🧪 QA & Tester Guide - Auth & User Management Service

This guide is designed for **QA Engineers and Testers** to easily run, test, and validate **`auth-service`** locally and in the cloud.

---

## 🚀 1. How to Run Locally

### Option A: Run Full Containerized Stack with Docker Compose (Recommended)
```bash
# From workspace root:
docker compose up -d
```
This automatically starts:
- `auth-service` (Port **`8081`**)
- `address-service` (Port **`8082`**)
- `platform-redis` (Port **`6379`**)
- `auth-postgres` (Port **`5432`**)
- `auth-mailpit` (Port **`8025`**)

### Option B: Build Local Container via Google Jib
```bash
cd auth-service
mvn compile jib:dockerBuild
```

---

## 🔑 2. Default Test Credentials Cheat Sheet

The system automatically initializes standard test accounts on startup:

| Role | Email | Password | Allowed Operations |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin@platform.com` | `Admin@123456` | User administration, promoting users, deleting users, viewing all users. |
| **Standard User** | `john.doe@example.com` | `Password@123` | Viewing & updating own profile (`/api/v1/users/me`), changing password. |

---

## ⚙️ 3. Environment Variables Reference

| Variable Name | Default Value | Purpose |
| :--- | :--- | :--- |
| `PORT` | `8081` | HTTP server listening port. |
| `DB_HOST` | `localhost` (or `postgres` in Docker) | PostgreSQL database hostname. |
| `DB_PORT` | `5432` | PostgreSQL port. |
| `DB_NAME` | `authdb` | Database name. |
| `DB_USERNAME` | `postgres` | Database username. |
| `DB_PASSWORD` | `postgrespassword` | Database password. |
| `ADDRESS_SERVICE_URL` | `http://localhost:8082` | UK Address Service URL for geocoding during registration. |
| `JWT_SECRET` | *(256-bit secret)* | Secret key for signing and verifying JWT tokens. |
| `CORS_ALLOWED_ORIGINS` | `*` (Localhost + `pranobkalitalabs.co.uk`) | Allowed CORS web origins. |

---

## 📮 4. Testing with Postman & Newman

Downloadable Postman test collections and environments are packaged directly in this repository:

| File | Purpose | Direct Link |
| :--- | :--- | :--- |
| **Postman Collection** | Complete test suite (21 requests, 42 assertions) | [📥 `auth-service.postman_collection.json`](./postman/auth-service.postman_collection.json) |
| **Local Environment** | Targets `http://localhost:8081` | [📥 `auth-service.postman_environment.json`](./postman/auth-service.postman_environment.json) |
| **Cloud Environment** | Targets `https://auth.pranobkalitalabs.co.uk` | [📥 `pranobkalitalabs-cloud.postman_environment.json`](./postman/pranobkalitalabs-cloud.postman_environment.json) |

### Run Automated Suite via Newman CLI:
```bash
# Run against Local Dev Server
npx -y newman run ./docs/postman/auth-service.postman_collection.json \
  --environment ./docs/postman/auth-service.postman_environment.json

# Run against Cloud Production Server
npx -y newman run ./docs/postman/auth-service.postman_collection.json \
  --environment ./docs/postman/pranobkalitalabs-cloud.postman_environment.json
```
**Expected Result**: `21 requests executed, 42 assertions passed (100% Passed)`.

---

## 📖 5. Key API Endpoints & Swagger

Interactive Swagger UI is available at:  
👉 **`http://localhost:8081/swagger-ui.html`**

### 1. Authentication Endpoints:
- `POST /api/v1/auth/register` $\rightarrow$ Register new user (calls `address-service` for address geocoding).
- `POST /api/v1/auth/login` $\rightarrow$ Authenticate user & return JWT tokens.
- `POST /api/v1/auth/refresh-token` $\rightarrow$ Exchange refresh token for new access token.
- `POST /api/v1/auth/forgot-password` $\rightarrow$ Generate password reset token.
- `POST /api/v1/auth/reset-password` $\rightarrow$ Confirm new password with token.
- `POST /api/v1/auth/logout` $\rightarrow$ Revoke tokens.

### 2. User Profile Endpoints:
- `GET /api/v1/users/me` $\rightarrow$ Fetch current user profile.
- `PUT /api/v1/users/me` $\rightarrow$ Update profile & address.

### 3. Admin Endpoints (Requires `ROLE_ADMIN`):
- `GET /api/v1/users?page=0&size=10` $\rightarrow$ Paginated user list.
- `PUT /api/v1/users/{id}/roles` $\rightarrow$ Promote/demote user roles.
- `DELETE /api/v1/users/{id}` $\rightarrow$ Delete user account.

---

## 📑 Next Reading
- 🏛️ [**Architecture & Service Overview**](./SERVICE_OVERVIEW.md)
- 📊 [**Visual Workflow Diagrams**](./WORKFLOW_DIAGRAMS.md)
- ☁️ [**GCP Deployment Guide**](./GCP_DEPLOYMENT.md)
- 🏠 [**Back to Main README**](../README.md)
