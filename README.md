# 🔐 Auth & IAM Microservice (`auth-service`)

[![Docker Pulls](https://img.shields.io/docker/pulls/pkalita/auth-service?logo=docker&style=flat-square)](https://hub.docker.com/r/pkalita/auth-service)
[![Docker Image Version](https://img.shields.io/docker/v/pkalita/auth-service/latest?logo=docker&style=flat-square)](https://hub.docker.com/r/pkalita/auth-service)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)

An enterprise-grade, containerized Spring Boot 3 microservice for **Stateless JWT Authentication**, **User Registration with UK Address Geocoding**, **Role-Based Access Control (RBAC)**, and **Password Recovery**, backed by **PostgreSQL**.

---

## 🌐 Live API & Swagger Documentation

| Environment | Base URL | Interactive Swagger / OpenAPI UI | Health Status |
| :--- | :--- | :--- | :--- |
| **Local Dev** | `http://localhost:8081` | [Local Swagger UI](http://localhost:8081/swagger-ui.html) | [Local Health](http://localhost:8081/actuator/health) |
| **Cloud Production** | `https://auth.pranobkalitalabs.co.uk` | [Production Swagger UI](https://auth.pranobkalitalabs.co.uk/swagger-ui.html) | [Production Health](https://auth.pranobkalitalabs.co.uk/actuator/health) |

---

## 📚 In-Depth Documentation

For detailed technical guides, please refer to the dedicated documents in the [`docs/`](./docs) folder:

| Document | Description |
| :--- | :--- |
| 🏛️ [**Architecture & Service Overview**](./docs/SERVICE_OVERVIEW.md) | High-level system architecture, JWT lifecycle, RBAC roles (`ROLE_USER`, `ROLE_ADMIN`), and PostgreSQL schema. |
| 📊 [**Visual Workflow Diagrams**](./docs/WORKFLOW_DIAGRAMS.md) | Sequence diagrams for User Registration with UK Address geocoding, JWT Authentication & Refresh, and Password Recovery. |
| 🧪 [**QA & Tester Guide**](./docs/TESTER_GUIDE.md) | Test credentials cheat sheet (`admin@platform.com` / `Admin@123456`), `.env` variables reference table, and Postman runner instructions. |
| 📮 [**Postman Collection & Environments**](./docs/postman/auth-service.postman_collection.json) | Ready-to-import Postman test collection with 21 automated requests and 42 assertions. |
| ☁️ [**GCP Deployment Guide**](./docs/GCP_DEPLOYMENT.md) | Step-by-step production deployment to Google Cloud Run, Cloud PostgreSQL, and custom domain mapping for `auth.pranobkalitalabs.co.uk`. |

---

## ⚡ Quick Start & Docker Compose

### 1. Run Standalone Auth Stack (Postgres + Mailpit + Auth Service)
```bash
docker compose up -d
```

### 2. Build Local Container with Google Jib (5 Seconds)
```bash
mvn compile jib:dockerBuild
```

### 3. Run Behavior-Driven Development (BDD) Cucumber Tests
```bash
# Run Gherkin feature scenarios
mvn test -Dtest=AuthCucumberTest
```
> 📊 **HTML Living Report**: Generated at `target/cucumber-reports/cucumber.html`.

Test that the service is running:
```bash
curl http://localhost:8081/actuator/health
```

---

## 👨‍💻 Author & Maintainer
- **Creator**: Pranob Jyoti Kalita
- **Organization**: [Pranob Kalita Labs](https://pranobkalitalabs.co.uk)
- **Docker Hub**: [`pkalita/auth-service`](https://hub.docker.com/r/pkalita/auth-service)
