package co.com.pragma.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SolicitudDetalladaDTO {
    private Double monto;
    private Integer plazoEnMeses;
    private String estado;
    private String correoCliente;
    private String nombreCliente;
    private Double salarioBaseCliente;
    private String nombreTipoPrestamo;
    private Double tasaInteres;
    private Double deudaTotalMensualAprobada;
}
