# Credit Card Payment Service

A Spring Boot microservice for processing credit card payments. It should be run in a docker container.

```bash
git clone https://github.com/palpandianpullan/credit-card-payment-service.git

cd credit-card-payment-service
```

## Running with Docker Compose

This is the recommended way to run the service, especially if you are using it in conjunction with other services like the `room-reservation-app`.


### 1. Build and Start the Service

```bash
docker-compose up --build -d
```

The service will be available at `http://localhost:9090/credit-card-payment-api`.

### 2. Check Health

The service includes Actuator health checks:

```bash
curl http://localhost:9090/credit-card-payment-api/actuator/health
```

## Configuration

- **Port**: 9090
- **Context Path**: `/credit-card-payment-api`
- **Java Version**: 17 (Temurin)

### Mock Payment Behavior

For testing purposes, certain payment references are mocked to return specific outcomes:
- **Confirmed References**: `DL123456789`, `REF001`, `REF002`, `REF003`
- **Rejected References**: `REJECT001`, `REJECT002`

You can modify these in `src/main/resources/application.properties`.
