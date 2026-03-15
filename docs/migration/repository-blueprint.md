# Repository Blueprint for Independent Services

This document defines the target structure for each microservice repository.

## Standard Repository Layout

```text
<service>-service/
  pom.xml
  README.md
  AGENTS.md
  Dockerfile
  src/
    main/
      java/com/anselmo/ecommerce/<service>/
        controller/
        dto/
        domain/
        repository/
        service/
        exception/
        config/
        mapper/
      resources/
        application.yml
    test/
      java/com/anselmo/ecommerce/<service>/
        controller/
        service/
        mapper/
  .github/workflows/
    ci.yml
```

## Proposed Repository Names
- `ecommerce-catalog-service`
- `ecommerce-inventory-service`
- `ecommerce-order-service`
- `ecommerce-payment-service`
- `ecommerce-cart-service`
- `ecommerce-checkout-service`

## Service Baseline Requirements
1. Spring Boot app entrypoint.
2. Actuator health endpoint.
3. OpenAPI docs endpoint.
4. Global exception handler with standardized error payload.
5. DynamoDB config via AWS SDK v2 + Enhanced Client.
6. Unit tests for service + controller + mapper.
7. CI pipeline with `mvn -q test`.

## Bootstrap Template
A ready-to-copy template lives under:
- `templates/service-blueprint/`

Use this template to start each new service repository and replace `service` path tokens with the real service name.
