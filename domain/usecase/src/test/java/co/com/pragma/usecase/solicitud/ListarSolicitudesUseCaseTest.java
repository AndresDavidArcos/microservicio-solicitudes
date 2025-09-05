package co.com.pragma.usecase.solicitud;

import co.com.pragma.model.page.Page;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.SolicitudDetallada;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.model.usuario.User;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarSolicitudesUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @InjectMocks
    private ListarSolicitudesUseCase listarSolicitudesUseCase;

    private Solicitud solicitud;
    private User user;
    private TipoPrestamo tipoPrestamo;

    @BeforeEach
    void setUp() {
        solicitud = Solicitud.builder()
                .documentoIdentidadCliente("123")
                .monto(50000.0)
                .plazoEnMeses(12)
                .estado("Pendiente de revisión")
                .tipoPrestamoId(1L)
                .build();

        user = User.builder()
                .nombres("Test").apellidos("User")
                .correoElectronico("test@user.com")
                .salarioBase(2000000.0)
                .build();

        tipoPrestamo = TipoPrestamo.builder()
                .nombre("Vivienda")
                .tasaInteres(8.5)
                .build();
    }

    @Test
    @DisplayName("Prueba de listado y enriquecimiento exitoso de solicitudes")
    void listarYEnriquecerSolicitudesCorrectamente() {
        Page<Solicitud> paginaSolicitudes = Page.<Solicitud>builder()
                .content(List.of(solicitud))
                .currentPage(0)
                .totalElements(1L)
                .totalPages(1)
                .build();

        Solicitud solicitudAprobada1 = Solicitud.builder().monto(12000.0).plazoEnMeses(12).build();
        Solicitud solicitudAprobada2 = Solicitud.builder().monto(30000.0).plazoEnMeses(10).build();

        when(solicitudRepository.findAllPaginatedAndFiltered(anyList(), anyInt(), anyInt())).thenReturn(Mono.just(paginaSolicitudes));
        when(usuarioRepository.buscarPorDocumento(anyString(), anyString())).thenReturn(Mono.just(user));
        when(tipoPrestamoRepository.findById(anyLong())).thenReturn(Mono.just(tipoPrestamo));
        when(solicitudRepository.findAprobadasPorCliente(anyString())).thenReturn(Flux.just(solicitudAprobada1, solicitudAprobada2));

        Mono<Page<SolicitudDetallada>> resultado = listarSolicitudesUseCase.ejecutar("token", 0, 10, Collections.emptyList());

        StepVerifier.create(resultado)
                .expectNextMatches(page -> {
                    SolicitudDetallada detallada = page.getContent().get(0);
                    return page.getCurrentPage() == 0 &&
                            page.getTotalElements() == 1L &&
                            detallada.getNombreCliente().equals("Test User") &&
                            detallada.getNombreTipoPrestamo().equals("Vivienda") &&
                            detallada.getDeudaTotalMensualAprobada() == 4000.0;
                })
                .verifyComplete();
    }
}
