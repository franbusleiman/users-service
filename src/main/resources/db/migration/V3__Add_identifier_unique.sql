-- Delete related rows in users_roles
DELETE FROM users_roles
WHERE users_id NOT IN (
    SELECT id FROM (
        SELECT MIN(id) AS id
        FROM users
        GROUP BY identification_nr
    ) AS temp_table
);

-- Delete from users table
DELETE FROM users
WHERE id NOT IN (
    SELECT id FROM (
        SELECT MIN(id) AS id
        FROM users
        GROUP BY identification_nr
    ) AS temp_table
);
ALTER TABLE users ADD CONSTRAINT unique_document UNIQUE (identification_nr);