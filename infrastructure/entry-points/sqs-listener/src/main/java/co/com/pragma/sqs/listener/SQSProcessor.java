package co.com.pragma.sqs.listener;

import co.com.pragma.sqs.listener.dto.DecisionSolicitudDTO;
import co.com.pragma.usecase.solicitud.ProcesarDecisionUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final ProcesarDecisionUseCase procesarDecisionUseCase;
    private final ObjectMapper mapper;
    @Override
    public Mono<Void> apply(Message message) {
        try {
            log.info("Procesando mensaje de decisión: {}", message.body());
            DecisionSolicitudDTO decision = mapper.readValue(message.body(), DecisionSolicitudDTO.class);

            return procesarDecisionUseCase.ejecutar(decision.getSolicitudId(), decision.getNuevoEstado())
                    .doOnSuccess(solicitud -> log.info("Solicitud {} actualizada a estado {}", solicitud.getId(), solicitud.getEstado()))
                    .then();

        } catch (Exception e) {
            log.error("Error procesando mensaje de decisión: {}", e.getMessage());
            return Mono.empty();
        }
    }
}
