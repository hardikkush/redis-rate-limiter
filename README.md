# Redis Rate Limiter

A Redis-powered API rate limiter built using **Spring Boot, Java, Redis Sorted Sets, and Lua scripting**.

This project implements the **Sliding Window Log algorithm** to enforce per-user request limits. Redis stores request timestamps, while Lua scripts execute the rate-limiting operations atomically.

## Features

- Sliding Window Log rate-limiting algorithm
- Redis Sorted Sets (ZSET) for request tracking
- Atomic rate-limit execution using Lua scripts
- Per-user request limits
- Automatic expired-request removal
- Redis key expiration (TTL)
- Spring Boot `OncePerRequestFilter` integration
- HTTP 400 for missing user identification
- HTTP 429 when the rate limit is exceeded

## Tech Stack

- Java 25
- Spring Boot 4.1.2
- Spring Data Redis
- Redis
- Lua
- Maven
- Postman

## Rate Limiting Configuration

| Configuration | Value |
|---|---|
| Algorithm | Sliding Window Log |
| Request limit | 5 requests |
| Time window | 60 seconds |
| Redis data structure | Sorted Set |
| Redis key format | `rate:{userId}` |
| Key expiration | 120 seconds |

## How It Works

The application uses the Sliding Window Log algorithm.

Each incoming request is associated with a user ID supplied through the `X-User-Id` HTTP header.

Redis maintains a separate Sorted Set for each user.

- **Key:** `rate:user101`
- **Score:** Request timestamp in milliseconds
- **Member:** Unique request ID (UUID)

For every incoming request:

1. Calculate the sliding window cutoff timestamp.
2. Remove requests older than or equal to the cutoff.
3. Count the remaining requests.
4. Reject the request if the limit has already been reached.
5. Otherwise, add the new request to the Sorted Set.
6. Update the Redis key expiration.

All these operations are executed through a Lua script to ensure atomic execution.

## Request Flow

```text
Client / Postman
       |
       v
RateLimitFilter
       |
       v
Read X-User-Id Header
       |
       v
SlidingWindowRateLimiterService
       |
       v
Redis Lua Script
       |
       v
Check Request Limit
       |
       +------------------+
       |                  |
       v                  v
    Allowed             Rejected
       |                  |
       v                  v
   Controller        HTTP 429
       |
       v
   HTTP 200
```

## API Endpoints

### 1. Rate-Limited API

```http
GET /api/test
```

Required header:

```http
X-User-Id: user101
```

Expected responses:

| Scenario | HTTP Status |
|---|---|
| Request within limit | 200 OK |
| Missing user ID | 400 Bad Request |
| Request limit exceeded | 429 Too Many Requests |

### 2. Redis Health Check

```http
GET /redis/ping
```

Checks Redis connectivity.

Expected response:

```text
PONG
```

### 3. Manual Rate-Limit Test Endpoint

```http
GET /rate-limit/check/{userId}
```

Allows direct testing of the rate-limiting service.

## Running Locally

### Prerequisites

- Java 25
- Maven
- Redis Server
- IntelliJ IDEA or another Java IDE
- Postman (for API testing)

### 1. Clone the repository

```bash
git clone https://github.com/hardikkush/redis-rate-limiter.git
cd redis-rate-limiter
```

### 2. Start Redis

Make sure your local Redis server is running on port `6379`.

### 3. Configure Redis

In `src/main/resources/application.properties`:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000ms
```

### 4. Run the Spring Boot application

On Windows:

```bash
.\mvnw.cmd spring-boot:run
```

Alternatively, run the main application class through your IDE.

### 5. Test using Postman

Send requests to:

```http
GET http://localhost:8080/api/test
```

Add the header:

```http
X-User-Id: user101
```

The first five requests should succeed, while the sixth request should receive HTTP 429.

Changing the user ID creates an independent rate-limit bucket.

## Project Structure

```text
RateLimitDemo/
│
├── src/main/java/
│   └── com/RateLimitDemo/RateLimitDemo/
│       ├── Config/
│       │   └── RedisScriptConfig.java
│       │
│       ├── Controller/
│       │   ├── RedisHealthController.java
│       │   ├── RateLimitTestController.java
│       │   └── TestController.java
│       │
│       ├── Filter/
│       │   └── RateLimitFilter.java
│       │
│       └── Service/
│           ├── RedisHealthService.java
│           └── SlidingWindowRateLimiterService.java
│
└── src/main/resources/
    ├── application.properties
    └── scripts/
        └── sliding-window.lua
```

## Learning Outcomes

This project helped explore:

- Redis Sorted Sets and their operations
- Sliding Window Log rate-limiting algorithm
- Lua scripting in Redis
- Atomic execution of Redis operations
- TTL and automatic key expiration
- Spring Boot servlet filters
- Integrating Redis with Spring Boot

## Future Improvements

- Configurable request limits and time windows
- Rate-limit response headers
- Distributed load and concurrency testing

---

**Built as a hands-on backend learning project to explore Redis and rate limiting.**
