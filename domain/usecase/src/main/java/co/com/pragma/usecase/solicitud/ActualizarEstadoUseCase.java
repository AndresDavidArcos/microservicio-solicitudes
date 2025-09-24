package co.com.pragma.usecase.solicitud;

import co.com.pragma.model.exception.BusinessValidationException;
import co.com.pragma.model.solicitud.Notificacion;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.gateways.AprobacionGateway;
import co.com.pragma.model.solicitud.gateways.NotificacionGateway;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.usuario.User;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
@RequiredArgsConstructor
public class ActualizarEstadoUseCase {

    private static final String ERROR_ESTADO_INVALIDO = "El estado proporcionado no es válido.";
    private static final String ERROR_SOLICITUD_NO_ENCONTRADA = "La solicitud no fue encontrada.";

    private final SolicitudRepository solicitudRepository;
    private final NotificacionGateway notificacionGateway;
    private final UsuarioRepository usuarioRepository;
    private final AprobacionGateway aprobacionGateway;

    public Mono<Solicitud> ejecutar(Long idSolicitud, String nuevoEstado) {
        if (!List.of(EstadoSolicitud.APROBADA.toString(), EstadoSolicitud.RECHAZADA.toString()).contains(nuevoEstado)) {
            return Mono.error(new BusinessValidationException(ERROR_ESTADO_INVALIDO));
        }

        return solicitudRepository.findById(idSolicitud)
                .switchIfEmpty(Mono.error(new BusinessValidationException(ERROR_SOLICITUD_NO_ENCONTRADA)))
                .flatMap(solicitud -> Mono.zip(Mono.just(solicitud), usuarioRepository.buscarPorDocumento(solicitud.getDocumentoIdentidadCliente())))
                .flatMap(tuple -> {
                    Solicitud solicitud = tuple.getT1();
                    User user = tuple.getT2();

                    solicitud.setEstado(nuevoEstado);
                    return solicitudRepository.guardar(solicitud)
                            .flatMap(solicitudGuardada -> {
                                Notificacion notificacion = Notificacion.builder()
                                        .solicitud(solicitudGuardada)
                                        .correoDestinatario(user.getCorreoElectronico())
                                        .build();

                                Mono<Void> notificacionClienteMono = notificacionGateway.enviarNotificacion(notificacion);

                                Mono<Void> notificacionReporteMono = Mono.empty();
                                if (EstadoSolicitud.APROBADA.toString().equals(solicitudGuardada.getEstado())) {
                                    notificacionReporteMono = aprobacionGateway.notificarAprobacion(solicitudGuardada);
                                }

                                return Mono.when(notificacionClienteMono, notificacionReporteMono)
                                        .thenReturn(solicitudGuardada);
                            });
                });
    }
}
