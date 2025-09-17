package co.com.pragma.usecase.solicitud;

import co.com.pragma.model.page.Page;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.SolicitudDetallada;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.model.tipoprestamo.TipoPrestamo;
import co.com.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.pragma.model.usuario.User;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ListarSolicitudesUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;

    public Mono<Page<SolicitudDetallada>> ejecutar(int page, int size, List<String> estados) {
        List<String> estadosAFiltrar = (estados == null || estados.isEmpty())
                ? EstadoSolicitud.estadosParaAsesor()
                : estados;

        return solicitudRepository.findAllPaginatedAndFiltered(estadosAFiltrar, page, size)
                .flatMap(paginaSolicitudes -> {
                    Flux<SolicitudDetallada> contenidoEnriquecido = Flux.fromIterable(paginaSolicitudes.getContent())
                            .flatMap(solicitud -> enriquecerSolicitud(solicitud));

                    return contenidoEnriquecido.collectList()
                            .map(list -> Page.<SolicitudDetallada>builder()
                                    .content(list)
                                    .currentPage(paginaSolicitudes.getCurrentPage())
                                    .totalElements(paginaSolicitudes.getTotalElements())
                                    .totalPages(paginaSolicitudes.getTotalPages())
                                    .build());
                });
    }

    private Mono<SolicitudDetallada> enriquecerSolicitud(Solicitud solicitud) {
        Mono<User> usuarioMono = usuarioRepository.buscarPorDocumento(solicitud.getDocumentoIdentidadCliente())
                .defaultIfEmpty(new User());

        Mono<TipoPrestamo> tipoPrestamoMono = tipoPrestamoRepository.findById(solicitud.getTipoPrestamoId())
                .defaultIfEmpty(new TipoPrestamo());

        Mono<Double> deudaMono = solicitudRepository.findAprobadasPorCliente(solicitud.getDocumentoIdentidadCliente())
                .map(s -> s.getMonto() / s.getPlazoEnMeses())
                .reduce(0.0, (acumulador, valorActual) -> acumulador + valorActual);

        return Mono.zip(usuarioMono, tipoPrestamoMono, deudaMono)
                .map(tuple -> {
                    User user = tuple.getT1();
                    TipoPrestamo tipoPrestamo = tuple.getT2();
                    Double deudaTotal = tuple.getT3();

                    return SolicitudDetallada.builder()
                            .id(solicitud.getId())
                            .monto(solicitud.getMonto())
                            .plazoEnMeses(solicitud.getPlazoEnMeses())
                            .estado(solicitud.getEstado())
                            .correoCliente(user.getCorreoElectronico())
                            .nombreCliente(user.getNombres() + " " + user.getApellidos())
                            .salarioBaseCliente(user.getSalarioBase())
                            .nombreTipoPrestamo(tipoPrestamo.getNombre())
                            .tasaInteres(tipoPrestamo.getTasaInteres())
                            .deudaTotalMensualAprobada(deudaTotal)
                            .build();
                });
    }
}
