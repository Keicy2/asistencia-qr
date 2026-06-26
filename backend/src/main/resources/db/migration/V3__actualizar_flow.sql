ALTER TABLE usuarios ADD COLUMN hora_entrada TIME;
ALTER TABLE usuarios ADD COLUMN hora_salida TIME;
ALTER TABLE usuarios ADD COLUMN estado VARCHAR(50) NOT NULL DEFAULT 'activo';

ALTER TABLE qr_sesiones DROP COLUMN hora_entrada;
ALTER TABLE qr_sesiones ALTER COLUMN expira_en DROP NOT NULL;

ALTER TABLE asistencia_registros ADD COLUMN usuario_id BIGINT REFERENCES usuarios(id);
ALTER TABLE asistencia_registros ADD COLUMN estado VARCHAR(50);
ALTER TABLE asistencia_registros ADD COLUMN hora_programada TIME;

CREATE INDEX idx_asistencia_usuario ON asistencia_registros(usuario_id);
