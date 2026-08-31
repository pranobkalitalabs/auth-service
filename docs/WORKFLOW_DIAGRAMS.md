# 📊 Auth Service - Visual Architecture & Workflow Diagrams

This document contains visual Mermaid sequence diagrams detailing the authentication, registration, address enrichment, and password reset flows within **`auth-service`**.

---

## 1. User Registration with UK Address Geocoding Flow

When a new user registers with a UK postcode, `auth-service` calls `address-service` to validate and enrich the user profile with coordinates before saving to PostgreSQL:

```mermaid
sequenceDiagram
    autonumber
    actor User as User / Postman
    participant Auth as auth-service (Port 8081)
    participant Addr as address-service (Port 8082)
    participant DB as PostgreSQL (Port 5432)

    User->>Auth: POST /api/v1/auth/register {email, password, postcode: "SW1A 2AA", ...}
    Auth->>DB: Check if email already exists
    
    alt Email Already Exists
        DB-->>Auth: User Found
        Auth-->>User: 400 Bad Request ("Email already registered")
    else Email is Unique
        Auth->>Addr: GET /api/v1/address/uk/lookup/SW1A 2AA
        
        alt Address Service Responds
            Addr-->>Auth: 200 OK {latitude: 51.5035, longitude: -0.1276, adminDistrict: "Westminster"}
            Auth->>Auth: Hash Password with BCrypt & Attach Coordinates
        else Address Service Offline
            Addr-->>Auth: Timeout / Error
            Auth->>Auth: Gracefully fallback (save profile without coordinates)
        end
        
        Auth->>DB: INSERT INTO users (email, password_hash, lat, lng, ...)
        Auth->>DB: INSERT INTO user_roles (user_id, role_id=ROLE_USER)
        Auth->>Auth: Generate Access Token (15m) & Refresh Token (7d)
        Auth-->>User: 201 Created {accessToken, refreshToken, userProfile}
    end
```

---

## 2. Authentication & JWT Token Refresh Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client as Client App / Tester
    participant Auth as auth-service (Port 8081)
    participant DB as PostgreSQL (Port 5432)

    Client->>Auth: POST /api/v1/auth/login {email, password}
    Auth->>DB: Find user by email
    DB-->>Auth: User record & BCrypt hash
    Auth->>Auth: Verify password against BCrypt hash
    
    alt Invalid Credentials
        Auth-->>Client: 401 Unauthorized ("Invalid email or password")
    else Valid Credentials
        Auth->>Auth: Generate JWT Access Token (HS256) & Refresh Token
        Auth->>DB: Save Refresh Token
        Auth-->>Client: 200 OK {accessToken, refreshToken, roles}
    end

    Note over Client,Auth: When Access Token expires after 15 minutes:
    Client->>Auth: POST /api/v1/auth/refresh-token {refreshToken}
    Auth->>DB: Validate token is active and not revoked
    DB-->>Auth: Token Valid
    Auth->>Auth: Issue New JWT Access Token
    Auth-->>Client: 200 OK {accessToken, refreshToken}
```

---

## 3. Password Reset Workflow

```mermaid
sequenceDiagram
    autonumber
    actor User as User / Postman
    participant Auth as auth-service (Port 8081)
    participant DB as PostgreSQL (Port 5432)
    participant Mail as Mailpit / SMTP

    User->>Auth: POST /api/v1/auth/forgot-password {email}
    Auth->>DB: Find user by email
    
    alt User Exists
        Auth->>Auth: Generate Secure Reset Token (UUID)
        Auth->>DB: INSERT INTO password_reset_tokens (token, expiry=1h)
        Auth->>Mail: Send Password Reset Email with Token Link
        Auth-->>User: 200 OK ("Password reset email sent")
    else User Not Found
        Auth-->>User: 200 OK (Generic response for security)
    end

    Note over User,Auth: User receives reset token and submits new password:
    User->>Auth: POST /api/v1/auth/reset-password {token, newPassword}
    Auth->>DB: Verify token exists, not expired, and not used
    Auth->>Auth: Hash new password with BCrypt
    Auth->>DB: UPDATE users SET password_hash = ...
    Auth->>DB: Mark reset token as used
    Auth-->>User: 200 OK ("Password reset successful")
```

---

## 📑 Next Reading
- 🏛️ [**Architecture & Service Overview**](./SERVICE_OVERVIEW.md)
- 🧪 [**QA & Tester Guide**](./TESTER_GUIDE.md)
- ☁️ [**GCP Deployment Guide**](./GCP_DEPLOYMENT.md)
- 🏠 [**Back to Main README**](../README.md)
