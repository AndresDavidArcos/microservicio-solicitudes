ALTER TABLE tipos_prestamo ADD COLUMN validacion_automatica BOOLEAN DEFAULT FALSE;

UPDATE tipos_prestamo SET validacion_automatica = TRUE WHERE nombre IN ('Vehículo', 'Libre Inversión');
