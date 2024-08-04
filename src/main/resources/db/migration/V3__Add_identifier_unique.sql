DELETE FROM users
WHERE id NOT IN (
    SELECT id FROM (
        SELECT MIN(id) AS id
        FROM users
        GROUP BY identification_nr
    ) AS temp_table
);
ALTER TABLE users ADD CONSTRAINT unique_document UNIQUE (identification_nr);