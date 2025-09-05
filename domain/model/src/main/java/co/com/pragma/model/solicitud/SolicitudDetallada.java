package co.com.pragma.model.solicitud;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class SolicitudDetallada {
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
