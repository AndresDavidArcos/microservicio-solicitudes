CREATE TABLE tipos_prestamo (
                                id BIGSERIAL PRIMARY KEY,
                                nombre VARCHAR(255) NOT NULL,
                                tasa_interes NUMERIC(5, 2)
);

CREATE TABLE solicitudes (
                             id BIGSERIAL PRIMARY KEY,
                             documento_identidad_cliente VARCHAR(255) NOT NULL,
                             monto NUMERIC(15, 2) NOT NULL,
                             plazo_en_meses INTEGER NOT NULL,
                             estado VARCHAR(100) NOT NULL,
                             tipo_prestamo_id BIGINT NOT NULL,
                             CONSTRAINT fk_tipo_prestamo
                                 FOREIGN KEY(tipo_prestamo_id)
                                     REFERENCES tipos_prestamo(id)
);
