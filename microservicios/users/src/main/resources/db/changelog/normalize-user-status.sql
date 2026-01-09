UPDATE users
SET status = UPPER(status)
WHERE status IN ('active', 'inactive', 'blocked');
