package co.com.pragma.sqs.sender;

import co.com.pragma.model.solicitud.ValidacionPayload;
import co.com.pragma.model.solicitud.gateways.ValidacionAutomaticaGateway;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SQSValidacionAdapter implements ValidacionAutomaticaGateway {

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper mapper;

    @Override
    @SneakyThrows
    public Mono<Void> encolarParaValidacion(ValidacionPayload payload) {
        String mensajeJson = mapper.writeValueAsString(payload);

        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(properties.queueUrlValidacion())
                .messageBody(mensajeJson)
                .build();

        return Mono.fromFuture(client.sendMessage(request))
                .doOnSuccess(response -> log.info("Solicitud {} encolada para validación automática.", payload.getSolicitudActual().getId()))
                .doOnError(err -> log.error("Error al encolar para validación.", err))
                .then();
    }
}
