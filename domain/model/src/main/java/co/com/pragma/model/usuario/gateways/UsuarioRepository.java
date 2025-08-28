package co.com.pragma.model.usuario.gateways;

import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Boolean> existePorDocumento(String documentoIdentidad);
}
