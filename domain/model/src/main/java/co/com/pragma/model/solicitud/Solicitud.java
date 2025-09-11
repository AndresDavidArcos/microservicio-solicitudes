package co.com.pragma.model.solicitud;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    private Long id;
    private String documentoIdentidadCliente;
    private Double monto;
    private Integer plazoEnMeses;
    private Long tipoPrestamoId;
    private String estado;
}
