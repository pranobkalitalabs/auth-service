# ☁️ Auth Service - Google Cloud Platform (GCP) Deployment Guide

This guide details the exact steps to deploy **`auth-service`** to **Google Cloud Run**, connect to a cloud PostgreSQL database, and map the custom domain **`auth.pranobkalitalabs.co.uk`**.

---

## 🏛️ GCP Architecture for Auth Service

```
                 https://auth.pranobkalitalabs.co.uk
                                 │
                                 ▼
                     [ Google Cloud Run: auth ]
                       (europe-west1 Belgium)
                                 │
                 ┌───────────────┴───────────────┐
                 ▼                               ▼
       [ Cloud PostgreSQL ]           [ address-service ]
     (Neon / Supabase / SQL)       (https://address.pranobkalitalabs.co.uk)
```

---

## 🛠️ Step-by-Step Deployment

### Step 1: Set Up Cloud PostgreSQL Database
For serverless cloud deployment, you can use:
- **[Neon.tech](https://neon.tech)** (100% Free Serverless Postgres) or **[Supabase](https://supabase.com)**
- Note your database credentials:
  - `DB_HOST`: e.g. `ep-cool-fog-12345.eu-central-1.aws.neon.tech`
  - `DB_PORT`: `5432`
  - `DB_NAME`: `authdb` (or `neondb`)
  - `DB_USERNAME`: `your_user`
  - `DB_PASSWORD`: `your_password`

---

### Step 2: Deploy Container to Google Cloud Run
1. Go to **[Google Cloud Console](https://console.cloud.google.com)** $\rightarrow$ Select project **`pranobkalitalabs`**.
2. Go to **Cloud Run** $\rightarrow$ Click **`Deploy container`** $\rightarrow$ **`Service`**.
3. Configure:
   - **Container image URL**: `docker.io/pkalita/auth-service:latest`
   - **Service name**: `auth-service`
   - **Region**: **`europe-west1 (Belgium)`** *(supports direct 1-click domain mapping)*
   - **Authentication**: `Allow unauthenticated invocations`
4. In **Containers, Volumes, Networking, Security**:
   - **Security**: Select `Default compute service account`.
   - **Variables & Secrets**: Add environment variables:
     - `SPRING_PROFILES_ACTIVE` = `prod`
     - `DB_HOST` = `<your-cloud-postgres-host>`
     - `DB_PORT` = `5432`
     - `DB_NAME` = `authdb`
     - `DB_USERNAME` = `<your-db-user>`
     - `DB_PASSWORD` = `<your-db-password>`
     - `ADDRESS_SERVICE_URL` = `https://address.pranobkalitalabs.co.uk`
     - `JWT_SECRET` = `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970`
     - `CORS_ALLOWED_ORIGINS` = `https://pranobkalitalabs.co.uk,https://*.pranobkalitalabs.co.uk`
5. Click **Create**!

---

### Step 3: Map Custom Domain (`auth.pranobkalitalabs.co.uk`)

1. In Cloud Run, go to **`Domain mappings`** $\rightarrow$ Click **`+ Add mapping`**.
2. Select service: **`auth-service (europe-west1)`**.
3. Enter domain: **`auth.pranobkalitalabs.co.uk`**.
4. In **Namecheap** $\rightarrow$ **Advanced DNS** $\rightarrow$ Click **`+ ADD NEW RECORD`**:
   - **Type**: `CNAME Record`
   - **Host**: `auth`
   - **Value**: `ghs.googlehosted.com`
   - **TTL**: `Automatic`
5. Save! Google Cloud will provision the free SSL certificate in ~10–15 minutes.

---

## 🔒 Production Smoke Test

```bash
# Health Check
curl https://auth.pranobkalitalabs.co.uk/actuator/health

# Swagger UI
open https://auth.pranobkalitalabs.co.uk/swagger-ui.html
```
