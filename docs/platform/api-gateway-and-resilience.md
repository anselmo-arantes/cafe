# Platform Layer: Gateway, Routing and Resilience

## Gateway Responsibilities
- Single external entrypoint for clients.
- Route requests to domain services by path prefix.
- Propagate correlation headers (`X-Correlation-Id`).
- Enforce payload size and request timeouts.

## Minimum Internal Policies
- HTTP timeout defaults per service call.
- Retry with backoff only for safe/idempotent operations.
- Circuit breaker for downstream instability.
- Centralized error mapping at gateway boundary.

## Security Baseline (within current scope)
- Service-to-service allowlist by network/policy.
- Validate content type and payload size.
- Input validation at service boundary.

## Observability Baseline
- Structured logs containing:
  - `correlationId`
  - `service`
  - `orderId`/`paymentId`/`reservationId` when present
- Health endpoints exposed on all services.
- Dashboard with latency/error rate per endpoint.
