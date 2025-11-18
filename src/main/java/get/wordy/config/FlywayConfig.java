package get.wordy.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flywayCore(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .schemas("public") // schema for core logic
                .locations("classpath:db/migration/core")
                .table("schema_history")
                .baselineOnMigrate(true)
                .baselineVersion("0.0.1")
                .load();
    }

    @Bean(initMethod = "migrate")
    public Flyway flywayUsers(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .schemas("public") // same schema for users
                .locations("classpath:db/migration/users")
                .table("users_schema_history") // independent history
                .baselineOnMigrate(true)
                .baselineVersion("0.0.1")
                .load();
    }

}
