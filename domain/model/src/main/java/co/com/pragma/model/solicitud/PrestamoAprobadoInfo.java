package co.com.pragma.model.solicitud;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class PrestamoAprobadoInfo {
    private Double monto;
    private Integer plazoEnMeses;
    private Double tasaInteres;
}
