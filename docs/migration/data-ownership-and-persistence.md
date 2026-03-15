# Data Ownership and Persistence Isolation

## Goal
Ensure strong data ownership per service and remove direct data coupling.

## Ownership Matrix

| Service | DynamoDB Tables (owner) | Notes |
|---|---|---|
| Catalog | `catalog_products` | Includes SKU GSI (`sku-index`) |
| Cart | `cart_items` | Cart-scoped item list |
| Inventory | `inventory_items`, `inventory_reservations` | Reservation lifecycle is inventory-owned |
| Order | `orders` | Order status machine owned by order service |
| Payment | `payments` | Payment status machine owned by payment service |
| Checkout | none (orchestration only) | No business persistence ownership required |

## Rules
1. A service can read/write only its own tables.
2. Cross-service reads must use public APIs.
3. No shared table writes across services.
4. If denormalized data is needed, replicate via API/event integration.

## Migration Strategy
1. Create service-owned tables in the target account/environment.
2. Backfill data from current monolith tables.
3. Enable dual-write only during migration windows.
4. Verify consistency with reconciliation scripts.
5. Switch traffic to the extracted service.
6. Disable legacy writes and keep read-only rollback window.

## Rollback Basics
- Keep old tables untouched during cutover window.
- Disable new service write path via feature flag.
- Re-enable previous write path in monolith if critical regressions occur.
