INSERT INTO organizations (id, slug, name, status, created_at)
VALUES
    ('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', 'northwind-labs', 'Northwind Labs', 'ACTIVE', CURRENT_TIMESTAMP),
    ('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb', 'contoso-systems', 'Contoso Systems', 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

INSERT INTO memberships (id, organization_id, subject_id, role, status, created_at)
VALUES
    ('c0000000-0000-4000-8000-000000000001', 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', '11111111-1111-4111-8111-111111111111', 'ORG_ADMIN', 'ACTIVE', CURRENT_TIMESTAMP),
    ('c0000000-0000-4000-8000-000000000002', 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', '22222222-2222-4222-8222-222222222222', 'IMPLEMENTATION_OPERATOR', 'ACTIVE', CURRENT_TIMESTAMP),
    ('c0000000-0000-4000-8000-000000000003', 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb', '33333333-3333-4333-8333-333333333333', 'ORG_ADMIN', 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
