package co.com.pragma.api;

import co.com.pragma.api.dto.SolicitudDTO;
import co.com.pragma.api.mapper.SolicitudDTOMapper;
import co.com.pragma.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final SolicitudUseCase solicitudUseCase;
    private final SolicitudDTOMapper solicitudDTOMapper;

    public Mono<ServerResponse> registrarSolicitud(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SolicitudDTO.class)
                .map(solicitudDTOMapper::toModel)
                .flatMap(solicitudUseCase::registrarSolicitud)
                .flatMap(solicitudGuardada -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(solicitudGuardada))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}

