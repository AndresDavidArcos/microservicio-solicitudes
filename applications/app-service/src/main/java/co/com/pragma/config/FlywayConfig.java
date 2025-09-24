package co.com.pragma.config;

import co.com.pragma.secretsprovider.SecretsProvider;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(SecretsProvider secrets) {

        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
                secrets.getDbHost(),
                secrets.getDbPort(),
                secrets.getDbSolciitudesName()
        );

        return Flyway.configure()
                .dataSource(
                        jdbcUrl,
                        secrets.getDbUser(),
                        secrets.getDbPassword()
                )
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
    }
}
