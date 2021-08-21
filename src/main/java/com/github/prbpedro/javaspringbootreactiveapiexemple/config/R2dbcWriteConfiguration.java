package com.github.prbpedro.javaspringbootreactiveapiexemple.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@EnableR2dbcRepositories(
    entityOperationsRef = "writeEntityTemplate",
    basePackages = "com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write")
public class R2dbcWriteConfiguration {

    @Bean
    @Qualifier(value = "writeConnectionFactory")
    public ConnectionFactory writeConnectionFactory() {
        return ConnectionFactories.get(EnvironmentVariables.getWriteR2dbcMysqlUrl());
    }

    @Bean
    public R2dbcEntityOperations writeEntityTemplate(
        @Qualifier("writeConnectionFactory")
            ConnectionFactory connectionFactory
    ) {

        DefaultReactiveDataAccessStrategy strategy = new DefaultReactiveDataAccessStrategy(MySqlDialect.INSTANCE);
        DatabaseClient databaseClient = DatabaseClient.builder()
            .connectionFactory(connectionFactory)
            .bindMarkers(MySqlDialect.INSTANCE.getBindMarkersFactory())
            .build();

        return new R2dbcEntityTemplate(databaseClient, strategy);
    }
}
