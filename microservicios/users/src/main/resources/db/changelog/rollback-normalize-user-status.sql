UPDATE users
SET status = LOWER(status)
WHERE status IN ('ACTIVE', 'INACTIVE', 'BLOCKED');
