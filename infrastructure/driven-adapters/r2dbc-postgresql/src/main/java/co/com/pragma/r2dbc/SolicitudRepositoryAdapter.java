package co.com.pragma.r2dbc;

import co.com.pragma.model.page.Page;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class SolicitudRepositoryAdapter extends ReactiveAdapterOperations<Solicitud, SolicitudEntity, Long, SolicitudReactiveRepository> implements SolicitudRepository {

    public SolicitudRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Solicitud.class));
    }

    @Override
    public Mono<Solicitud> guardar(Solicitud solicitud) {
        return this.save(solicitud);
    }

    @Override
    public Mono<Page<Solicitud>> findAllPaginatedAndFiltered(List<String> estados, int page, int size) {
        long offset = (long) page * size;

        Mono<Long> totalElementsMono = repository.countByEstadoIn(estados);
        Flux<Solicitud> contentFlux = repository.findByEstadoIn(estados, size, offset)
                .map(this::toEntity);

        return Mono.zip(contentFlux.collectList(), totalElementsMono)
                .map(tuple -> {
                    List<Solicitud> content = tuple.getT1();
                    long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / size);

                    return Page.<Solicitud>builder()
                            .content(content)
                            .currentPage(page)
                            .totalElements(totalElements)
                            .totalPages(totalPages)
                            .build();
                });
    }

    @Override
    public Flux<Solicitud> findAprobadasPorCliente(String documentoIdentidadCliente) {
        return repository.findByDocumentoIdentidadClienteAndEstado(documentoIdentidadCliente, "Aprobada")
                .map(this::toEntity);
    }

    @Override
    public Mono<Solicitud> findById(Long idSolicitud) {
        return super.findById(idSolicitud);
    }

}
