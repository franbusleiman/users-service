DELETE FROM users WHERE id NOT IN (
    SELECT MIN(id)
    FROM users
    GROUP BY identification_nr
);
ALTER TABLE users ADD CONSTRAINT unique_document UNIQUE (identification_nr);