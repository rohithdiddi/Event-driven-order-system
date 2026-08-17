# Event-Driven Order Processing System

A small but complete distributed system demonstrating event-driven microservices
architecture with Java, Spring Boot, and Apache Kafka. Three independently
deployable services communicate asynchronously via Kafka, each owning its own
datastore (polyglot persistence).

## Architecture

```
┌────────────────┐     order-created      ┌─────────────────────┐
│  Order Service  │ ─────────────────────▶│  Inventory Service   │
│  (PostgreSQL)   │      (Kafka topic)      │     (MongoDB)        │
└────────────────┘                         └──────────┬───────────┘
                                                        │
                                     inventory-reserved │ inventory-failed
                                                        │  (Kafka topics)
                                                        ▼
                                            ┌─────────────────────────┐
                                            │  Notification Service    │
                                            │  (logs simulated notify) │
                                            └─────────────────────────┘
```

**Flow:**
1. A client `POST`s an order to **order-service**, which persists it to PostgreSQL
   and publishes an `OrderCreatedEvent` to the `order-created` Kafka topic.
2. **inventory-service** consumes that event, checks stock in MongoDB, and either
   reserves the stock (publishing `InventoryReservedEvent`) or fails the reservation
   (publishing `InventoryFailedEvent`).
3. **notification-service** consumes both outcome events and logs a simulated
   customer notification — in a real system this would call an email/SMS/push provider.

Each service is independently deployable and only communicates through Kafka —
no direct service-to-service HTTP calls, no shared database.

## Why I built this

I wanted a project that demonstrates the same patterns I use professionally:
Spring Boot microservices, Kafka-based event-driven communication, containerized
deployment, and automated testing — the same stack referenced throughout my
[resume](https://linkedin.com/in/rohithdiddi) and the backend/cloud roles I'm targeting.

## Tech Stack

- **Java 17**, **Spring Boot 3.2**
- **Apache Kafka** (via Confluent images) for async event-driven communication
- **PostgreSQL** (order-service) and **MongoDB** (inventory-service) — polyglot persistence
- **Docker & Docker Compose** for local orchestration
- **JUnit 5 + Mockito** for unit tests
- **GitHub Actions** for CI (builds + tests all three services on every push)

## Running locally

Requires Docker and Docker Compose.

```bash
git clone https://github.com/rohithdiddi/event-driven-order-system.git
cd event-driven-order-system
docker compose up --build
```

This starts Zookeeper, Kafka, PostgreSQL, MongoDB, and all three services.

| Service              | Port |
|-----------------------|------|
| order-service          | 8081 |
| inventory-service       | 8082 |
| notification-service    | 8083 |

### Try it out

1. Seed some inventory:
```bash
curl -X POST http://localhost:8082/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"productSku": "SKU-123", "availableQuantity": 10}'
```

2. Place an order:
```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productSku": "SKU-123", "quantity": 2, "totalPrice": 49.99, "customerId": "customer-1"}'
```

3. Watch the logs — you'll see `order-service` publish the event, `inventory-service`
   reserve stock, and `notification-service` log the simulated notification, all
   asynchronously.

## Running tests

Each service has its own test suite:

```bash
cd order-service && mvn test
cd inventory-service && mvn test
cd notification-service && mvn test
```

## What I'd add next

- Idempotency keys on event consumers to safely handle duplicate Kafka deliveries
- A saga/compensation flow to cancel orders automatically on `InventoryFailedEvent`
- An API Gateway in front of the services
- Kubernetes manifests as an alternative to Docker Compose for production-style deployment

## Author

**Rohith Diddi** — Backend Software Engineer
[LinkedIn](https://linkedin.com/in/rohithdiddi)
