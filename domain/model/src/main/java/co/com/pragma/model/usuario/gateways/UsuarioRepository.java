package co.com.pragma.model.usuario.gateways;

import co.com.pragma.model.usuario.User;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Boolean> existePorDocumento(String documentoIdentidad);
    Mono<User> buscarPorDocumento(String documentoIdentidad, String token);
}
