# 🚀 Auth Service - Google Cloud Platform (GCP) Deployment Guide

Step-by-step documentation for deploying `auth-service` to Google Cloud Run, connecting Neon PostgreSQL and Upstash Redis, domain mapping on Namecheap, and GitHub Actions CI/CD automation.

---

## 🏗️ 1. Service Specifications

* **Service Name**: `auth-service`
* **Region**: `europe-west1 (Belgium)`
* **Container Image**: `pkalita/auth-service:latest`
* **Port**: `8081`
* **Allocated Memory**: `1 GiB`
* **Allocated CPU**: `1 vCPU`
* **Authentication**: `Allow unauthenticated invocations`

---

## 📋 2. Cloud Run Environment Variables

These variables are configured in the Google Cloud Run Service under **Container $\rightarrow$ Environment Variables**:

| Variable Name | Category | Purpose / Description | Format / Example Value |
| :--- | :--- | :--- | :--- |
| `SERVER_PORT` | System | Container HTTP server listening port | `8081` |
| `SPRING_PROFILES_ACTIVE` | Runtime | Activates PostgreSQL datasource and Flyway migrations | `docker` |
| `DB_HOST` | Database | Neon Serverless PostgreSQL host | `<neon-subdomain>.aws.neon.tech` |
| `DB_PORT` | Database | PostgreSQL port | `5432` |
| `DB_NAME` | Database | Database name with SSL query parameter | `<db_name>?sslmode=require` |
| `DB_USERNAME` | Database | PostgreSQL username | `<db_user_owner>` |
| `DB_PASSWORD` | Database | PostgreSQL user password | `<db_password>` |
| `ADDRESS_SERVICE_URL` | Integration | Production URL of the downstream address microservice | `https://address.pranobkalitalabs.co.uk` |
| `JWT_SECRET` | Security | 256-bit HS512 cryptographic signing secret key | `<256-bit-hex-or-base64-secret-key>` |
| `REDIS_HOST` | Cache | Upstash / Cloud Redis endpoint hostname | `<upstash-redis-hostname>.upstash.io` |
| `REDIS_PORT` | Cache | Redis port | `6379` |
| `REDIS_PASSWORD` | Cache | Redis auth password | `<upstash-redis-password>` |
| `REDIS_SSL_ENABLED` | Cache | Enables TLS connection for cloud Redis | `true` |
| `CORS_ALLOWED_ORIGINS` | Security | Allowed Web origins for cross-origin requests | `https://pranobkalitalabs.co.uk,https://*.pranobkalitalabs.co.uk` |

---

## 🌐 3. Domain Mapping & Namecheap DNS

### Cloud Run Domain Mapping:
* **Service**: `auth-service (europe-west1)`
* **Verified Domain**: `pranobkalitalabs.co.uk`
* **Subdomain**: `auth` *(mapping resolves to `auth.pranobkalitalabs.co.uk`)*

### Namecheap Host Record:
* **Type**: `CNAME Record`
* **Host**: `auth`
* **Value**: `ghs.googlehosted.com.`
* **TTL**: `Automatic`

---

## 🤖 4. Automated CI/CD Deployment (GitHub Actions)

Every push to `main` executes `.github/workflows/docker-ci-cd.yml`:
1. Executes unit, integration, and Cucumber 7 BDD test suites against ephemeral PostgreSQL.
2. Builds multi-arch container image with Google Jib and pushes to Docker Hub.
3. Authenticates with Google Cloud and rolls out a new Cloud Run revision.

### Required GitHub Repository Secrets:
* `DOCKERHUB_USERNAME`: Docker Hub user ID.
* `DOCKERHUB_TOKEN`: Docker Hub Personal Access Token.
* `GCP_SA_KEY`: Google Cloud Service Account JSON key (`Cloud Run Admin` + `Service Account User`).

---

## 🔒 5. Health & Smoke Testing

```bash
# 1. Health Probe
curl -s https://auth.pranobkalitalabs.co.uk/actuator/health

# 2. Admin User Login
curl -s -X POST https://auth.pranobkalitalabs.co.uk/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"<admin-email>","password":"<admin-password>"}'

# 3. Swagger UI
open https://auth.pranobkalitalabs.co.uk/swagger-ui.html
```
