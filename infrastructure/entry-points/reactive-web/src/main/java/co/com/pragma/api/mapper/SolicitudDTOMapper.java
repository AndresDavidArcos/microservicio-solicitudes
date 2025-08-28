package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.SolicitudDTO;
import co.com.pragma.model.solicitud.Solicitud;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolicitudDTOMapper {
    Solicitud toModel(SolicitudDTO dto);
    SolicitudDTO toDTO(Solicitud model);
}
