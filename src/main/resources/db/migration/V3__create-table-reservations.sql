CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    table_id UUID NOT NULL,
    reservation_date_time TIMESTAMP NOT NULL,
    status VARCHAR(100) NOT NULL
);