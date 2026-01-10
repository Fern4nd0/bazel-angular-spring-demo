CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  role VARCHAR(50) NOT NULL,
  status VARCHAR(20) NOT NULL,
  phone VARCHAR(30),
  metadata JSONB,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE users
  ADD CONSTRAINT uq_users_email UNIQUE (email);

CREATE INDEX idx_users_status ON users (status);
