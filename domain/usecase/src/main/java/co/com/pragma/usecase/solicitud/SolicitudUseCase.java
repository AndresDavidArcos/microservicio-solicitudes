package co.com.pragma.usecase.solicitud;

import co.com.pragma.model.exception.BusinessValidationException;
import co.com.pragma.model.exception.UnauthorizedException;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final UsuarioRepository usuarioRepository;

    public Mono<Solicitud> registrarSolicitud(Solicitud solicitud, String documentoAutenticado) {

        solicitud.setDocumentoIdentidadCliente(documentoAutenticado);

        return usuarioRepository.existePorDocumento(solicitud.getDocumentoIdentidadCliente())
                .flatMap(existeUsuario -> {
                    if (Boolean.FALSE.equals(existeUsuario)) {
                        return Mono.error(new UnauthorizedException("El cliente no se encuentra registrado."));
                    }
                    return tipoPrestamoRepository.existePorId(solicitud.getTipoPrestamoId())
                            .flatMap(existeTipo -> {
                                if (Boolean.FALSE.equals(existeTipo)) {
                                    return Mono.error(new BusinessValidationException("El tipo de préstamo seleccionado no es válido."));
                                }
                                solicitud.setEstado("Pendiente de revisión");
                                return solicitudRepository.guardar(solicitud);
                            });
                });
    }
}
