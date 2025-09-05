package co.com.pragma.consumer;

import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
public class UsuarioRestConsumer implements UsuarioRepository {

    private final WebClient client;

    public UsuarioRestConsumer(@Value("${adapters.restconsumer.url}") String url) {
        this.client = WebClient.builder().baseUrl(url).build();
    }

    @Override
    @CircuitBreaker(name = "authCircuitBreaker")
    public Mono<Boolean> existePorDocumento(String documentoIdentidad) {
        return client.head()
                .uri("/api/v1/usuarios/existe/{documento}", documentoIdentidad)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false);
    }

    @Override
    @CircuitBreaker(name = "buscarUsuario", fallbackMethod = "fallbackBuscarUsuario")
    public Mono<User> buscarPorDocumento(String documentoIdentidad, String token) {
        return client.get()
                .uri("/api/v1/usuarios/{documento}", documentoIdentidad)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(User.class);
    }

    public Mono<User> fallbackBuscarUsuario(String documento, String token, Throwable ex) {
        return Mono.just(new User());
    }
}
