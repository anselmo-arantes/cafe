# Extraction Roadmap (Service by Service)

## Phase 1: Catalog Service (Anchor)
- Create `ecommerce-catalog-service` repository from template.
- Move catalog controllers/services/repositories.
- Keep current API contracts unchanged.
- Validate OpenAPI parity and smoke tests.

## Phase 2: Inventory Service
- Create `ecommerce-inventory-service`.
- Move stock + reservation domain.
- Expose reservation contract endpoints.
- Integrate callers through HTTP client only.

## Phase 3: Order Service
- Create `ecommerce-order-service`.
- Move order lifecycle and status transition rules.
- Harden invalid transitions at service boundary.

## Phase 4: Payment Service
- Create `ecommerce-payment-service`.
- Move payment create/get/webhook processing.
- Enforce webhook idempotency and transition protection.

## Phase 5: Cart Service
- Create `ecommerce-cart-service`.
- Move cart storage and snapshot computation.

## Phase 6: Checkout Service (Orchestrator)
- Create `ecommerce-checkout-service`.
- Keep orchestration only; no domain ownership besides workflow.
- Implement compensating actions for partial failures.

## Exit Criteria per Phase
1. Service compiles and tests pass.
2. OpenAPI published.
3. Health endpoint available.
4. Contract tests pass against upstream/downstream services.
5. Canary rollout completed without SLO degradation.
