CREATE TABLE customers
(
        id UUID PRIMARY KEY,
        name TEXT NOT NULL,
        logo TEXT NOT NULL,
        email TEXT NOT NULL,
        phone TEXT,
        created_at TIMESTAMPTZ
);
