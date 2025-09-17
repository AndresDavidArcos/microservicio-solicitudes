package co.com.pragma.usecase.solicitud;

import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcesarDecisionUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @InjectMocks
    private ProcesarDecisionUseCase procesarDecisionUseCase;

    private Solicitud solicitudExistente;

    @BeforeEach
    void setUp() {
        solicitudExistente = Solicitud.builder()
                .id(1L)
                .estado("En validación automática")
                .build();
    }

    @Test
    @DisplayName("Prueba de actualización de estado exitosa")
    void procesarDecisionExitosa() {
        String nuevoEstado = "Aprobada";
        when(solicitudRepository.findById(anyLong())).thenReturn(Mono.just(solicitudExistente));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Solicitud> resultado = procesarDecisionUseCase.ejecutar(1L, nuevoEstado);

        StepVerifier.create(resultado)
                .expectNextMatches(solicitud -> solicitud.getEstado().equals(nuevoEstado))
                .verifyComplete();
    }

    @Test
    @DisplayName("Prueba de flujo cuando la solicitud no se encuentra")
    void procesarDecisionSolicitudNoEncontrada() {
        when(solicitudRepository.findById(anyLong())).thenReturn(Mono.empty());


        Mono<Solicitud> resultado = procesarDecisionUseCase.ejecutar(999L, "Aprobada");

        StepVerifier.create(resultado)
                .verifyComplete();
    }
}
