# CI/CD and Contract Testing Plan

## Per-Service Pipeline Stages
1. Build + unit tests (`mvn -q test`).
2. Static checks (format/lint as defined by repo).
3. OpenAPI generation and validation.
4. Contract tests against dependent services.
5. Container build and publish.

## Contract Test Scope
- Checkout -> Cart (cart snapshot contract)
- Checkout -> Inventory (reserve/confirm/release contract)
- Checkout -> Order (create/update state contract)
- Checkout -> Payment (create/webhook contract)
- Cart -> Catalog (product lookup contract)

## Release Gates
- No contract break allowed without explicit versioning strategy.
- Failed contract test blocks deploy.
- Canary deploy only after pipeline green.

## Artifact Standards
- OpenAPI JSON published as build artifact.
- Test reports published (Surefire/JUnit XML).
- Image tags include git SHA.
