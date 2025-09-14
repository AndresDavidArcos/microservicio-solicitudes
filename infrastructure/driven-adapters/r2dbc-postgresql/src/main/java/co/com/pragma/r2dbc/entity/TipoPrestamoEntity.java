package co.com.pragma.r2dbc.entity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("tipos_prestamo")
public class TipoPrestamoEntity {
    @Id
    private Long id;
    private String nombre;
    @Column("tasa_interes")
    private Double tasaInteres;
    @Column("validacion_automatica")
    private Boolean validacionAutomatica;
}
