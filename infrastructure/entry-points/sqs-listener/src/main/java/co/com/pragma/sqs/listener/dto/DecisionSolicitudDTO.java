package co.com.pragma.sqs.listener.dto;

import lombok.Data;

@Data
public class DecisionSolicitudDTO {
    private Long solicitudId;
    private String nuevoEstado;
}
