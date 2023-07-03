SELECT * FROM pg_extension WHERE extname = 'uuid-ossp';
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS operation (
    operation_id uuid NOT NULL,
    cost integer,
    type character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT operation_pkey PRIMARY KEY (operation_id),
    CONSTRAINT operation_type_check CHECK (type::text = ANY (ARRAY['ADDITION'::character varying, 'SUBTRACTION'::character varying, 'MULTIPLICATION'::character varying, 'DIVISION'::character varying, 'SQUARE_ROOT'::character varying, 'RANDOM_STRING'::character varying]::text[])),
    CONSTRAINT operation_type_unique UNIQUE (type)
);

INSERT INTO operation (operation_id, type, cost)
VALUES (uuid_generate_v4(), 'ADDITION',1),
       (uuid_generate_v4(), 'SUBTRACTION', 1),
       (uuid_generate_v4(), 'MULTIPLICATION', 2),
       (uuid_generate_v4(), 'DIVISION', 2),
       (uuid_generate_v4(), 'SQUARE_ROOT', 3),
       (uuid_generate_v4(), 'RANDOM_STRING', 4)
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS roles
(
    role_id uuid NOT NULL,
    type character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT roles_pkey PRIMARY KEY (role_id),
    CONSTRAINT uk_q9npl2ty4pngm2cussiul2qj5 UNIQUE (type),
    CONSTRAINT roles_type_check CHECK (type::text = ANY (ARRAY['USER'::character varying, 'ADMIN'::character varying]::text[]))
);

INSERT INTO roles (role_id, type)
VALUES (uuid_generate_v4(), 'USER'),
       (uuid_generate_v4(), 'ADMIN')
ON CONFLICT DO NOTHING;
