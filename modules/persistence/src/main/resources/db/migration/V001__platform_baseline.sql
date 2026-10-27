CREATE TABLE platform_bootstrap (
    id smallint PRIMARY KEY,
    installed_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT platform_bootstrap_singleton CHECK (id = 1)
);

INSERT INTO platform_bootstrap (id) VALUES (1);
