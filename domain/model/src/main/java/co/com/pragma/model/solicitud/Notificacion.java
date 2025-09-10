package co.com.pragma.model.solicitud;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Notificacion {
    private Solicitud solicitud;
    private String correoDestinatario;
}
