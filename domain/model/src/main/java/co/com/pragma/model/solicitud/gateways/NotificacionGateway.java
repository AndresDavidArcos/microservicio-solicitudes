package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.solicitud.Notificacion;
import reactor.core.publisher.Mono;

public interface NotificacionGateway {
    Mono<Void> enviarNotificacion(Notificacion notificacion);
}
