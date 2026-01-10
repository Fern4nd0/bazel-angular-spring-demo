CREATE TABLE positions (
  id VARCHAR(64) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  latitude DOUBLE PRECISION NOT NULL,
  longitude DOUBLE PRECISION NOT NULL,
  altitude_meters DOUBLE PRECISION,
  accuracy_meters DOUBLE PRECISION,
  heading_degrees DOUBLE PRECISION,
  speed_mps DOUBLE PRECISION,
  source VARCHAR(20) NOT NULL,
  recorded_at TIMESTAMPTZ NOT NULL,
  received_at TIMESTAMPTZ NOT NULL,
  metadata JSONB
);

CREATE INDEX idx_positions_user_id ON positions (user_id);
CREATE INDEX idx_positions_recorded_at ON positions (recorded_at);
