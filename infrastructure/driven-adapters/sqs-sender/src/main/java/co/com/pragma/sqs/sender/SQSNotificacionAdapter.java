package co.com.pragma.sqs.sender;

import co.com.pragma.model.solicitud.Notificacion;
import co.com.pragma.model.solicitud.gateways.NotificacionGateway;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SQSNotificacionAdapter implements NotificacionGateway {

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final Gson gson = new Gson();

    @Override
    public Mono<Void> enviarNotificacion(Notificacion notificacion) {
        String mensajeJson = gson.toJson(notificacion);

        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(mensajeJson)
                .build();

        return Mono.fromFuture(client.sendMessage(request))
                .doOnSuccess(response -> log.info("Mensaje enviado a SQS con ID: {}", response.messageId()))
                .doOnError(err -> log.error("Error al enviar mensaje a SQS", err))
                .then();
    }
}
