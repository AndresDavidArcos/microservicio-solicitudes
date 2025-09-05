package co.com.pragma.model.tipoprestamo.gateways;

import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {
    Mono<Boolean> existePorId(Long id);
    Mono<TipoPrestamo> findById(Long id);
}
