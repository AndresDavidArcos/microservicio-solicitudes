package co.com.pragma.sqs.sender;

import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.gateways.AprobacionGateway;
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
public class SQSAprobacionAdapter implements AprobacionGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper mapper;

    @Override
    @SneakyThrows
    public Mono<Void> notificarAprobacion(Solicitud solicitud) {
        String mensajeJson = mapper.writeValueAsString(solicitud);
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(properties.queueUrlAprobaciones())
                .messageBody(mensajeJson)
                .build();
        return Mono.fromFuture(client.sendMessage(request)).then();
    }
}
