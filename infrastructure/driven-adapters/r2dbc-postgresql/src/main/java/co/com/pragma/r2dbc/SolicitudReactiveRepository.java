package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.SolicitudEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SolicitudReactiveRepository extends ReactiveCrudRepository<SolicitudEntity, Long>, ReactiveQueryByExampleExecutor<SolicitudEntity> {

    @Query("SELECT count(*) FROM solicitudes WHERE estado IN (:estados)")
    Mono<Long> countByEstadoIn(List<String> estados);

    @Query("SELECT * FROM solicitudes WHERE estado IN (:estados) ORDER BY id ASC LIMIT :size OFFSET :offset")
    Flux<SolicitudEntity> findByEstadoIn(List<String> estados, int size, long offset);

    Flux<SolicitudEntity> findByDocumentoIdentidadClienteAndEstado(String documentoIdentidadCliente, String estado);
}
