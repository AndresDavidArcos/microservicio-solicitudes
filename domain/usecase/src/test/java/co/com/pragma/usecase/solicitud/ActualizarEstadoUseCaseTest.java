package co.com.pragma.usecase.solicitud;

import co.com.pragma.model.exception.BusinessValidationException;
import co.com.pragma.model.solicitud.Notificacion;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.AprobacionGateway;
import co.com.pragma.model.solicitud.gateways.NotificacionGateway;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.usuario.User;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarEstadoUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private NotificacionGateway notificacionGateway;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AprobacionGateway aprobacionGateway;

    @InjectMocks
    private ActualizarEstadoUseCase actualizarEstadoUseCase;

    private Solicitud solicitudExistente;
    private User usuarioExistente;

    @BeforeEach
    void setUp() {
        solicitudExistente = Solicitud.builder()
                .id(1L)
                .documentoIdentidadCliente("12345")
                .estado("Pendiente de revisión")
                .build();

        usuarioExistente = User.builder()
                .documentoIdentidad("12345")
                .correoElectronico("test@cliente.com")
                .build();
    }

    @Test
    @DisplayName("Prueba de actualización de estado exitosa a Aprobada")
    void actualizarEstadoExitosoAprobada() {
        String nuevoEstado = "Aprobada";
        when(solicitudRepository.findById(anyLong())).thenReturn(Mono.just(solicitudExistente));
        when(usuarioRepository.buscarPorDocumento(anyString())).thenReturn(Mono.just(usuarioExistente));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(notificacionGateway.enviarNotificacion(any(Notificacion.class))).thenReturn(Mono.empty());
        when(aprobacionGateway.notificarAprobacion(any(Solicitud.class))).thenReturn(Mono.empty());

        Mono<Solicitud> resultado = actualizarEstadoUseCase.ejecutar(1L, nuevoEstado);

        StepVerifier.create(resultado)
                .expectNextMatches(solicitud -> solicitud.getEstado().equals(nuevoEstado))
                .verifyComplete();
    }

    @Test
    @DisplayName("Prueba de fallo por estado no válido")
    void actualizarEstadoFalloEstadoInvalido() {
        String nuevoEstado = "EstadoIncorrecto";

        Mono<Solicitud> resultado = actualizarEstadoUseCase.ejecutar(1L, nuevoEstado);

        StepVerifier.create(resultado)
                .expectErrorMatches(throwable -> throwable instanceof BusinessValidationException &&
                        throwable.getMessage().equals("El estado proporcionado no es válido."))
                .verify();
    }

    @Test
    @DisplayName("Prueba de fallo porque la solicitud no existe")
    void actualizarEstadoFalloSolicitudNoEncontrada() {
        when(solicitudRepository.findById(anyLong())).thenReturn(Mono.empty());

        Mono<Solicitud> resultado = actualizarEstadoUseCase.ejecutar(999L, "Aprobada");

        StepVerifier.create(resultado)
                .expectErrorMatches(throwable -> throwable instanceof BusinessValidationException &&
                        throwable.getMessage().equals("La solicitud no fue encontrada."))
                .verify();
    }
}


