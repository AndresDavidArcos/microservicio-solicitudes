package co.com.pragma.secretsprovider;
import co.com.bancolombia.secretsmanager.api.GenericManagerAsync;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class SecretsProvider {

    private final GenericManagerAsync secretsManager;
    private final Gson gson = new Gson();

    @Value("${aws.secretName}")
    private String secretName;

    private String dbHost;
    private Integer dbPort;
    private String dbSolciitudesName;
    private String dbUser;
    private String dbPassword;
    private String jwtSecret;
    private String dbSchema;

    @SneakyThrows
    @PostConstruct
    public void loadSecrets() {
        String secretValueJson = secretsManager.getSecret(secretName).block();
        log.info("Cargando secretos desde AWS Secrets Manager...");
        JsonObject secretJson = gson.fromJson(secretValueJson, JsonObject.class);

        this.dbHost = secretJson.get("DB_HOST").getAsString();
        this.dbPort = secretJson.get("DB_PORT").getAsInt();
        this.dbSolciitudesName = secretJson.get("DB_SOLICITUDES_NAME").getAsString();
        this.dbSchema = secretJson.get("DB_SCHEMA").getAsString();
        this.dbUser = secretJson.get("DB_USER").getAsString();
        this.dbPassword = secretJson.get("DB_PASSWORD").getAsString();
        this.jwtSecret = secretJson.get("JWT_SECRET").getAsString();
        log.info("Secretos cargados exitosamente.");
    }
}
