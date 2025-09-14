package co.com.pragma.model.solicitud.gateways;
import co.com.pragma.model.solicitud.ValidacionPayload;
import reactor.core.publisher.Mono;

public interface ValidacionAutomaticaGateway {
    Mono<Void> encolarParaValidacion(ValidacionPayload payload);
}
