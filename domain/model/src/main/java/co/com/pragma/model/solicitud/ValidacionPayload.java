package co.com.pragma.model.solicitud;

import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.model.usuario.User;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ValidacionPayload {
    private Solicitud solicitudActual;
    private User usuario;
    private TipoPrestamo tipoPrestamo;
    private List<PrestamoAprobadoInfo> prestamosAprobados;
}
