package co.com.pragma.model.solicitud.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EstadoSolicitud {
    PENDIENTE_DE_REVISION("Pendiente de revisión"),
    RECHAZADA("Rechazada"),
    REVISION_MANUAL("Revision manual"),
    APROBADA("Aprobada"),
    EN_VALIDACION_AUTOMATICA("En validación automática");

    private final String valor;

    EstadoSolicitud(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return this.valor;
    }

    public static List<String> estadosParaAsesor() {
        return Arrays.stream(new EstadoSolicitud[]{PENDIENTE_DE_REVISION, RECHAZADA, REVISION_MANUAL})
                .map(EstadoSolicitud::toString)
                .collect(Collectors.toList());
    }
}
