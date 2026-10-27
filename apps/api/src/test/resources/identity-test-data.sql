DELETE FROM memberships;
DELETE FROM organizations;

INSERT INTO organizations (id, slug, name, status, created_at)
VALUES
    ('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', 'northwind-labs', 'Northwind Labs', 'ACTIVE', CURRENT_TIMESTAMP),
    ('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb', 'contoso-systems', 'Contoso Systems', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO memberships (id, organization_id, subject_id, role, status, created_at)
VALUES
    ('c0000000-0000-4000-8000-000000000001', 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', 'subject-admin-a', 'ORG_ADMIN', 'ACTIVE', CURRENT_TIMESTAMP),
    ('c0000000-0000-4000-8000-000000000002', 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', 'subject-operator-a', 'IMPLEMENTATION_OPERATOR', 'ACTIVE', CURRENT_TIMESTAMP),
    ('c0000000-0000-4000-8000-000000000003', 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb', 'subject-admin-b', 'ORG_ADMIN', 'ACTIVE', CURRENT_TIMESTAMP);
