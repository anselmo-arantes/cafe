# Microservices Boundaries and Public Contracts

## Objective
Freeze bounded contexts and public API contracts before code extraction into independent repositories.

## Bounded Contexts

### 1) Catalog Service (`com.anselmo.ecommerce.catalog`)
**Owns:** Product data, product activation, price and stock metadata for discovery.

**Public APIs (v1):**
- `GET /api/v1/catalog/products/{sku}`
- `GET /api/v1/catalog/products/{sku}/availability`
- `POST /api/v1/admin/catalog/products`
- `PUT /api/v1/admin/catalog/products/{id}`
- `PATCH /api/v1/admin/catalog/products/{id}/price`
- `PATCH /api/v1/admin/catalog/products/{id}/stock`
- `PATCH /api/v1/admin/catalog/products/{id}/status`
- `GET /api/v1/admin/catalog/products`

### 2) Inventory Service (`com.anselmo.ecommerce.inventory`)
**Owns:** Available quantity, reserved quantity, reservation lifecycle.

**Public APIs (v1):**
- `GET /api/v1/inventory/{sku}`
- `PATCH /api/v1/admin/inventory/{sku}/adjust`
- `POST /api/v1/inventory/reservations`
- `POST /api/v1/inventory/reservations/{reservationId}/confirm`
- `POST /api/v1/inventory/reservations/{reservationId}/release`

### 3) Cart Service (`com.anselmo.ecommerce.cart`)
**Owns:** Cart item list and calculated cart snapshot.

**Public APIs (v1):**
- `GET /api/v1/cart/{cartId}`
- `POST /api/v1/cart/{cartId}/items`
- `PATCH /api/v1/cart/{cartId}/items/{sku}`
- `DELETE /api/v1/cart/{cartId}/items/{sku}`
- `DELETE /api/v1/cart/{cartId}/items`

### 4) Order Service (`com.anselmo.ecommerce.order`)
**Owns:** Order lifecycle, status transitions, item snapshots.

**Public APIs (v1):**
- `GET /api/v1/orders/{orderId}`
- `GET /api/v1/admin/orders`
- `PATCH /api/v1/orders/{orderId}/status`

### 5) Payment Service (`com.anselmo.ecommerce.payment`)
**Owns:** Payment lifecycle, webhook processing, provider reference.

**Public APIs (v1):**
- `POST /api/v1/payments`
- `GET /api/v1/payments/{paymentId}`
- `POST /api/v1/payments/webhook`

### 6) Checkout Service (`com.anselmo.ecommerce.checkout`)
**Owns:** Orchestration workflow only (cart -> reservation -> order -> payment).

**Public APIs (v1):**
- `POST /api/v1/checkout`

## Contract Rules (Hard Requirements)
1. Every service publishes an OpenAPI spec.
2. Breaking change requires version bump (`/api/v2` or equivalent).
3. Error payload is standardized across services:
   - `timestamp`
   - `status`
   - `error`
   - `message`
   - `path`
4. Idempotent operations must accept stable identifiers:
   - `reservationId` for inventory reservations.
   - `paymentId` + webhook status transition checks.
5. Service-to-service communication uses HTTP contracts only (no direct package dependency).

## Cross-Service Sequence (Checkout)
1. Checkout receives `cartId` + `customerEmail`.
2. Checkout fetches cart snapshot from Cart Service.
3. Checkout reserves stock in Inventory Service.
4. Checkout creates order in Order Service.
5. Checkout creates payment in Payment Service.
6. On payment creation failure, checkout compensates by releasing reservations and marking order as failed.

## Decision Log
- Keep extraction order: Catalog -> Inventory -> Order -> Payment -> Cart -> Checkout.
- Keep package root per service under `com.anselmo.ecommerce.<service>`.
