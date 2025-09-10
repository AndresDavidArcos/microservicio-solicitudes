package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarEstadoDTO {
    @NotBlank(message = "El nuevo estado es obligatorio.")
    private String estado;
}
