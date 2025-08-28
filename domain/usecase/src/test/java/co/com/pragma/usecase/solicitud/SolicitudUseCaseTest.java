package co.com.pragma.usecase.solicitud;

import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private SolicitudUseCase solicitudUseCase;

    private Solicitud solicitud;

    @BeforeEach
    void setUp() {
        solicitud = Solicitud.builder()
                .documentoIdentidadCliente("123456789")
                .monto(10000000.0)
                .plazoEnMeses(24)
                .tipoPrestamoId(1L)
                .build();
    }

    @Test
    @DisplayName("Prueba de registro exitoso de una solicitud")
    void registrarSolicitudExitosa() {
        when(usuarioRepository.existePorDocumento(anyString())).thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.existePorId(anyLong())).thenReturn(Mono.just(true));

        when(solicitudRepository.guardar(any(Solicitud.class))).thenAnswer(invocation -> {
            Solicitud s = invocation.getArgument(0);
            s.setEstado("Pendiente de revisión");
            return Mono.just(s);
        });

        Mono<Solicitud> resultado = solicitudUseCase.registrarSolicitud(solicitud);

        StepVerifier.create(resultado)
                .expectNextMatches(s -> s.getEstado().equals("Pendiente de revisión"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Prueba de fallo al registrar porque el usuario no existe")
    void registrarSolicitudFalloUsuarioNoExiste() {
        when(usuarioRepository.existePorDocumento(anyString())).thenReturn(Mono.just(false));

        Mono<Solicitud> resultado = solicitudUseCase.registrarSolicitud(solicitud);

        StepVerifier.create(resultado)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Prueba de fallo al registrar porque el tipo de préstamo no existe")
    void registrarSolicitudFalloTipoPrestamoNoExiste() {
        when(usuarioRepository.existePorDocumento(anyString())).thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.existePorId(anyLong())).thenReturn(Mono.just(false));

        Mono<Solicitud> resultado = solicitudUseCase.registrarSolicitud(solicitud);

        StepVerifier.create(resultado)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Prueba de fallo por monto cero")
    void registrarSolicitudFalloDatosInvalidos() {
        solicitud.setMonto(0.0);

        Mono<Solicitud> resultado = solicitudUseCase.registrarSolicitud(solicitud);

        StepVerifier.create(resultado)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
