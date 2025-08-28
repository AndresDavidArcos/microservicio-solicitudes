package co.com.pragma.r2dbc;

import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {

    private final TipoPrestamoReactiveRepository repository;

    public TipoPrestamoRepositoryAdapter(TipoPrestamoReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Boolean> existePorId(Long id) {
        return repository.existsById(id);
    }
}
