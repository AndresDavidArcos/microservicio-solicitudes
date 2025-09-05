package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.PageDTO;
import co.com.pragma.api.dto.SolicitudDTO;
import co.com.pragma.api.dto.SolicitudDetalladaDTO;
import co.com.pragma.model.page.Page;
import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.SolicitudDetallada;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SolicitudDTOMapper {
    Solicitud toModel(SolicitudDTO dto);
    SolicitudDetalladaDTO toDetalladaDTO(SolicitudDetallada model);
    default PageDTO<SolicitudDetalladaDTO> toPageDTO(Page<SolicitudDetallada> page) {
        return PageDTO.<SolicitudDetalladaDTO>builder()
                .content(page.getContent().stream().map(this::toDetalladaDTO).collect(Collectors.toList()))
                .currentPage(page.getCurrentPage())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
