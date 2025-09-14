package co.com.pragma.usecase.solicitud;

import co.com.pragma.model.exception.BusinessValidationException;
import co.com.pragma.model.exception.UnauthorizedException;
import co.com.pragma.model.solicitud.PrestamoAprobadoInfo;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.ValidacionPayload;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.solicitud.gateways.ValidacionAutomaticaGateway;
import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.model.usuario.User;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ValidacionAutomaticaGateway validacionAutomaticaGateway;

    @InjectMocks
    private SolicitudUseCase solicitudUseCase;

    @Captor
    private ArgumentCaptor<ValidacionPayload> payloadCaptor;

    private Solicitud solicitud;
    private String documentoAutenticado;
    private TipoPrestamo tipoPrestamoManual;
    private TipoPrestamo tipoPrestamoAutomatico;

    @BeforeEach
    void setUp() {
        documentoAutenticado = "123456789";
        solicitud = Solicitud.builder()
                .monto(10000000.0)
                .plazoEnMeses(24)
                .tipoPrestamoId(1L)
                .build();

        tipoPrestamoManual = TipoPrestamo.builder()
                .id(1L)
                .validacionAutomatica(false)
                .build();

        tipoPrestamoAutomatico = TipoPrestamo.builder()
                .id(2L)
                .tasaInteres(15.0)
                .validacionAutomatica(true)
                .build();
    }

    @Test
    @DisplayName("Prueba de registro exitoso para un préstamo de validación MANUAL")
    void registrarSolicitud_Manual_Exitoso() {
        when(usuarioRepository.existePorDocumento(anyString())).thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(1L)).thenReturn(Mono.just(tipoPrestamoManual));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Solicitud> resultado = solicitudUseCase.registrarSolicitud(solicitud, documentoAutenticado);

        StepVerifier.create(resultado)
                .expectNextMatches(s -> s.getEstado().equals("Pendiente de revisión")
                        && s.getDocumentoIdentidadCliente().equals(documentoAutenticado))
                .verifyComplete();
    }

    @Test
    @DisplayName("Prueba de registro exitoso para un préstamo de validación AUTOMÁTICA")
    void registrarSolicitud_Automatica_Exitoso() {
        solicitud.setTipoPrestamoId(2L);
        Solicitud prestamoPrevio = Solicitud.builder().monto(500000.0).plazoEnMeses(12).tipoPrestamoId(1L).build();
        TipoPrestamo tipoPrestamoPrevio = TipoPrestamo.builder().id(1L).tasaInteres(10.0).build();

        when(usuarioRepository.existePorDocumento(anyString())).thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(2L)).thenReturn(Mono.just(tipoPrestamoAutomatico));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(usuarioRepository.buscarPorDocumento(anyString())).thenReturn(Mono.just(new User()));
        when(solicitudRepository.findAprobadasPorCliente(anyString())).thenReturn(Flux.just(prestamoPrevio));
        when(tipoPrestamoRepository.findById(1L)).thenReturn(Mono.just(tipoPrestamoPrevio));
        when(validacionAutomaticaGateway.encolarParaValidacion(any(ValidacionPayload.class))).thenReturn(Mono.empty());

        Mono<Solicitud> resultado = solicitudUseCase.registrarSolicitud(solicitud, documentoAutenticado);

        StepVerifier.create(resultado)
                .expectNextMatches(s -> s.getEstado().equals("En validación automática"))
                .verifyComplete();

        verify(validacionAutomaticaGateway).encolarParaValidacion(payloadCaptor.capture());
        PrestamoAprobadoInfo info = payloadCaptor.getValue().getPrestamosAprobados().get(0);
        assertEquals(500000.0, info.getMonto());
        assertEquals(10.0, info.getTasaInteres());
    }


    @Test
    @DisplayName("Prueba de fallo al registrar porque el usuario no existe")
    void registrarSolicitud_Fallo_UsuarioNoExiste() {
        when(usuarioRepository.existePorDocumento(anyString())).thenReturn(Mono.just(false));

        Mono<Solicitud> resultado = solicitudUseCase.registrarSolicitud(solicitud, documentoAutenticado);

        StepVerifier.create(resultado)
                .expectError(UnauthorizedException.class)
                .verify();
    }

    @Test
    @DisplayName("Prueba de fallo al registrar porque el tipo de préstamo no existe")
    void registrarSolicitud_Fallo_TipoPrestamoNoExiste() {
        when(usuarioRepository.existePorDocumento(anyString())).thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.findById(anyLong())).thenReturn(Mono.empty());

        Mono<Solicitud> resultado = solicitudUseCase.registrarSolicitud(solicitud, documentoAutenticado);

        StepVerifier.create(resultado)
                .expectError(BusinessValidationException.class)
                .verify();
    }
}

