# Progressive Rollout and Safe Cutover

## Rollout Strategy
1. **Shadow mode**: new service receives mirrored traffic, no side effects.
2. **Canary 5%**: route limited real traffic.
3. **Canary 25%/50%**: expand gradually if SLOs are stable.
4. **Full cutover**: route 100% and disable old path.

## Feature Flags
- `service.<name>.enabled`
- `service.<name>.write-enabled`
- `service.<name>.read-enabled`

## Operational Checks per Step
- Error rate (5xx + business error spikes)
- p95/p99 latency
- Timeout and retry volume
- Data reconciliation diffs

## Rollback Plan
- Immediate route switch back to previous implementation.
- Disable write path on new service.
- Preserve audit logs and request traces.
- Run reconciliation before re-attempting rollout.
