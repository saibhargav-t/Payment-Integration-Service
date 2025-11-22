# Core Payment Integration System

A secure and scalable microservices-based payment integration system built with Java Spring Boot, designed to handle payment validation, processing, and integration with the Trustly payment provider.

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Microservices](#microservices)
- [Security Features](#security-features)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Setup and Installation](#setup-and-installation)
- [Configuration](#configuration)
- [Testing](#testing)
- [Deployment](#deployment)
- [Design Patterns](#design-patterns)
- [Troubleshooting](#troubleshooting)

## Overview

The Core Payment Integration System is a comprehensive payment processing solution that provides secure payment validation, processing, and integration with external payment providers. The system is built using microservices architecture to ensure scalability, maintainability, and high availability.

### Key Features
- **Secure Payment Processing**: Multi-layered security with HmacSHA256 and RSA encryption
- **Modular Validation Framework**: Flexible rule-based validation system with Redis caching
- **Payment Status Tracking**: Complete transaction lifecycle monitoring
- **Trustly Integration**: Seamless integration with Trustly Deposit API
- **Robust Error Handling**: Centralized exception management with custom error codes
- **Mock Service for Testing**: Complete testing environment without external dependencies
- **High Availability**: Deployed on AWS with proper security measures

## Architecture

The system consists of three main microservices and one mock service for testing:

```
┌─────────────────────────────────────────────────────────────┐
│                   API Gateway / Load Balancer                │
└─────────────────────────────────────────────────────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
        ▼                        ▼                        ▼
┌───────────────┐      ┌───────────────┐      ┌──────────────────┐
│   Payment     │      │   Payment     │      │     Trustly      │
│  Validation   │─────▶│  Processing   │─────▶│     Provider     │
│   Service     │      │   Service     │      │     Service      │
│  (Port 8081)  │      │  (Port 8082)  │      │   (Port 8083)    │
└───────────────┘      └───────────────┘      └──────────────────┘
        │                        │                        │
        └────────────────────────┼────────────────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │                         │
                    │   Trustly Mock Service  │◄── Development/Testing Only
                    │      (Port 8084)        │
                    │                         │
                    └─────────────────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │    MySQL Database       │
                    │  (Payments & Validations)│
                    └─────────────────────────┘
                                 │
                                 ▼
                         ┌──────────────┐
                         │ Redis Cache  │
                         └──────────────┘
```

### Service Components

The system includes **four** main components:

1. **Payment Validation Service** (Port 8081) - Validates payment requests
2. **Payment Processing Service** (Port 8082) - Processes payments and manages transactions
3. **Trustly Provider Service** (Port 8083) - Integrates with Trustly API
4. **Trustly Mock Service** (Port 8084) - Mock implementation for testing

> **Note:** The Trustly Mock Service simulates the Trustly payment provider for development and testing purposes. In production, it is replaced with the actual Trustly Provider Service that communicates with the real Trustly API.

## Technologies Used

### Core Technologies
- **Java 24**
- **Spring Boot 3.5.3**
- **Spring Security**
- **Spring JDBC**
- **Maven 3.x**

### Database & Caching
- **MySQL 8.x** - Primary database
- **Redis** - Caching layer for validation rules

### Security
- **HmacSHA256** - Request authentication
- **RSA (SHA256withRSA)** - External API security
- **BouncyCastle** - Cryptographic operations

### Cloud & Infrastructure
- **AWS EC2** - Application hosting
- **AWS RDS** - MySQL database hosting
- **AWS Secrets Manager** - Secure credential management

### Testing
- **JUnit 5**
- **Mockito**

### Additional Libraries
- **Gson** - JSON processing
- **ModelMapper** - Object mapping
- **Lombok** - Boilerplate code reduction

## Microservices

### 1. Payment Validation Service (Port 8081)

Handles payment request validation with a modular rule-based framework.

**Key Responsibilities:**
- HMAC signature verification
- Customer ID validation
- Business rule validation with Redis caching
- Request authentication using Spring Security

**Design Patterns:**
- Factory Pattern (Validator selection)
- Strategy Pattern (Validation rules)

**Technology Stack:**
- Spring Boot 3.5.3
- Spring Security
- Gson for JSON processing
- ModelMapper for object mapping

### 2. Payment Processing Service (Port 8082)

Manages the core payment processing logic and transaction lifecycle.

**Key Responsibilities:**
- Transaction creation and management
- Payment status tracking (CREATED → INITIATED → PENDING → SUCCESS/FAILED)
- Integration with Trustly Provider Service
- Transaction log maintenance
- Error handling and recovery

**Design Patterns:**
- Factory Pattern (Status handler selection)
- State Pattern (Transaction status management)
- Builder Pattern (Request/Response objects)

**Technology Stack:**
- Spring Boot 3.5.3
- Spring JDBC
- MySQL
- RestClient for HTTP communication

### 3. Trustly Provider Service (Port 8083)

Interfaces with the Trustly payment provider API (or mock service in development).

**Key Responsibilities:**
- RSA signature generation and verification
- Communication with Trustly API
- Request/response transformation
- Error handling for provider failures

**Design Patterns:**
- Builder Pattern (Request/Response objects)
- Component Pattern (Service helpers)

**Technology Stack:**
- Spring Boot 3.5.3
- BouncyCastle for RSA operations
- RestClient for HTTP communication

### 4. Trustly Mock Service (Port 8084) - Testing Only

A mock implementation of the Trustly payment provider for development and testing purposes.

**Key Responsibilities:**
- Simulates Trustly Deposit API behavior
- RSA signature generation and verification (same as real Trustly)
- Provides success/failure simulation endpoints
- Serves demo payment page for testing user interactions
- Sends payment notifications to Processing Service

**Design Patterns:**
- Builder Pattern (Request/Response objects)
- Component Pattern (Service helpers)

**Environment Usage:**
- ✅ **Local Development** - Active
- ✅ **Dev Environment** - Active
- ✅ **QA Environment** - Active
- ❌ **UAT Environment** - Disabled (uses real Trustly)
- ❌ **Production** - Disabled (uses real Trustly)

**Key Endpoints:**
- `POST /payment/initiate` - Simulates deposit initiation
- `POST /payment/success/{paymentId}` - Simulates successful payment
- `POST /payment/fail/{paymentId}` - Simulates failed payment
- `GET /?token={paymentId}` - Demo payment page (HTML)

**Mock vs Real Service Configuration:**

| Aspect | Mock Service (Dev/QA) | Real Trustly Service (UAT/Prod) |
|--------|----------------------|----------------------------------|
| Endpoint | http://localhost:8084 | https://api.trustly.com |
| Authentication | Same RSA keys | Production RSA keys |
| Response Time | Instant | Real network latency |
| Payment Flow | Simulated | Actual bank integration |
| Notifications | Manual triggers | Real-time webhooks |

**Switching Between Mock and Real:**

Configuration in `payment-processing-service/application-{profile}.properties`:

```properties
# Development/Testing (Mock)
trustlyprovider.deposit.url=http://localhost:8084/payment/initiate

# Production (Real Trustly)
trustlyprovider.deposit.url=https://api.trustly.com/v1/deposits
```

**Testing with Mock Service:**

```bash
# 1. Start the mock service
cd trustly-mock-service/trustly-mock-service
mvn spring-boot:run

# 2. Access demo payment page
# After initiating payment, you'll get a URL like:
# http://localhost:8084/?token=<payment-id>

# 3. Test success scenario
curl -X POST http://localhost:8084/payment/success/<payment-id>

# 4. Test failure scenario
curl -X POST http://localhost:8084/payment/fail/<payment-id>
```

**Demo Payment Page:**

The mock service serves an HTML page (`src/main/resources/templates/index.html`) with two buttons:
- **SUCCESS** - Triggers successful payment callback
- **FAILURE** - Triggers failed payment callback

This simulates the user completing payment on Trustly's hosted page.

## Security Features

### 1. HmacSHA256 Authentication

Request authentication flow for Payment Validation Service:

```
1. Client generates HMAC signature using shared secret
2. Signature sent in 'hmac-signature' header
3. Server validates signature before processing
4. Invalid signatures result in 403 Unauthorized
```

**Implementation:**
- Secret Key: `THIS_IS_MY_SECRET`
- Algorithm: HmacSHA256
- Encoding: Base64

**Signature Generation Process:**
```java
// Convert request body to JSON
String jsonData = gson.toJson(paymentRequest);

// Generate HMAC
SecretKeySpec keySpec = new SecretKeySpec(
    secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
Mac mac = Mac.getInstance("HmacSHA256");
mac.init(keySpec);
byte[] signatureBytes = mac.doFinal(jsonData.getBytes(StandardCharsets.UTF_8));

// Encode to Base64
String signature = Base64.getEncoder().encodeToString(signatureBytes);
```

### 2. RSA Security (Trustly Integration)

External API security for Trustly Provider communication:

```
1. Private key signs outgoing requests
2. Public key verifies incoming responses
3. Algorithm: SHA256withRSA
4. Key format: PEM
```

**Signature Process:**
```java
// Serialize data in sorted order
String serializedData = serializeData(jsonNode);

// Create plain text: METHOD + UUID + SERIALIZED_DATA
String plainText = "Deposit" + uuid + serializedData;

// Sign with private key
Signature signature = Signature.getInstance("SHA256withRSA");
signature.initSign(privateKey);
signature.update(plainText.getBytes(StandardCharsets.UTF_8));
byte[] digitalSignature = signature.sign();
String signatureBase64 = Base64.getEncoder().encodeToString(digitalSignature);
```

### 3. Spring Security Configuration

- Stateless session management
- Custom filter chain (HmacFilter, ExceptionHandlerFilter)
- CSRF disabled for REST APIs
- Role-based access control ready

**Filter Chain:**
```
DisableEncodeUrlFilter → ExceptionHandlerFilter → LogoutFilter → HmacFilter → 
Authorization Filter → REST Controllers
```

## API Documentation

### Payment Validation Service

#### Create Payment
Creates and validates a new payment request.

**Endpoint:** `POST /payments`

**Headers:**
```
Content-Type: application/json
hmac-signature: <Base64-encoded-HMAC-SHA256-signature>
```

**Request Body:**
```json
{
  "amount": 100.00,
  "currency": "EUR",
  "paymentMethod": "APM",
  "paymentType": "SALE",
  "provider": "TRUSTLY",
  "customerID": "CUST123",
  "mobileNo": "+1234567890"
}
```

**Success Response (200 OK):**
```json
{
  "id": "uuid-string",
  "redirectUrl": "https://example.com/redirect?paymentId=uuid"
}
```

**Error Response (400 Bad Request):**
```json
{
  "errorCode": "10001",
  "errorMessage": "Customer ID is missing in the payment request"
}
```

**Status Codes:**
- `200 OK` - Payment validated successfully
- `400 Bad Request` - Validation failed
- `403 Forbidden` - Invalid HMAC signature
- `500 Internal Server Error` - Server error

---

### Payment Processing Service

#### Create Transaction
Creates a new payment transaction.

**Endpoint:** `POST /payments`

**Request Body:**
```json
{
  "userId": 123,
  "paymentMethod": "APM",
  "provider": "TRUSTLY",
  "paymentType": "SALE",
  "amount": 100.00,
  "currency": "EUR",
  "merchantTransactionReference": "MERCH-REF-123"
}
```

**Success Response (200 OK):**
```json
{
  "txnReference": "uuid-transaction-reference",
  "txnStatus": "CREATED"
}
```

#### Initiate Payment
Initiates payment processing with provider.

**Endpoint:** `POST /payments/{transactionReference}/initiate`

**Path Parameters:**
- `transactionReference` - UUID of the transaction

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "country": "LT",
  "locale": "en",
  "successUrl": "https://example.com/success",
  "failUrl": "https://example.com/fail"
}
```

**Success Response (200 OK):**
```json
{
  "txnReference": "uuid-transaction-reference",
  "txnStatus": "PENDING",
  "url": "https://trustly-provider.com/deposit?orderId=provider-order-id"
}
```

**Error Response (503 Service Unavailable):**
```json
{
  "errorCode": "20001",
  "errorMessage": "Unable to connect to Trustly Provider, please try later"
}
```

**Status Codes:**
- `200 OK` - Payment initiated successfully
- `400 Bad Request` - Invalid request
- `503 Service Unavailable` - Provider connection failed
- `500 Internal Server Error` - Server error

---

### Trustly Provider Service

#### Initiate Deposit
Processes deposit request and communicates with Trustly API.

**Endpoint:** `POST /v1/trustly/deposits`

**Request Body:**
```json
{
  "method": "Deposit",
  "version": "1.1",
  "params": {
    "Signature": "RSA-signature-base64",
    "UUID": "request-uuid",
    "Data": {
      "Username": "merchant-username",
      "Password": "merchant-password",
      "NotificationURL": "https://merchant.com/notify",
      "EndUserID": "user-123",
      "MessageID": "msg-123",
      "Attributes": {
        "Country": "LT",
        "Locale": "en",
        "Currency": "EUR",
        "Amount": 100.00,
        "Firstname": "John",
        "Lastname": "Doe",
        "Email": "john.doe@example.com",
        "SuccessURL": "https://merchant.com/success",
        "FailURL": "https://merchant.com/fail"
      }
    }
  }
}
```

**Success Response (200 OK):**
```json
{
  "version": "1.1",
  "result": {
    "signature": "RSA-signature-base64",
    "uuid": "request-uuid",
    "method": "Deposit",
    "data": {
      "orderid": "provider-order-id",
      "url": "https://trustly.com/payment?token=order-id"
    }
  }
}
```

**Error Response (401 Unauthorized):**
```json
{
  "version": "1.1",
  "error": {
    "name": "JSONRPCError",
    "code": "636",
    "message": "ERROR_UNABLE_TO_VERIFY_RSA_SIGNATURE",
    "error": {
      "signature": "error-signature",
      "uuid": "request-uuid",
      "method": "Deposit",
      "data": {
        "code": "636",
        "message": "ERROR_UNABLE_TO_VERIFY_RSA_SIGNATURE"
      }
    }
  }
}
```

---

### Trustly Mock Service (Testing Only)

#### Initiate Payment (Mock)
Mock implementation of Trustly deposit API.

**Endpoint:** `POST /payment/initiate`

**Request/Response:** Same as Trustly Provider Service above

#### Payment Success Callback
Simulates successful payment completion.

**Endpoint:** `POST /payment/success/{paymentId}`

**Path Parameters:**
- `paymentId` - Provider order ID

**Response:** `200 OK`

**Side Effects:**
- Updates transaction status to SUCCESS
- Sends notification to Processing Service
- Triggers callback to merchant success URL

#### Payment Failure Callback
Simulates payment failure.

**Endpoint:** `POST /payment/fail/{paymentId}`

**Path Parameters:**
- `paymentId` - Provider order ID

**Response:** `200 OK`

**Side Effects:**
- Updates transaction status to FAILED
- Sends notification to Processing Service
- Triggers callback to merchant fail URL

#### Demo Payment Page
Serves HTML page for manual testing.

**Endpoint:** `GET /?token={paymentId}`

**Query Parameters:**
- `token` - Payment ID

**Response:** HTML page with SUCCESS/FAILURE buttons

---

### Common Error Codes

#### Validation Service (10xxx)
- `10000` - Generic error
- `10001` - Customer ID missing
- `10002` - HMAC signature missing
- `10003` - Invalid HMAC signature

#### Processing Service (20xxx)
- `20000` - Generic error
- `20001` - Unable to connect to Trustly Provider
- `20002` - Error processing Trustly response

#### Trustly Provider/Mock (30xxx)
- `30001` - Generic exception
- `30002` - Failed to connect to Trustly
- `30003` - Failed to initiate payment
- `636` - Unable to verify RSA signature
- `637` - Username missing

## Database Schema

### Payments Database

#### Payment_Method Table
```sql
CREATE TABLE Payment_Method (
  id INT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  status TINYINT DEFAULT 1,
  creationDate TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2)
);
```

**Initial Data:**
```sql
INSERT INTO Payment_Method (id, name, status) 
VALUES (1, 'APM', 1);
```

#### Payment_Type Table
```sql
CREATE TABLE Payment_Type (
  id INT PRIMARY KEY,
  type VARCHAR(50) NOT NULL,
  status TINYINT DEFAULT 1,
  creationDate TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2)
);
```

**Initial Data:**
```sql
INSERT INTO Payment_Type (id, type, status) 
VALUES (1, 'SALE', 1);
```

#### Provider Table
```sql
CREATE TABLE Provider (
  id INT PRIMARY KEY AUTO_INCREMENT,
  providerName VARCHAR(50) NOT NULL,
  status TINYINT DEFAULT 1,
  creationDate TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2)
);
```

**Initial Data:**
```sql
INSERT INTO Provider (id, providerName, status) 
VALUES (1, 'TRUSTLY', 1);
```

#### Transaction_Status Table
```sql
CREATE TABLE Transaction_Status (
  id INT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  status TINYINT DEFAULT 1,
  creationDate TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2)
);
```

**Initial Data:**
```sql
INSERT INTO Transaction_Status (id, name, status) VALUES
(1, 'CREATED', 1),
(2, 'INITIATED', 1),
(3, 'PENDING', 1),
(4, 'SUCCESS', 1),
(5, 'FAILED', 1);
```

**Transaction Status Flow:**
```
CREATED → INITIATED → PENDING → SUCCESS
                              → FAILED
```

#### Transaction Table
```sql
CREATE TABLE Transaction (
  id INT PRIMARY KEY AUTO_INCREMENT,
  userId INT NOT NULL,
  paymentMethodId INT NOT NULL,
  providerId INT NOT NULL,
  paymentTypeId INT NOT NULL,
  txnStatusId INT NOT NULL,
  amount DECIMAL(19,2) DEFAULT 0.00,
  currency VARCHAR(3) NOT NULL,
  merchantTransactionReference VARCHAR(50) NOT NULL,
  txnReference VARCHAR(50) NOT NULL UNIQUE,
  providerReference VARCHAR(100),
  errorCode VARCHAR(500),
  errorMessage VARCHAR(1000),
  creationDate TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2),
  retryCount INT DEFAULT 0,
  
  FOREIGN KEY (paymentMethodId) REFERENCES Payment_Method(id),
  FOREIGN KEY (providerId) REFERENCES Provider(id),
  FOREIGN KEY (txnStatusId) REFERENCES Transaction_Status(id),
  FOREIGN KEY (paymentTypeId) REFERENCES Payment_Type(id),
  
  INDEX idx_txnReference (txnReference),
  INDEX idx_userId (userId),
  INDEX idx_creationDate (creationDate)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### Transaction_Log Table
```sql
CREATE TABLE Transaction_Log (
  id INT PRIMARY KEY AUTO_INCREMENT,
  transactionId INT NOT NULL,
  txnFromStatus VARCHAR(50) DEFAULT '-1',
  txnToStatus VARCHAR(50) DEFAULT '-1',
  creationDate TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2),
  
  FOREIGN KEY (transactionId) REFERENCES Transaction(id),
  INDEX idx_transactionId (transactionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### Validations Database

#### merchant_payment_request Table
```sql
CREATE TABLE merchant_payment_request (
  id INT PRIMARY KEY AUTO_INCREMENT,
  endUserID VARCHAR(100),
  merchantTransactionReference VARCHAR(50) NOT NULL UNIQUE,
  transactionRequest TEXT,
  creationDate TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2),
  
  INDEX idx_merchantTxnRef (merchantTransactionReference)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### users Table
```sql
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  endUserID VARCHAR(100) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL,
  phoneNumber VARCHAR(100),
  firstName VARCHAR(100) NOT NULL,
  lastName VARCHAR(100) NOT NULL,
  creationDate TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2),
  
  INDEX idx_endUserID (endUserID),
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### validation_rules Table
```sql
CREATE TABLE validation_rules (
  id INT PRIMARY KEY AUTO_INCREMENT,
  validatorName VARCHAR(50) NOT NULL UNIQUE,
  isActive BOOLEAN NOT NULL,
  priority SMALLINT NOT NULL,
  creationDate TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2),
  
  INDEX idx_priority (priority),
  INDEX idx_isActive (isActive)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Initial Data:**
```sql
INSERT INTO validation_rules (validatorName, isActive, priority) VALUES
('CHECK1_VALIDATOR_RULE', true, 0),
('DUPLICATION_TXN_RULE', true, 10),
('PAYMENT_ATTEMPT_THRESHOLD_RULE', true, 30);
```

**Validation Rule Priorities:**
- `0` - Initial validation checks
- `10` - Core business rules (duplication)
- `20` - Field validations
- `30` - Business constraints (thresholds)

#### validation_rules_params Table
```sql
CREATE TABLE validation_rules_params (
  id INT PRIMARY KEY AUTO_INCREMENT,
  validatorName VARCHAR(50) NOT NULL,
  paramName VARCHAR(200) NOT NULL,
  paramValue VARCHAR(200) NOT NULL,
  creationDate TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2),
  
  FOREIGN KEY (validatorName) REFERENCES validation_rules(validatorName),
  INDEX idx_validatorName (validatorName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Initial Data:**
```sql
INSERT INTO validation_rules_params (validatorName, paramName, paramValue) VALUES
('PAYMENT_ATTEMPT_THRESHOLD_RULE', 'durationInMins', '2'),
('PAYMENT_ATTEMPT_THRESHOLD_RULE', 'maxPaymentThreshold', '5');
```

**Redis Caching:**
- Validation rules and parameters are cached in Redis
- Cache key pattern: `validation:rules:{validatorName}`
- TTL: Configurable (default 1 hour)
- Cache invalidation on rule updates

## Setup and Installation

### Prerequisites
- **Java Development Kit (JDK) 24**
- **Maven 3.x**
- **MySQL 8.x**
- **Redis** (optional, for caching)
- **AWS CLI** (for deployment)
- **Git**

### Local Development Setup

#### 1. Clone the Repository
```bash
git clone <repository-url>
cd core-payment-system
```

#### 2. Database Setup

**Create Databases:**
```bash
# Login to MySQL
mysql -u root -p

# Run DDL scripts
mysql -u root -p < database-scripts/sprint2/ddl/ddl-script.sql
mysql -u root -p < database-scripts/sprint4/validation_ddl-script.sql

# Insert initial data
mysql -u root -p < database-scripts/sprint2/dml/dml-script.sql
mysql -u root -p < database-scripts/sprint4/validation_dml-script.sql
```

**Verify Database Setup:**
```sql
-- Check payments database
USE payments;
SHOW TABLES;
SELECT * FROM Transaction_Status;

-- Check validations database
USE validations;
SHOW TABLES;
SELECT * FROM validation_rules;
```

#### 3. Configure Application Properties

**Payment Validation Service:**

Edit `payment-validation-service/src/main/resources/application-local.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/validations
spring.datasource.username=validations
spring.datasource.password=validations
merchant.client.id=merchant-id-123
```

**Payment Processing Service:**

Edit `payment-processing-service/src/main/resources/application-dev.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/payments
spring.datasource.username=payments
spring.datasource.password=payments
trustlyprovider.deposit.url=http://localhost:8083/v1/trustly/deposits
```

**Trustly Mock Service:**

Edit `trustly-mock-service/src/main/resources/application-dev.properties`:
```properties
trustly.initiate.payment.url=http://localhost:8084/?token=
cpt.notification.url=http://localhost:8082/trustly/notification
```

#### 4. Build Services

```bash
# Build Payment Validation Service
cd payment-validation-service/payment-validation-service
mvn clean install
cd ../..

# Build Payment Processing Service
cd payment-processing-service/payments-processing-service
mvn clean install
cd ../..

# Build Trustly Provider Service
cd trustly-provider-service/trustly-provider-service
mvn clean install
cd ../..

# Build Trustly Mock Service
cd trustly-mock-service/trustly-mock-service
mvn clean install
cd ../..
```

#### 5. Run Services

Open **four** terminal windows and run:

```bash
# Terminal 1 - Validation Service
cd payment-validation-service/payment-validation-service
mvn spring-boot:run

# Terminal 2 - Processing Service
cd payment-processing-service/payments-processing-service
mvn spring-boot:run

# Terminal 3 - Trustly Provider Service
cd trustly-provider-service/trustly-provider-service
mvn spring-boot:run

# Terminal 4 - Trustly Mock Service (for testing)
cd trustly-mock-service/trustly-mock-service
mvn spring-boot:run
```

**Services will be available at:**
- Validation Service: http://localhost:8081
- Processing Service: http://localhost:8082
- Trustly Provider: http://localhost:8083
- **Trustly Mock**: http://localhost:8084 (testing only)

> **Note:** In production environments, only run the first three services. The Trustly Mock Service should only be used in development and testing environments.

#### 6. Verify Services

```bash
# Check health endpoints
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health

# Expected response for each:
# {"status":"UP"}
```

## Configuration

### Environment Profiles

Each service supports multiple profiles:
- `local` - Local development
- `dev` - Development environment
- `qa` - QA environment
- `uat` - UAT environment
- `prod` - Production environment

**Activate a profile:**
```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Using Java
java -jar -Dspring.profiles.active=prod application.jar
```

### Key Configuration Properties

#### Payment Validation Service
```properties
# Server Configuration
server.port=8081
spring.application.name=payment-validation-service

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/validations
spring.datasource.username=validations
spring.datasource.password=validations
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Security Configuration
merchant.client.id=merchant-id-123

# Validation Rules
validator.rules=CHECK1_VALIDATOR_RULE,CHECK2_VALIDATOR_RULE

# Logging
logging.level.org.springframework.security=TRACE
logging.level.com.hulkhiretech.payments=DEBUG
```

#### Payment Processing Service
```properties
# Server Configuration
server.port=8082
spring.application.name=payment-processing-service

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/payments
spring.datasource.username=payments
spring.datasource.password=payments
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Trustly Provider Integration
trustlyprovider.deposit.url=http://localhost:8083/v1/trustly/deposits

# Logging
logging.level.com.hulkhiretech.payments=DEBUG
```

#### Trustly Provider Service
```properties
# Server Configuration
server.port=8083
spring.application.name=trustly-provider-service

# Trustly API Configuration (Production)
trustly.api.url=https://api.trustly.com/v1
trustly.username=${TRUSTLY_USERNAME}
trustly.password=${TRUSTLY_PASSWORD}

# RSA Keys
trustly.private.key.path=classpath:private.pem
trustly.public.key.path=classpath:public_trustly.pem
```

#### Trustly Mock Service
```properties
# Server Configuration
server.port=8084
spring.application.name=trustly-mock-service

# URLs for payment flow
trustly.initiate.payment.url=http://localhost:8084/?token=
cpt.notification.url=http://localhost:8082/trustly/notification
```

**Environment-Specific Configuration:**

```properties
# application-local.properties (Development)
trustly.initiate.payment.url=http://localhost:8084/?token=

# application-dev.properties (Dev Server)
trustly.initiate.payment.url=http://dev-trustly-mock:8084/payment/

# application-prod.properties (Production - Real Trustly)
trustly.

```properties
# application-prod.properties (Production - Real Trustly)
trustly.initiate.payment.url=https://api.trustly.com/payment/
trustly.api.url=https://api.trustly.com/v1
trustly.username=${TRUSTLY_USERNAME}
trustly.password=${TRUSTLY_PASSWORD}
```

### Security Configuration

**HMAC Secret Configuration:**

Located in `HMacSHA256ServiceImpl.java`:
```java
String secretKey = "THIS_IS_MY_SECRET";
```

> **Production Note:** Store this in AWS Secrets Manager or environment variables.

**RSA Keys Configuration:**

Keys are located in `src/main/resources/`:
- `private.pem` - Service private key for signing
- `merchant-public.pem` - Merchant public key for verification
- `public_trustly.pem` - Trustly public key for verification

**Key Generation (if needed):**
```bash
# Generate RSA private key
openssl genrsa -out private.pem 2048

# Extract public key
openssl rsa -in private.pem -pubout -out public.pem

# Convert to PKCS8 format (if needed)
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt \
  -in private.pem -out private-pkcs8.pem
```

### Redis Configuration (Optional)

If using Redis for caching validation rules:

```properties
# Redis Configuration
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=
spring.cache.type=redis
spring.cache.redis.time-to-live=3600000
```

## Testing

### Unit Tests

Run unit tests for each service:

```bash
# Test Validation Service
cd payment-validation-service/payment-validation-service
mvn test

# Test Processing Service
cd payment-processing-service/payments-processing-service
mvn test

# Test Trustly Provider Service
cd trustly-provider-service/trustly-provider-service
mvn test

# Test Trustly Mock Service
cd trustly-mock-service/trustly-mock-service
mvn test
```

**Run all tests with coverage:**
```bash
mvn clean test jacoco:report
```

### Integration Testing with Mock Service

The Trustly Mock Service enables complete end-to-end testing without requiring real Trustly API access.

#### Complete Payment Flow Test

**Step 1: Generate HMAC Signature**

Use the HmacSHA256ServiceImpl utility:
```java
// Java code to generate signature
String jsonData = "{\"amount\":100.00,\"currency\":\"EUR\",\"paymentMethod\":\"APM\",\"paymentType\":\"SALE\",\"provider\":\"TRUSTLY\",\"customerID\":\"CUST123\",\"mobileNo\":\"+1234567890\"}";
String signature = hMacSHA256Service.generateHmacSHA256Signature(jsonData);
System.out.println("Signature: " + signature);
```

**Step 2: Create and Validate Payment**
```bash
curl -X POST http://localhost:8081/payments \
  -H "Content-Type: application/json" \
  -H "hmac-signature: <generated-signature>" \
  -d '{
    "amount": 100.00,
    "currency": "EUR",
    "paymentMethod": "APM",
    "paymentType": "SALE",
    "provider": "TRUSTLY",
    "customerID": "CUST123",
    "mobileNo": "+1234567890"
  }'

# Expected Response:
# {
#   "id": "uuid-123",
#   "redirectUrl": "https://example.com/redirect?paymentId=uuid"
# }
```

**Step 3: Create Transaction**
```bash
curl -X POST http://localhost:8082/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123,
    "paymentMethod": "APM",
    "provider": "TRUSTLY",
    "paymentType": "SALE",
    "amount": 100.00,
    "currency": "EUR",
    "merchantTransactionReference": "MERCH-REF-123"
  }'

# Expected Response:
# {
#   "txnReference": "uuid-456",
#   "txnStatus": "CREATED"
# }
```

**Step 4: Initiate Payment**
```bash
curl -X POST http://localhost:8082/payments/uuid-456/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "country": "LT",
    "locale": "en",
    "successUrl": "https://example.com/success",
    "failUrl": "https://example.com/fail"
  }'

# Expected Response:
# {
#   "txnReference": "uuid-456",
#   "txnStatus": "PENDING",
#   "url": "http://localhost:8084/?token=mock-order-id"
# }
```

**Step 5: Simulate Payment Completion**

Option A - Use Web Interface:
```bash
# Open the URL in browser
http://localhost:8084/?token=mock-order-id

# Click SUCCESS or FAILURE button
```

Option B - Use API:
```bash
# Test Success Flow
curl -X POST http://localhost:8084/payment/success/mock-order-id

# Test Failure Flow
curl -X POST http://localhost:8084/payment/fail/mock-order-id
```

**Step 6: Verify Transaction Status**
```sql
-- Check transaction status in database
SELECT txnReference, txnStatusId, providerReference, errorCode, errorMessage
FROM payments.Transaction 
WHERE txnReference = 'uuid-456';

-- Check transaction logs
SELECT * FROM payments.Transaction_Log 
WHERE transactionId = (
  SELECT id FROM payments.Transaction WHERE txnReference = 'uuid-456'
);

-- Expected: Status should be 4 (SUCCESS) or 5 (FAILED)
```

### Automated Test Scenarios

**Success Flow Test Script:**
```bash
#!/bin/bash

# Variables
BASE_URL_VALIDATION="http://localhost:8081"
BASE_URL_PROCESSING="http://localhost:8082"
BASE_URL_MOCK="http://localhost:8084"

# Step 1: Create transaction
TXN_RESPONSE=$(curl -s -X POST $BASE_URL_PROCESSING/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123,
    "paymentMethod": "APM",
    "provider": "TRUSTLY",
    "paymentType": "SALE",
    "amount": 100.00,
    "currency": "EUR",
    "merchantTransactionReference": "TEST-'$(date +%s)'"
  }')

TXN_REF=$(echo $TXN_RESPONSE | jq -r '.txnReference')
echo "Created transaction: $TXN_REF"

# Step 2: Initiate payment
INIT_RESPONSE=$(curl -s -X POST $BASE_URL_PROCESSING/payments/$TXN_REF/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "country": "LT",
    "locale": "en",
    "successUrl": "https://example.com/success",
    "failUrl": "https://example.com/fail"
  }')

PAYMENT_URL=$(echo $INIT_RESPONSE | jq -r '.url')
PAYMENT_ID=$(echo $PAYMENT_URL | grep -oP 'token=\K[^&]+')
echo "Payment initiated: $PAYMENT_ID"

# Step 3: Simulate success
curl -s -X POST $BASE_URL_MOCK/payment/success/$PAYMENT_ID
echo "Payment completed successfully"

# Step 4: Wait for notification processing
sleep 2

# Step 5: Verify status
echo "Verifying transaction status..."
mysql -u payments -ppasswords -e "SELECT txnReference, txnStatusId FROM payments.Transaction WHERE txnReference='$TXN_REF';"
```

**Failure Flow Test Script:**
```bash
#!/bin/bash
# Similar to above, but calls /payment/fail/{paymentId}
# Expected result: txnStatusId = 5 (FAILED)
```

### Mock Service Features for Testing

1. **RSA Signature Validation** - Same algorithm as production
2. **Error Scenarios**:
   - Missing username (Error 637)
   - Invalid signature (Error 636)
   - Generic errors (Error 30001)
3. **Payment Simulation** - Instant success/failure responses
4. **Notification Callbacks** - Automatic webhook calls to Processing Service
5. **HTML Demo Page** - Manual testing interface with buttons

### Load Testing

Use Apache JMeter or similar tools:

```bash
# Install JMeter
wget https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.6.3.tgz
tar -xzf apache-jmeter-5.6.3.tgz

# Create test plan for payment creation
# Target: 100 requests/second for 5 minutes
# Monitor: Response time, error rate, throughput
```

**Performance Benchmarks:**
- Payment Validation: < 200ms response time
- Transaction Creation: < 500ms response time
- Payment Initiation: < 1000ms response time (including provider call)

## Deployment

### Production Deployment (Without Mock Service)

**Important:** The Trustly Mock Service should NOT be deployed to production environments.

#### Service Deployment Matrix

| Service | Local | Dev | QA | UAT | Prod |
|---------|-------|-----|-----|-----|------|
| Validation Service | ✅ | ✅ | ✅ | ✅ | ✅ |
| Processing Service | ✅ | ✅ | ✅ | ✅ | ✅ |
| Trustly Provider Service | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Trustly Mock Service** | ✅ | ✅ | ✅ | ❌ | ❌ |

### AWS Deployment

#### Prerequisites
- AWS Account with appropriate permissions
- 3 EC2 instances (t3.medium or higher)
- RDS MySQL instance (db.t3.medium or higher)
- Security groups configured
- Elastic Load Balancer (optional)
- Route 53 for DNS (optional)

#### Architecture Overview

```
Internet
   │
   ▼
┌──────────────┐
│  Route 53    │ (DNS)
└──────────────┘
   │
   ▼
┌──────────────┐
│     ALB      │ (Application Load Balancer)
└──────────────┘
   │
   ├─────────────────────────┬─────────────────────────┐
   ▼                         ▼                         ▼
┌──────────┐         ┌──────────┐         ┌──────────┐
│   EC2    │         │   EC2    │         │   EC2    │
│ Port 8081│         │ Port 8082│         │ Port 8083│
│Validation│         │Processing│         │ Provider │
└──────────┘         └──────────┘         └──────────┘
   │                         │                         │
   └─────────────────────────┴─────────────────────────┘
                            │
                            ▼
                    ┌──────────────┐
                    │   AWS RDS    │
                    │    MySQL     │
                    └──────────────┘
                            │
                            ▼
                    ┌──────────────┐
                    │AWS ElastiCache│
                    │    Redis     │
                    └──────────────┘
```

#### Step 1: Prepare Application

**Build Production JARs:**
```bash
# Build Validation Service
cd payment-validation-service/payment-validation-service
mvn clean package -Pprod

# Build Processing Service
cd ../../payment-processing-service/payments-processing-service
mvn clean package -Pprod

# Build Trustly Provider Service
cd ../../trustly-provider-service/trustly-provider-service
mvn clean package -Pprod

# DO NOT build trustly-mock-service for production
```

**JARs will be located in:**
- `payment-validation-service/target/payment-validation-service.jar`
- `payment-processing-service/target/payment-processing-service.jar`
- `trustly-provider-service/target/trustly-provider-service.jar`

#### Step 2: Configure AWS Infrastructure

**Security Groups:**
```bash
# Create security group for services
aws ec2 create-security-group \
  --group-name payment-services-sg \
  --description "Security group for payment services"

# Allow inbound traffic on service ports
aws ec2 authorize-security-group-ingress \
  --group-name payment-services-sg \
  --protocol tcp --port 8081 --cidr 10.0.0.0/16

aws ec2 authorize-security-group-ingress \
  --group-name payment-services-sg \
  --protocol tcp --port 8082 --cidr 10.0.0.0/16

aws ec2 authorize-security-group-ingress \
  --group-name payment-services-sg \
  --protocol tcp --port 8083 --cidr 10.0.0.0/16

# Allow SSH
aws ec2 authorize-security-group-ingress \
  --group-name payment-services-sg \
  --protocol tcp --port 22 --cidr your-ip/32
```

**RDS Configuration:**
```bash
# Create RDS MySQL instance
aws rds create-db-instance \
  --db-instance-identifier payment-db-prod \
  --db-instance-class db.t3.medium \
  --engine mysql \
  --engine-version 8.0.35 \
  --master-username admin \
  --master-user-password <secure-password> \
  --allocated-storage 100 \
  --vpc-security-group-ids sg-xxxxx \
  --db-subnet-group-name default \
  --backup-retention-period 7 \
  --multi-az
```

#### Step 3: Store Secrets in AWS Secrets Manager

```bash
# Database credentials
aws secretsmanager create-secret \
  --name prod/payment-system/db-credentials \
  --secret-string '{
    "username":"payments",
    "password":"<secure-password>",
    "host":"payment-db-prod.xxxxx.rds.amazonaws.com",
    "port":"3306"
  }'

# Trustly credentials
aws secretsmanager create-secret \
  --name prod/payment-system/trustly-credentials \
  --secret-string '{
    "username":"<trustly-username>",
    "password":"<trustly-password>",
    "apiUrl":"https://api.trustly.com/v1"
  }'

# HMAC secret
aws secretsmanager create-secret \
  --name prod/payment-system/hmac-secret \
  --secret-string '<secure-hmac-secret>'

# RSA private key
aws secretsmanager create-secret \
  --name prod/payment-system/trustly-private-key \
  --secret-string file://private.pem
```

#### Step 4: Launch EC2 Instances

```bash
# Launch instances for each service
aws ec2 run-instances \
  --image-id ami-xxxxx \
  --count 1 \
  --instance-type t3.medium \
  --key-name your-key-pair \
  --security-group-ids sg-xxxxx \
  --subnet-id subnet-xxxxx \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=payment-validation-service}]' \
  --iam-instance-profile Name=payment-services-role

# Repeat for processing and provider services
```

#### Step 5: Deploy Application

**Upload JARs to EC2:**
```bash
# Upload to each EC2 instance
scp -i your-key.pem \
  payment-validation-service.jar \
  ec2-user@<validation-ec2-ip>:/home/ec2-user/

scp -i your-key.pem \
  payment-processing-service.jar \
  ec2-user@<processing-ec2-ip>:/home/ec2-user/

scp -i your-key.pem \
  trustly-provider-service.jar \
  ec2-user@<provider-ec2-ip>:/home/ec2-user/
```

**Create systemd service files:**

SSH into each EC2 instance and create service files:

```bash
# On Validation Service EC2
sudo nano /etc/systemd/system/payment-validation.service
```

```ini
[Unit]
Description=Payment Validation Service
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar \
  -Dspring.profiles.active=prod \
  -Xms512m -Xmx1024m \
  /home/ec2-user/payment-validation-service.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# On Processing Service EC2
sudo nano /etc/systemd/system/payment-processing.service
```

```ini
[Unit]
Description=Payment Processing Service
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar \
  -Dspring.profiles.active=prod \
  -Xms1024m -Xmx2048m \
  /home/ec2-user/payment-processing-service.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# On Provider Service EC2
sudo nano /etc/systemd/system/trustly-provider.service
```

```ini
[Unit]
Description=Trustly Provider Service
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar \
  -Dspring.profiles.active=prod \
  -Xms512m -Xmx1024m \
  /home/ec2-user/trustly-provider-service.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**Start services:**
```bash
# On each EC2 instance
sudo systemctl daemon-reload
sudo systemctl start payment-validation  # or payment-processing, or trustly-provider
sudo systemctl enable payment-validation # Enable auto-start on boot
sudo systemctl status payment-validation # Check status
```

#### Step 6: Configure Application Load Balancer

```bash
# Create target groups
aws elbv2 create-target-group \
  --name payment-validation-tg \
  --protocol HTTP \
  --port 8081 \
  --vpc-id vpc-xxxxx \
  --health-check-path /actuator/health

# Register targets
aws elbv2 register-targets \
  --target-group-arn arn:aws:elasticloadbalancing:region:account:targetgroup/payment-validation-tg/xxxxx \
  --targets Id=i-validation-instance-id

# Create ALB and listeners
aws elbv2 create-load-balancer \
  --name payment-services-alb \
  --subnets subnet-xxxxx subnet-yyyyy \
  --security-groups sg-xxxxx
```

#### Step 7: Production Configuration

**application-prod.properties for Processing Service:**
```properties
# Server Configuration
server.port=8082
spring.profiles.active=prod

# Database Configuration (using Secrets Manager)
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/payments
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Trustly Configuration (Real API)
trustlyprovider.deposit.url=https://api.trustly.com/v1/deposits
trustly.username=${TRUSTLY_USERNAME}
trustly.password=${TRUSTLY_PASSWORD}

# Security
trustly.private.key.path=file:/secure/trustly-production-private.pem
trustly.public.key.path=file:/secure/trustly-production-public.pem

# Logging
logging.file.name=/var/log/payment-processing-service/application.log
logging.level.com.hulkhiretech.payments=INFO
logging.level.root=WARN
```

**Environment Variables (set in systemd service file or EC2 user data):**
```bash
# Add to ExecStart in systemd file:
Environment="DB_HOST=payment-db-prod.xxxxx.rds.amazonaws.com"
Environment="DB_PORT=3306"
Environment="DB_USERNAME=payments"
Environment="DB_PASSWORD=<from-secrets-manager>"
Environment="TRUSTLY_USERNAME=<from-secrets-manager>"
Environment="TRUSTLY_PASSWORD=<from-secrets-manager>"
```

### Database Migration to RDS

```sql
-- Connect to RDS
mysql -h payment-db-prod.xxxxx.rds.amazonaws.com -u admin -p

-- Run migration scripts
source /path/to/database-scripts/sprint2/ddl/ddl-script.sql
source /path/to/database-scripts/sprint4/validation_ddl-script.sql
source /path/to/database-scripts/sprint2/dml/dml-script.sql
source /path/to/database-scripts/sprint4/validation_dml-script.sql

-- Verify
USE payments;
SHOW TABLES;
SELECT * FROM Transaction_Status;
```

### Security Considerations for Production

1. **Secrets Management**
   - Store all credentials in AWS Secrets Manager
   - Rotate keys regularly (90 days)
   - Use IAM roles for service access
   - Never commit secrets to version control

2. **Network Security**
   - Configure security groups to allow only necessary traffic
   - Use private subnets for services
   - Enable VPC flow logs
   - Use AWS WAF for web application firewall

3. **Application Security**
   - Enable SSL/TLS for all endpoints
   - Use HTTPS for ALB listeners
   - Implement rate limiting
   - Enable Spring Security features
   - Keep dependencies updated

4. **Database Security**
   - Enable encryption at rest
   - Enable encryption in transit (SSL)
   - Regular backups (7-day retention minimum)
   - Enable Multi-AZ for high availability
   - Restrict access via security groups

5. **Monitoring and Alerting**
   - CloudWatch metrics and alarms
   - Application logs to CloudWatch Logs
   - Set up alerts for errors and high latency
   - Monitor database performance

## Design Patterns

### 1. Factory Pattern

**Usage:** Creating appropriate handlers based on type/status

**TransactionStatusFactory:**
```java
public class TransactionStatusFactory {
    private final ApplicationContext applicationContext;
    
    public TransactionStatusHandler getTransactionHandler(
            TransactionStatusEnum status) {
        switch (status) {
            case CREATED:
                return applicationContext.getBean(CreatedStatusHandler.class);
            case INITIATED:
                return applicationContext.getBean(InitiatedStatusHandler.class);
            case PENDING:
                return applicationContext.getBean(PendingStatusHandler.class);
            case FAILED:
                return applicationContext.getBean(FailedStatusHandler.class);
            default:
                return null;
        }
    }
}
```

**ValidatorEnum:**
```java
public enum ValidatorEnum {
    CHECK1_VALIDATOR_RULE("CHECK1_VALIDATOR_RULE", Check1Validator.class),
    CHECK2_VALIDATOR_RULE("CHECK2_VALIDATOR_RULE", Check2Validator.class);
    
    public static Class<? extends Validator> getValidatorClassByName(String name) {
        ValidatorEnum type = NAME_TO_ENUM_MAP.get(name);
        return type != null ? type.validatorClass : null;
    }
}
```

### 2. Strategy Pattern

**Usage:** Interchangeable validation and status handling algorithms

**Validator Interface:**
```java
public interface Validator {
    void validate(PaymentRequest paymentRequest);
}
```

**Implementations:**
```java
@Service
public class Check1Validator implements Validator {
    @Override
    public void validate(PaymentRequest paymentRequest) {
        if (paymentRequest.getCustomerID() == null) {
            throw new ValidationException("10001", "Customer ID missing");
        }
    }
}

@Service
public class Check2Validator implements Validator {
    @Override
    public void validate(PaymentRequest paymentRequest) {
        // Different validation logic
    }
}
```

### 3. State Pattern

**Usage:** Managing transaction status transitions

**Transaction State Transitions:**
```
CREATED → INITIATED → PENDING → SUCCESS
                              → FAILED
```

**TransactionStatusHandler Interface:**
```java
public interface TransactionStatusHandler {
    TransactionDto handleTransactionStatus(TransactionDto transactionDto);
}
```

**State-Specific Handlers:**
```java
@Service
public class CreatedStatusHandler implements TransactionStatusHandler {
    @Override
    public TransactionDto handleTransactionStatus(TransactionDto dto) {
        // Save transaction to database
        return dto;
    }
}

@Service
public class InitiatedStatusHandler implements TransactionStatusHandler {
    @Override
    public TransactionDto handleTransactionStatus(TransactionDto dto) {
        // Update transaction status
        return dto;
    }
}
```

### 4. Builder Pattern

**Usage:** Constructing complex objects

**Example:**
```java
@Data
@Builder
public class TrustlyProviderDepositRequest {
    private String txnReference;
    private String endUserId;
    private Double amount;
    private String currency;
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private String locale;
    private String successUrl;
    private String failUrl;
}

// Usage:
TrustlyProviderDepositRequest request = TrustlyProviderDepositRequest.builder()
    .txnReference(txnReference)
    .endUserId(userId)
    .amount(amount)
    .currency("EUR")
    .firstName("John")
    .lastName("Doe")
    .email("john@example.com")
    .country("LT")
    .locale("en")
    .successUrl(successUrl)
    .failUrl(failUrl)
    .build();
```

### 5. Template Method Pattern

**Usage:** Status handling with common workflow

```java
public abstract class AbstractStatusHandler implements TransactionStatusHandler {
    
    @Override
    public final TransactionDto handleTransactionStatus(TransactionDto dto) {
        validate(dto);
        TransactionDto updated = process(dto);
        log(updated);
        return updated;
    }
    
    protected abstract void validate(TransactionDto dto);
    protected abstract TransactionDto process(TransactionDto dto);
    protected abstract void log(TransactionDto dto);
}
```

### 6. Adapter Pattern

**Usage:** Converting between different object models

**ModelMapper Configuration:**
```java
@Configuration
public class AppConfig {
    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Custom converters for enum to ID mapping
        TypeMap<TransactionDto, Transaction> typeMap = 
            modelMapper.createTypeMap(TransactionDto.class, Transaction.class);
        
        typeMap.addMappings(mapper -> {
            mapper.using(paymentMethodEnumConverter)
                  .map(TransactionDto::getPaymentMethod, 
                       Transaction::setPaymentMethodId);
        });
        
        return modelMapper;
    }
}
```

## Monitoring and Logging

### Health Checks

Each service exposes Spring Boot Actuator endpoints:

```bash
# Health check
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health

# Expected Response:
# {"status":"UP"}

# Detailed health information (if enabled)
curl http://localhost:8082/actuator/health/db
curl http://localhost:8082/actuator/health/diskSpace
```

### Application Metrics

```bash
# Metrics endpoint
curl http://localhost:8082/actuator/metrics

# Specific metrics
curl http://localhost:8082/actuator/metrics/jvm.memory.used
curl http://localhost:8082/actuator/metrics/http.server.requests
```

### Logging Configuration

**Log Levels by Environment:**

```properties
# Development
logging.level.com.hulkhiretech.payments=DEBUG
logging.level.org.springframework=INFO

# Production
logging.level.com.hulkhiretech.payments=INFO
logging.level.org.springframework=WARN
logging.level.root=ERROR
```

**Log Locations:**
- **Local:** Console output
- **Dev/QA:** `/var/log/{service-name}/application.log`
- **Production:** `/var/log/{service-name}/application.log` + CloudWatch Logs

**Log Pattern:**
```properties
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

### CloudWatch Integration

**Install CloudWatch Agent on EC2:**
```bash
sudo yum install amazon-cloudwatch-agent

# Configure agent
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-config-wizard
```

**CloudWatch Logs Configuration:**
```json
{
  "logs": {
    "logs_collected": {
      "files": {
        "collect_list": [
          {
            "file_path": "/var/log/payment-processing-service/application.log",
            "log_group_name": "/aws/ec2/payment-services",
            "log_stream_name": "{instance_id}/processing-service"
          }
        ]
      }
    }
  }
}
```

### Monitoring Dashboards

**Key Metrics to Monitor:**

1. **Application Metrics:**
   - Request count
   - Response time (P50, P95, P99)
   - Error rate
   - Active sessions

2. **Database Metrics:**
   - Connection pool usage
   - Query execution time
   - Slow queries
   - Deadlocks

3. **System Metrics:**
   - CPU utilization
   - Memory usage
   - Disk I/O
   - Network throughput

4. **Business Metrics:**
   - Transaction success rate
   - Transaction volume by status
   - Average transaction amount
   - Payment provider response time

## Troubleshooting

### Common Issues

#### Issue 1: Database Connection Failed

**Symptoms:**
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: 
Communications link failure
```

**Solutions:**
```bash
# 1. Check MySQL service is running
sudo systemctl status mysql

# 2. Verify credentials
mysql -u payments -ppasswords

# 3. Check connection string
# Ensure: jdbc:mysql://localhost:3306/payments

# 4. Verify user permissions
mysql -u root -p
GRANT ALL ON payments.* TO 'payments'@'%';
FLUSH PRIVILEGES;

# 5. Check firewall
sudo ufw allow 3306/tcp
```

#### Issue 2: HMAC Signature Validation Failed

**Symptoms:**
```json
{
  "errorCode": "10003",
  "errorMessage": "HMAC signature is invalid"
}
```

**Solutions:**
```bash
# 1. Verify secret key matches
# Check: HMacSHA256ServiceImpl.java
# String secretKey = "THIS_IS_MY_SECRET";

# 2. ```bash
# 2. Ensure request body is identical
# The signature must be generated from EXACT JSON (no whitespace changes)

# 3. Verify signature generation
# Use provided utility to generate correct signature
java -cp payment-validation-service.jar \
  com.hulkhiretech.payments.services.impl.HMacSHA256ServiceImpl

# 4. Check header name
# Must be exactly: "hmac-signature" (lowercase with hyphen)

# 5. Debug signature comparison
# Enable DEBUG logging:
logging.level.com.hulkhiretech.payments.security=DEBUG
```

**Correct Signature Generation:**
```java
// 1. Create JSON string (no extra spaces)
String jsonData = gson.toJson(paymentRequest);

// 2. Generate signature
String signature = hMacSHA256Service.generateHmacSHA256Signature(jsonData);

// 3. Send in header
headers.put("hmac-signature", signature);
```

#### Issue 3: RSA Signature Verification Failed

**Symptoms:**
```json
{
  "errorCode": "636",
  "message": "ERROR_UNABLE_TO_VERIFY_RSA_SIGNATURE"
}
```

**Solutions:**
```bash
# 1. Verify PEM files are correctly placed
ls -la src/main/resources/*.pem
# Expected files:
# - private.pem
# - merchant-public.pem
# - public_trustly.pem

# 2. Check PEM file format
openssl rsa -in private.pem -text -noout

# 3. Ensure no extra whitespace in PEM files
cat private.pem | head -1
# Should be exactly: -----BEGIN RSA PRIVATE KEY-----

# 4. Verify signature generation order
# Plain text format: METHOD + UUID + SERIALIZED_DATA
# Example: "Deposit67d6c2f3-51b3-4eed-ad1a-16b4c4063c33AmountXXX..."

# 5. Check BouncyCastle provider
# Ensure dependency is included:
mvn dependency:tree | grep bouncycastle
```

**Debug RSA Signature:**
```java
// Enable detailed logging
log.info("Plain text for signature: {}", plainText);
log.info("Generated signature: {}", signature);
log.info("Signature length: {}", signature.length());
```

#### Issue 4: Port Already in Use

**Symptoms:**
```
Web server failed to start. Port 8081 was already in use.
```

**Solutions:**
```bash
# 1. Find process using the port
lsof -i :8081
# or
netstat -tulpn | grep 8081

# 2. Kill the process
kill -9 <PID>

# 3. Change port if needed
# Edit application.properties:
server.port=8091

# 4. Check all services
for port in 8081 8082 8083 8084; do
  echo "Port $port:"
  lsof -i :$port
done
```

#### Issue 5: Transaction Stuck in PENDING Status

**Symptoms:**
Transaction remains in PENDING status indefinitely.

**Solutions:**
```bash
# 1. Check if mock service is running
curl http://localhost:8084/actuator/health

# 2. Verify notification URL is correct
# Check: trustly-mock-service application.properties
# cpt.notification.url=http://localhost:8082/trustly/notification

# 3. Manually trigger completion (testing only)
curl -X POST http://localhost:8084/payment/success/{paymentId}

# 4. Check notification endpoint logs
tail -f payment-processing-service/logs/application.log | grep "notification"

# 5. Verify transaction status in database
mysql -u payments -ppasswords -e "
  SELECT id, txnReference, txnStatusId, providerReference 
  FROM payments.Transaction 
  WHERE txnStatusId = 3 
  ORDER BY creationDate DESC 
  LIMIT 10;
"

# 6. Check Transaction_Log for status transitions
mysql -u payments -ppasswords -e "
  SELECT tl.*, t.txnReference 
  FROM payments.Transaction_Log tl
  JOIN payments.Transaction t ON tl.transactionId = t.id
  WHERE t.txnReference = 'your-txn-reference'
  ORDER BY tl.creationDate;
"
```

#### Issue 6: Mock Service Payment Page Not Loading

**Symptoms:**
Blank page or 404 when accessing `http://localhost:8084/?token=xxx`

**Solutions:**
```bash
# 1. Verify mock service is running
curl http://localhost:8084/actuator/health

# 2. Check HTML template exists
ls -la trustly-mock-service/src/main/resources/templates/index.html

# 3. Verify token parameter
# URL must include token: http://localhost:8084/?token=payment-id

# 4. Check service logs
tail -f trustly-mock-service/logs/application.log

# 5. Test with curl
curl -v "http://localhost:8084/?token=test"

# 6. Verify FaviconController isn't interfering
# Check: com.cpt.payments.controller.FaviconController
```

#### Issue 7: Real Trustly Integration Not Working in UAT

**Symptoms:**
Trustly API calls fail with authentication or network errors.

**Solutions:**
```bash
# 1. Verify configuration points to real Trustly endpoint
grep "trustlyprovider.deposit.url" application-prod.properties
# Should NOT contain 'localhost' or 'mock'
# Should be: https://api.trustly.com/v1/deposits

# 2. Check Trustly credentials
# Verify in AWS Secrets Manager:
aws secretsmanager get-secret-value \
  --secret-id prod/payment-system/trustly-credentials

# 3. Verify production RSA keys are configured
ls -la /secure/trustly-production-*.pem

# 4. Test Trustly API connectivity
curl -X POST https://api.trustly.com/v1/deposits \
  -H "Content-Type: application/json" \
  -d '{"method":"Deposit","version":"1.1",...}'

# 5. Check network connectivity from EC2
# From EC2 instance:
curl -v https://api.trustly.com

# 6. Verify security group allows outbound HTTPS
aws ec2 describe-security-groups \
  --group-ids sg-xxxxx

# 7. Enable detailed Trustly API logging
logging.level.com.hulkhiretech.payments.http=DEBUG
```

#### Issue 8: Validation Rules Not Loading from Database

**Symptoms:**
Validation always passes or fails unexpectedly.

**Solutions:**
```bash
# 1. Verify validation rules exist in database
mysql -u validations -pvalidations -e "
  SELECT * FROM validations.validation_rules;
"

# 2. Check rule parameters
mysql -u validations -pvalidations -e "
  SELECT vr.validatorName, vr.isActive, vr.priority, 
         vrp.paramName, vrp.paramValue
  FROM validations.validation_rules vr
  LEFT JOIN validations.validation_rules_params vrp 
    ON vr.validatorName = vrp.validatorName
  ORDER BY vr.priority;
"

# 3. Verify validator configuration
# Check application.properties:
validator.rules=CHECK1_VALIDATOR_RULE,CHECK2_VALIDATOR_RULE

# 4. Check Redis cache (if enabled)
redis-cli
> KEYS validation:*
> GET validation:rules:CHECK1_VALIDATOR_RULE

# 5. Clear cache and reload
redis-cli FLUSHDB

# 6. Enable validation debug logging
logging.level.com.hulkhiretech.payments.services.impl.validator=DEBUG
```

#### Issue 9: High Memory Usage

**Symptoms:**
```
java.lang.OutOfMemoryError: Java heap space
```

**Solutions:**
```bash
# 1. Check current memory settings
ps aux | grep java

# 2. Increase heap size
java -jar -Xms1024m -Xmx2048m application.jar

# 3. Update systemd service file
sudo nano /etc/systemd/system/payment-processing.service
# Add to ExecStart:
# -Xms1024m -Xmx2048m

# 4. Monitor memory usage
jmap -heap <PID>
jstat -gc <PID> 1000

# 5. Generate heap dump for analysis
jmap -dump:format=b,file=/tmp/heap-dump.hprof <PID>

# 6. Analyze with Eclipse MAT or VisualVM

# 7. Check for memory leaks
# Look for:
# - Unclosed database connections
# - Large collections not being cleared
# - Static references holding objects

# 8. Enable GC logging
-Xlog:gc*:file=/var/log/gc.log:time,uptime:filecount=5,filesize=100M
```

#### Issue 10: Slow Database Queries

**Symptoms:**
High response times, timeout errors.

**Solutions:**
```sql
-- 1. Enable slow query log
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
SET GLOBAL slow_query_log_file = '/var/log/mysql/slow-queries.log';

-- 2. Find slow queries
SELECT * FROM mysql.slow_log 
ORDER BY query_time DESC 
LIMIT 10;

-- 3. Check for missing indexes
EXPLAIN SELECT * FROM Transaction WHERE txnReference = 'xxx';

-- 4. Add indexes if needed
CREATE INDEX idx_txnReference ON Transaction(txnReference);
CREATE INDEX idx_userId_creationDate ON Transaction(userId, creationDate);

-- 5. Analyze table statistics
ANALYZE TABLE Transaction;

-- 6. Check table sizes
SELECT 
  table_name,
  ROUND(((data_length + index_length) / 1024 / 1024), 2) AS "Size (MB)"
FROM information_schema.TABLES
WHERE table_schema = 'payments'
ORDER BY (data_length + index_length) DESC;

-- 7. Optimize tables
OPTIMIZE TABLE Transaction;
OPTIMIZE TABLE Transaction_Log;

-- 8. Check for table locks
SHOW OPEN TABLES WHERE In_use > 0;

-- 9. Monitor connection pool
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Threads_connected';
SHOW VARIABLES LIKE 'max_connections';
```

#### Issue 11: Mock and Real Services Conflict

**Symptoms:**
Confusion between mock and real service endpoints.

**Solutions:**
```bash
# 1. Use different ports
# Mock: 8084 (dev/qa only)
# Real Trustly API: External (uat/prod only)

# 2. Use environment-specific configuration
# application-local.properties:
trustlyprovider.deposit.url=http://localhost:8084/payment/initiate

# application-prod.properties:
trustlyprovider.deposit.url=https://api.trustly.com/v1/deposits

# 3. Ensure mock service is NOT running in production
# Check running processes:
ps aux | grep trustly-mock-service
# Should return nothing in production

# 4. Verify deployment scripts exclude mock
ls -la /opt/payment-services/
# Should NOT see trustly-mock-service.jar

# 5. Check systemd services
systemctl list-units --type=service | grep trustly
# Should NOT show trustly-mock service in production

# 6. Use feature flags if needed
payment.use-mock-provider=false
```

### Performance Optimization

**Database Connection Pooling:**
```properties
# HikariCP configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

**Caching Configuration:**
```properties
# Redis cache
spring.cache.type=redis
spring.cache.redis.time-to-live=3600000
spring.cache.redis.cache-null-values=false

# Caffeine cache (in-memory alternative)
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=1h
```

**RestClient Timeout Configuration:**
```java
@Bean
RestClient restClient() {
    return RestClient.builder()
        .requestFactory(new SimpleClientHttpRequestFactory() {{
            setConnectTimeout(5000);
            setReadTimeout(10000);
        }})
        .build();
}
```

### Log Analysis

**Common Log Patterns:**

```bash
# Find errors in last hour
grep ERROR /var/log/payment-processing-service/application.log | \
  tail -n 100

# Count errors by type
grep ERROR application.log | \
  cut -d'-' -f4 | \
  sort | uniq -c | sort -rn

# Find slow requests (> 1 second)
grep "Request took" application.log | \
  awk '$NF > 1000' | tail -n 50

# Monitor specific transaction
grep "uuid-456" application.log | tail -f

# Find all FAILED transactions
grep "txnStatus.*FAILED" application.log | wc -l
```

## Contributing

### Code Style Guidelines

1. **Java Code Style:**
   - Follow Google Java Style Guide
   - Use 4 spaces for indentation
   - Maximum line length: 120 characters
   - Use meaningful variable names

2. **Naming Conventions:**
   - Classes: PascalCase (e.g., `PaymentService`)
   - Methods: camelCase (e.g., `createPayment`)
   - Constants: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
   - Packages: lowercase (e.g., `com.hulkhiretech.payments`)

3. **Use Lombok:**
   ```java
   @Data
   @Builder
   @AllArgsConstructor
   @NoArgsConstructor
   public class PaymentRequest {
       private Long amount;
       private String currency;
   }
   ```

4. **Exception Handling:**
   - Use custom exceptions with error codes
   - Log exceptions with context
   - Don't swallow exceptions

5. **Comments:**
   - Write self-documenting code
   - Add JavaDoc for public APIs
   - Explain "why" not "what"

### Git Workflow

1. **Branch Naming:**
   ```
   feature/payment-validation-enhancement
   bugfix/hmac-signature-issue
   hotfix/production-database-connection
   release/v2.1.0
   ```

2. **Commit Messages:**
   ```
   feat: Add RSA signature verification for Trustly API
   fix: Resolve HMAC signature validation in validation service
   refactor: Simplify transaction status handler factory
   docs: Update API documentation for payment endpoints
   test: Add unit tests for payment processing service
   ```

3. **Pull Request Process:**
   ```bash
   # 1. Create feature branch from develop
   git checkout develop
   git pull origin develop
   git checkout -b feature/new-payment-method
   
   # 2. Make changes and commit
   git add .
   git commit -m "feat: Add new payment method support"
   
   # 3. Push to remote
   git push origin feature/new-payment-method
   
   # 4. Create pull request on GitHub/GitLab
   # 5. Code review and address comments
   # 6. Merge after approval
   ```

4. **Branch Protection:**
   - Require pull request reviews
   - Require status checks to pass
   - No direct commits to `main` or `develop`

### Testing Guidelines

1. **Write Tests for:**
   - All new features
   - Bug fixes
   - Public APIs
   - Business logic

2. **Test Structure:**
   ```java
   @SpringBootTest
   class PaymentServiceTest {
       
       @Mock
       private TransactionDAO transactionDAO;
       
       @InjectMocks
       private PaymentServiceImpl paymentService;
       
       @Test
       void testCreatePayment_Success() {
           // Arrange
           CreateTransaction request = CreateTransaction.builder()
               .userId(123)
               .amount(BigDecimal.valueOf(100))
               .currency("EUR")
               .build();
           
           // Act
           CreateTransactionResponse response = 
               paymentService.createPayment(request);
           
           // Assert
           assertNotNull(response.getTxnReference());
           assertEquals("CREATED", response.getTxnStatus());
       }
   }
   ```

3. **Test Coverage:**
   - Aim for 80%+ code coverage
   - Focus on critical paths
   - Don't test trivial getters/setters

### Code Review Checklist

- [ ] Code follows style guidelines
- [ ] Tests are included and passing
- [ ] Documentation is updated
- [ ] No sensitive data in code
- [ ] Error handling is proper
- [ ] Logging is appropriate
- [ ] Performance impact considered
- [ ] Security implications reviewed
- [ ] Database migrations included (if needed)

## License

This project is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.

**Copyright © 2025 HulkHireTech**

All rights reserved.

## Support and Contact

### For Issues and Questions

1. **Technical Issues:**
   - Check this documentation first
   - Search existing issues in repository
   - Create new issue with detailed description

2. **Feature Requests:**
   - Open feature request issue
   - Describe use case and benefits
   - Provide examples if possible

3. **Security Vulnerabilities:**
   - **DO NOT** create public issue
   - Email: security@hulkhiretech.com
   - Include detailed description and steps to reproduce

### Team Contacts

- **Project Lead:** [Name] - [email]
- **Backend Team:** backend-team@hulkhiretech.com
- **DevOps Team:** devops@hulkhiretech.com
- **QA Team:** qa@hulkhiretech.com

### Documentation Updates

This documentation is maintained in the repository. To suggest improvements:

1. Fork the repository
2. Update the README.md
3. Submit a pull request
4. Documentation team will review

---

## Appendix

### A. Transaction Status Flow Diagram

```
┌─────────┐
│ CREATED │ (Status ID: 1)
└────┬────┘
     │
     │ initiate()
     ▼
┌───────────┐
│ INITIATED │ (Status ID: 2)
└─────┬─────┘
      │
      │ provider.deposit()
      ▼
┌─────────┐
│ PENDING │ (Status ID: 3)
└────┬────┘
     │
     ├─────► SUCCESS (Status ID: 4)
     │       [User completes payment]
     │
     └─────► FAILED (Status ID: 5)
             [User cancels or error occurs]
```

### B. Validation Rules Priority

```
Priority 0: CHECK1_VALIDATOR_RULE
  ├─ Customer ID validation
  └─ Basic request structure

Priority 10: DUPLICATION_TXN_RULE
  ├─ Check duplicate merchant transaction reference
  └─ Prevent double processing

Priority 20: Field Validations
  ├─ Amount validation
  ├─ Currency validation
  └─ Format validations

Priority 30: PAYMENT_ATTEMPT_THRESHOLD_RULE
  ├─ Max 5 attempts per 2 minutes
  └─ Rate limiting
```

### C. Database Indexes

**Recommended Indexes for Performance:**

```sql
-- Transaction table indexes
CREATE INDEX idx_txnReference ON Transaction(txnReference);
CREATE INDEX idx_userId ON Transaction(userId);
CREATE INDEX idx_creationDate ON Transaction(creationDate);
CREATE INDEX idx_txnStatusId ON Transaction(txnStatusId);
CREATE INDEX idx_userId_creationDate ON Transaction(userId, creationDate);

-- Transaction_Log indexes
CREATE INDEX idx_transactionId ON Transaction_Log(transactionId);
CREATE INDEX idx_creationDate ON Transaction_Log(creationDate);

-- merchant_payment_request indexes
CREATE INDEX idx_merchantTxnRef ON merchant_payment_request(merchantTransactionReference);
CREATE INDEX idx_endUserID ON merchant_payment_request(endUserID);

-- validation_rules indexes
CREATE INDEX idx_priority ON validation_rules(priority);
CREATE INDEX idx_isActive ON validation_rules(isActive);
```

### D. Environment Variables Reference

```bash
# Database
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=payments
DB_PASSWORD=<secure-password>

# Trustly
TRUSTLY_USERNAME=<trustly-username>
TRUSTLY_PASSWORD=<trustly-password>
TRUSTLY_API_URL=https://api.trustly.com/v1

# Security
HMAC_SECRET=<secure-secret>
RSA_PRIVATE_KEY_PATH=/secure/private.pem
RSA_PUBLIC_KEY_PATH=/secure/public.pem

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=<redis-password>

# Application
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8082
LOG_LEVEL=INFO
```

### E. Useful SQL Queries

```sql
-- 1. Transaction summary by status
SELECT 
  ts.name AS status,
  COUNT(*) AS count,
  SUM(t.amount) AS total_amount
FROM Transaction t
JOIN Transaction_Status ts ON t.txnStatusId = ts.id
GROUP BY ts.name;

-- 2. Failed transactions in last 24 hours
SELECT 
  txnReference,
  userId,
  amount,
  currency,
  errorCode,
  errorMessage,
  creationDate
FROM Transaction
WHERE txnStatusId = 5
  AND creationDate >= DATE_SUB(NOW(), INTERVAL 24 HOUR)
ORDER BY creationDate DESC;

-- 3. Average transaction amount by provider
SELECT 
  p.providerName,
  AVG(t.amount) AS avg_amount,
  COUNT(*) AS transaction_count
FROM Transaction t
JOIN Provider p ON t.providerId = p.id
GROUP BY p.providerName;

-- 4. Transaction success rate
SELECT 
  DATE(creationDate) AS date,
  COUNT(*) AS total,
  SUM(CASE WHEN txnStatusId = 4 THEN 1 ELSE 0 END) AS successful,
  ROUND(SUM(CASE WHEN txnStatusId = 4 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS success_rate
FROM Transaction
WHERE creationDate >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY DATE(creationDate)
ORDER BY date DESC;

-- 5. Pending transactions older than 1 hour
SELECT 
  txnReference,
  userId,
  amount,
  creationDate,
  TIMESTAMPDIFF(MINUTE, creationDate, NOW()) AS minutes_pending
FROM Transaction
WHERE txnStatusId = 3
  AND creationDate < DATE_SUB(NOW(), INTERVAL 1 HOUR)
ORDER BY creationDate;
```

---

**Project Version:** 2.0.0  
**Last Updated:** November 2025  
**Java Version:** 24  
**Spring Boot Version:** 3.5.3  
**Documentation Version:** 1.0

**Happy Coding! 🚀**
