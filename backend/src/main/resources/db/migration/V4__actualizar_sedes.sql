DELETE FROM asistencia_registros WHERE sesion_id IN (SELECT id FROM qr_sesiones WHERE sede_id = 2);
DELETE FROM qr_sesiones WHERE sede_id = 2;
DELETE FROM sedes WHERE id = 2;

UPDATE sedes SET latitud = 8.967414, longitud = -79.550392 WHERE id = 1;
