package co.com.pragma.r2dbc.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("solicitudes")
public class SolicitudEntity {
    @Id
    private Long id;
    @Column("documento_identidad_cliente")
    private String documentoIdentidadCliente;
    private Double monto;
    @Column("plazo_en_meses")
    private Integer plazoEnMeses;
    @Column("tipo_prestamo_id")
    private Long tipoPrestamoId;
    private String estado;
}
