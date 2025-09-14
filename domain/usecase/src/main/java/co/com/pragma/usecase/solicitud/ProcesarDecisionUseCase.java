package co.com.pragma.usecase.solicitud;

import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProcesarDecisionUseCase {

    private final SolicitudRepository solicitudRepository;

    public Mono<Solicitud> ejecutar(Long idSolicitud, String nuevoEstado) {
        return solicitudRepository.findById(idSolicitud)
                .flatMap(solicitud -> {
                    solicitud.setEstado(nuevoEstado);
                    return solicitudRepository.guardar(solicitud);
                });
    }
}
