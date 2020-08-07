package in.org.projecteka.clientregistry;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;


@Component
@ConfigurationProperties(prefix = "spring.datasource")
@Getter
@Setter
class LiquibaseDataSourceProperties {
    private String url;
    private String password;
    private String driverClassName;
    private String username;
}

@Configuration
public class LiquibaseDataSourceConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(LiquibaseDataSourceConfiguration.class);

    @Autowired
    private LiquibaseDataSourceProperties liquibaseDataSourceProperties;

    @LiquibaseDataSource
    @Bean
    public DataSource liquibaseDataSource() {
        DataSource ds = DataSourceBuilder.create()
                .username(liquibaseDataSourceProperties.getUsername())
                .password(liquibaseDataSourceProperties.getPassword())
                .url(liquibaseDataSourceProperties.getUrl())
                .driverClassName(liquibaseDataSourceProperties.getDriverClassName())
                .build();
        if (ds instanceof HikariDataSource) {
            ((HikariDataSource) ds).setMaximumPoolSize(1);
            ((HikariDataSource) ds).setPoolName("Liquibase Pool");
        }

        LOG.info("Initialized a datasource for {}", liquibaseDataSourceProperties.getUrl());
        return ds;
    }

}