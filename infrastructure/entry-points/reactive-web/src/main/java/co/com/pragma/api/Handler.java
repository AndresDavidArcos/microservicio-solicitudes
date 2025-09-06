package co.com.pragma.api;

import co.com.pragma.api.dto.SolicitudDTO;
import co.com.pragma.api.dto.SolicitudDetalladaDTO;
import co.com.pragma.api.mapper.SolicitudDTOMapper;
import co.com.pragma.api.validation.ValidatorHandler;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.usecase.solicitud.ListarSolicitudesUseCase;
import co.com.pragma.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Handler {
    private final SolicitudUseCase solicitudUseCase;
    private final ListarSolicitudesUseCase listarSolicitudesUseCase;
    private final SolicitudDTOMapper solicitudDTOMapper;
    private final ValidatorHandler validatorHandler;

    public Mono<ServerResponse> registrarSolicitud(ServerRequest serverRequest) {
        Mono<String> documentoAutenticadoMono = ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName);

        Mono<Solicitud> solicitudMono = serverRequest.bodyToMono(SolicitudDTO.class)
                .flatMap(validatorHandler::validate)
                .map(solicitudDTOMapper::toModel);

        return Mono.zip(solicitudMono, documentoAutenticadoMono)
                .flatMap(tuple -> solicitudUseCase.registrarSolicitud(tuple.getT1(), tuple.getT2()))
                .flatMap(solicitudGuardada -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(solicitudGuardada));
    }

    public Mono<ServerResponse> listarSolicitudes(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader(HttpHeaders.AUTHORIZATION);

        int page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(10);
        List<String> estados = serverRequest.queryParams().get("estado");

        return listarSolicitudesUseCase.ejecutar(page, size, estados)
                .map(solicitudDTOMapper::toPageDTO)
                .flatMap(pageDTO -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pageDTO));
    }
}
