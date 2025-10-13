# URL Shortener REST API

## Overview

This REST API provides a service to shorten URLs, store them in a database, and serve them via short links. It also caches URL lookups in Redis to improve performance.

The API evaluates each endpoint in its workflow as follows:

1. **POST `/`** – Shortens a given URL.
    - Looks up the shortened URL in Redis cache first.
    - If not found in cache, queries the database.
    - If the URL already exists, the API returns the existing shortened URL.
    - If not found in cache and db, stores it in both.
    - Note: The URL Shortener API expects full URLs including the schema (http or https).

2. **GET `/{shortenedUrl}`** – Redirects to the original URL.
    - Looks up the shortened URL in Redis cache first.
    - If not found in cache, queries the database.
    - Responds with a 302 redirect to the original URL.
    - Saves the usage log in the 'UrlAccessLog' table for future analytics.

---

## Running Locally with Docker

The application can be run locally using Docker Compose:

```bash
mvn clean install
docker-compose up --build
```

This will start three services:
- PostgreSQL database on port 5432
- Redis cache on port 6379
- URL Shortener application on port 8080
- Environment variables are preconfigured in the docker-compose.yml file.

## Using Swagger to Test Endpoints

Swagger UI is available at:

```bash
http://localhost:8080/swagger-ui.html
```

You can use *Swagger-UI* to:
- Explore all available endpoints
- Test API requests with example data from the DTOs
- View request and response schemas

Note: The GET endpoint for redirecting to the original URL might be blocked by CORS in Swagger because it performs a browser redirect. To test this endpoint:
- Use curl or Postman to perform the GET request:
```bash
curl -v http://localhost:8080/SHORT_URL
```
- Or use a browser directly by entering:
```bash
http://localhost:8080/SHORT_URL
```

Replace SHORT_URL with the shortened URL returned by the POST endpoint.

## Dev Notes and Future Improvements

### Dependency Injection for URL Hashing

- Currently, URL shortening logic uses a fixed algorithm.
- By injecting the hash/shortening method, you can easily switch from Base32 to Base64 or 128-bit encoding.
- Use a key separated by "-" from the hash to specify the algorithm used for de-shortening the URLs.

### Monitoring and Cleanup Job

- Add a scheduled job to analyze the logs table and remove URLs that have not been used for a configurable time period.

### Trace IDs in Logs

- Currently, UrlAccessLog table uses auto-generated IDs. 
- Replace these with trace IDs matching the database log entries for better observability and tracing.

### Custom @ValidUrl Validation

- Enhance URL validation to accept URLs without http/https.
- Automatically prepend https:// before saving to the database.