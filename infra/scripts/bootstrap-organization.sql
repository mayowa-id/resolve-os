\set ON_ERROR_STOP on

\if :{?organization_id}
\else
  \echo 'Missing organization_id variable'
  \quit 2
\endif

\if :{?organization_slug}
\else
  \echo 'Missing organization_slug variable'
  \quit 2
\endif

\if :{?organization_name}
\else
  \echo 'Missing organization_name variable'
  \quit 2
\endif

\if :{?membership_id}
\else
  \echo 'Missing membership_id variable'
  \quit 2
\endif

\if :{?subject_id}
\else
  \echo 'Missing subject_id variable'
  \quit 2
\endif

BEGIN;

INSERT INTO organizations (id, slug, name, status)
VALUES (:'organization_id'::uuid, :'organization_slug', :'organization_name', 'ACTIVE');

INSERT INTO memberships (id, organization_id, subject_id, role, status)
VALUES (:'membership_id'::uuid, :'organization_id'::uuid, :'subject_id', 'ORG_ADMIN', 'ACTIVE');

COMMIT;
