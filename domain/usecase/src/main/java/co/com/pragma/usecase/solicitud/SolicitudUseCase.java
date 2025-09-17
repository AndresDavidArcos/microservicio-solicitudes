package co.com.pragma.usecase.solicitud;

import co.com.pragma.model.exception.BusinessValidationException;
import co.com.pragma.model.exception.UnauthorizedException;
import co.com.pragma.model.solicitud.PrestamoAprobadoInfo;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.ValidacionPayload;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.gateways.ValidacionAutomaticaGateway;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.model.usuario.User;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final ValidacionAutomaticaGateway validacionAutomaticaGateway;
    private final UsuarioRepository usuarioRepository;

    public Mono<Solicitud> registrarSolicitud(Solicitud solicitud, String documentoAutenticado) {

        solicitud.setDocumentoIdentidadCliente(documentoAutenticado);

        return usuarioRepository.existePorDocumento(solicitud.getDocumentoIdentidadCliente())
                .flatMap(existeUsuario -> {
                    if (Boolean.FALSE.equals(existeUsuario)) {
                        return Mono.error(new UnauthorizedException("El cliente no se encuentra registrado."));
                    }
                    return tipoPrestamoRepository.findById(solicitud.getTipoPrestamoId())
                            .switchIfEmpty(Mono.error(new BusinessValidationException("El tipo de préstamo seleccionado no es válido.")))
                            .flatMap(tipoPrestamo -> {
                                if (Boolean.TRUE.equals(tipoPrestamo.getValidacionAutomatica())) {
                                    solicitud.setEstado(EstadoSolicitud.EN_VALIDACION_AUTOMATICA.toString());
                                    return solicitudRepository.guardar(solicitud)
                                            .flatMap(solicitudGuardada -> enriquecerYEncolar(solicitudGuardada, tipoPrestamo));
                                } else {
                                    solicitud.setEstado(EstadoSolicitud.PENDIENTE_DE_REVISION.toString());
                                    return solicitudRepository.guardar(solicitud);
                                }
                            });
                });
    }

    private Mono<Solicitud> enriquecerYEncolar(Solicitud solicitud, TipoPrestamo tipoPrestamo) {
        Mono<User> usuarioMono = usuarioRepository.buscarPorDocumento(solicitud.getDocumentoIdentidadCliente());
        Mono<List<PrestamoAprobadoInfo>> aprobadosMono = solicitudRepository.findAprobadasPorCliente(solicitud.getDocumentoIdentidadCliente())
                .flatMap(solicitudAprobada ->
                        tipoPrestamoRepository.findById(solicitudAprobada.getTipoPrestamoId())
                                .map(tipoPrestamoAprobado -> PrestamoAprobadoInfo.builder()
                                        .monto(solicitudAprobada.getMonto())
                                        .plazoEnMeses(solicitudAprobada.getPlazoEnMeses())
                                        .tasaInteres(tipoPrestamoAprobado.getTasaInteres())
                                        .build())
                ).collectList();

        return Mono.zip(usuarioMono, aprobadosMono)
                .flatMap(tuple -> {
                    User user = tuple.getT1();
                    List<PrestamoAprobadoInfo> aprobados = tuple.getT2();

                    ValidacionPayload payload = ValidacionPayload.builder()
                            .solicitudActual(solicitud)
                            .usuario(user)
                            .tipoPrestamo(tipoPrestamo)
                            .prestamosAprobados(aprobados)
                            .build();

                    return validacionAutomaticaGateway.encolarParaValidacion(payload)
                            .thenReturn(solicitud);
                });
    }

}
