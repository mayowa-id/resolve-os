CREATE TABLE organizations (
    id uuid PRIMARY KEY,
    slug varchar(63) NOT NULL,
    name varchar(200) NOT NULL,
    status varchar(32) NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT organizations_slug_unique UNIQUE (slug),
    CONSTRAINT organizations_slug_format CHECK (slug ~ '^[a-z0-9]+(?:-[a-z0-9]+)*$'),
    CONSTRAINT organizations_status_check CHECK (status IN ('ACTIVE', 'SUSPENDED'))
);

CREATE TABLE memberships (
    id uuid PRIMARY KEY,
    organization_id uuid NOT NULL,
    subject_id varchar(255) NOT NULL,
    role varchar(64) NOT NULL,
    status varchar(32) NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT memberships_organization_fk
        FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT memberships_subject_unique UNIQUE (organization_id, subject_id),
    CONSTRAINT memberships_subject_not_blank CHECK (length(trim(subject_id)) > 0),
    CONSTRAINT memberships_role_check CHECK (role IN (
        'ORG_ADMIN',
        'OPERATIONS_MANAGER',
        'IMPLEMENTATION_OPERATOR',
        'IMPLEMENTATION_MANAGER',
        'AUDITOR',
        'INTERNAL_SERVICE'
    )),
    CONSTRAINT memberships_status_check CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE INDEX memberships_subject_status_idx ON memberships (subject_id, status, organization_id);
CREATE INDEX memberships_organization_status_idx ON memberships (organization_id, status);
