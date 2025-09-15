package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface AprobacionGateway {
    Mono<Void> notificarAprobacion(Solicitud solicitud);
}
