# Name Registration App

A minimal Quarkus application with HTMX for name registration.

## Features
- Register names using a simple form
- View all registered names in real-time
- Uses HTMX for dynamic updates without page refresh

## Running the application

```bash
./mvnw quarkus:dev
```

Visit: http://localhost:8080

## Deployment

Package for deployment:
```bash
./mvnw package
```

The application will be packaged in `target/quarkus-app/`