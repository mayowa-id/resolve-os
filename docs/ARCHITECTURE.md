# Architecture

## Shape

ResolveOS is a modular monolith with two deployables: an HTTP API and a background worker. Shared modules use inward dependencies:

```text
contracts       domain
    \           /
      application
       /       \
persistence   integrations
       \       /
        api / worker
```

The `agent` module remains an empty boundary until Milestone 7. It may depend inward on `application`; domain code must never depend on it.

## Responsibilities

- `contracts`: stable transport/event contracts and common error vocabulary.
- `domain`: framework-free entities, value objects, policies, and state transitions.
- `application`: commands, use cases, authorization-aware orchestration, and ports.
- `persistence`: JPA adapters, explicit queries, Flyway migrations, inbox/outbox storage.
- `integrations`: storage, messaging, CRM, activation, and other external adapters.
- `agent`: governed Spring AI implementation introduced only in Milestone 7.
- `apps/api`: HTTP/security composition root.
- `apps/worker`: Temporal/activity composition root.

PostgreSQL is authoritative for business state. Temporal will coordinate durable work but will not own case status. External side effects occur through adapters/Activities and require idempotency or reconciliation.

