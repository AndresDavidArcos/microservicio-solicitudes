package co.com.pragma.api.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudDTO {

    @NotNull(message = "El monto es obligatorio.")
    @Min(value = 1, message = "El monto debe ser mayor a cero.")
    private Double monto;

    @NotNull(message = "El plazo es obligatorio.")
    @Min(value = 1, message = "El plazo debe ser de al menos un mes.")
    private Integer plazoEnMeses;

    @NotNull(message = "El tipo de préstamo es obligatorio.")
    private Long tipoPrestamoId;
}
