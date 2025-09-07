INSERT INTO tipos_prestamo (id, nombre, tasa_interes) VALUES
  (1, 'Vivienda', 7.5),
  (2, 'Vehículo', 10.2),
  (3, 'Libre Inversión', 15.0),
  (4, 'Educativo', 5.0)
    ON CONFLICT (id) DO NOTHING;
