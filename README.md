# Crypto Trading System
It implements a simplified crypto trading system using Spring Boot and an in-memory H2 database.

## Architecture Decisions

The project is structured following a **Pragmatic Hexagonal Architecture** to ensure separation of concerns, decoupling the core business logic from external frameworks, APIs, and databases.

* **Domain**: Contains the core entities. I applied lightweight **Domain-Driven Design (DDD)** concepts here. For example, the `Wallet` entity acts as a **Rich Domain Model**. Instead of exposing standard setters (Anemic Domain Model), it encapsulates the `credit` and `debit` invariants to self-protect its balance state.
* **Application**: Contains the use cases (`TradingService`) orchestrating the domain objects.
* **Infrastructure**: Contains adapters for database access (Spring Data JPA), external API calls (OpenFeign), and scheduled jobs.
* **Presentation**: Contains the REST controllers and global exception handling.

I intentionally avoided full DDD patterns (like pure Value Objects for Money or separate Domain Events) to prevent over-engineering a straightforward transactional scope, striking a balance between clean architecture and pragmatism.

## Key Features Implemented
* Real-time price aggregation from Binance and Huobi using OpenFeign and a 10s Scheduler.
* Concurrent trade handling: Applied `@Version` on the `Wallet` entity to enable Optimistic Locking, preventing double-spending issues during rapid simultaneous API calls.
* BigDecimal implementation for high-precision financial calculations.