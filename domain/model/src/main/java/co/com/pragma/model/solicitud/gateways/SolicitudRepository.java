package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.page.Page;
import co.com.pragma.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SolicitudRepository {
    Mono<Solicitud> guardar(Solicitud solicitud);
    Mono<Page<Solicitud>> findAllPaginatedAndFiltered(List<String> estados, int page, int size);
    Flux<Solicitud> findAprobadasPorCliente(String documentoIdentidadCliente);
}
