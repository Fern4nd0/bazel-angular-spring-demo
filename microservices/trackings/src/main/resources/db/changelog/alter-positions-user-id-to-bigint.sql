ALTER TABLE positions
  ALTER COLUMN user_id TYPE BIGINT USING user_id::bigint;
