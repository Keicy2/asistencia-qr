ALTER TABLE usuarios ADD COLUMN username VARCHAR(100) UNIQUE;

UPDATE usuarios SET username = LOWER(SUBSTRING(correo, 1, POSITION('@' IN correo) - 1)) WHERE username IS NULL;

ALTER TABLE usuarios ALTER COLUMN username SET NOT NULL;
