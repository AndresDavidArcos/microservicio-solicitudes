package co.com.pragma.api;

import co.com.pragma.api.dto.ActualizarEstadoDTO;
import co.com.pragma.api.dto.SolicitudDTO;
import co.com.pragma.api.dto.SolicitudDetalladaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.Data;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class RouterRest {

    @Data
    private static class PageSolicitudDetalladaDTO {
        @ArraySchema(schema = @Schema(implementation = SolicitudDetalladaDTO.class))
        private List<SolicitudDetalladaDTO> content;
        private int currentPage;
        private long totalElements;
        private int totalPages;
    }

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "registrarSolicitud",
                    operation = @Operation(
                            summary = "Registrar una nueva solicitud de préstamo",
                            security = @SecurityRequirement(name = "bearerAuth"),
                            description = "Crea una nueva solicitud de préstamo para el usuario autenticado. Requiere rol de CLIENTE.",
                            operationId = "registrarSolicitud",
                            tags = {"Solicitudes"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de la solicitud de préstamo",
                                    content = @Content(schema = @Schema(implementation = SolicitudDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                                    @ApiResponse(responseCode = "401", description = "No autenticado"),
                                    @ApiResponse(responseCode = "403", description = "Acceso denegado (rol no es CLIENTE)")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listarSolicitudes",
                    operation = @Operation(
                            summary = "Listar solicitudes para revisión",
                            security = @SecurityRequirement(name = "bearerAuth"),
                            description = "Obtiene una lista paginada y filtrable de solicitudes. Requiere rol de ASESOR.",
                            operationId = "listarSolicitudes",
                            tags = {"Solicitudes"},
                            parameters = {
                                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Número de página a solicitar (inicia en 0)", schema = @Schema(type = "integer", defaultValue = "0")),
                                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Tamaño de la página", schema = @Schema(type = "integer", defaultValue = "10")),
                                    @Parameter(in = ParameterIn.QUERY, name = "estado", description = "Filtrar por uno o más estados (ej: ?estado=Rechazada&estado=Revision manual)", schema = @Schema(type = "string"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente", content = @Content(schema = @Schema(implementation = PageSolicitudDetalladaDTO.class))),
                                    @ApiResponse(responseCode = "401", description = "No autenticado"),
                                    @ApiResponse(responseCode = "403", description = "Acceso denegado (rol no es ASESOR)")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud/{id}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PUT,
                    beanClass = Handler.class,
                    beanMethod = "actualizarEstado",
                    operation = @Operation(
                            summary = "Aprobar o rechazar una solicitud",
                            security = @SecurityRequirement(name = "bearerAuth"),
                            description = "Cambia el estado de una solicitud existente a 'Aprobada' o 'Rechazada'. Requiere rol de ASESOR.",
                            operationId = "actualizarEstado",
                            tags = {"Solicitudes"},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID de la solicitud a actualizar", required = true, schema = @Schema(type = "integer", format = "int64"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Objeto con el nuevo estado para la solicitud.",
                                    content = @Content(schema = @Schema(implementation = ActualizarEstadoDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej: estado no permitido)"),
                                    @ApiResponse(responseCode = "401", description = "No autenticado"),
                                    @ApiResponse(responseCode = "403", description = "Acceso denegado (rol no es ASESOR)"),
                                    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/solicitud"), handler::registrarSolicitud)
                .andRoute(GET("/api/v1/solicitud"), handler::listarSolicitudes)
                .andRoute(PUT("/api/v1/solicitud/{id}"), handler::actualizarEstado);
    }
}
