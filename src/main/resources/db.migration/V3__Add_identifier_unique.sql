DELETE FROM users
WHERE id NOT IN (
    SELECT MIN(id)
    FROM users
    GROUP BY identificationNr
);
ALTER TABLE users ADD CONSTRAINT unique_document UNIQUE (identificationNr);