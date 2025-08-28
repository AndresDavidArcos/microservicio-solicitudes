package co.com.pragma.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDTO {
    private String documentoIdentidadCliente;
    private Double monto;
    private Integer plazoEnMeses;
    private Long tipoPrestamoId;
}
