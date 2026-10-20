package com.lapalma.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

//define configuración exclusiva para pruebas
@TestConfiguration(proxyBeanMethods = false)
public class PostgresTestConfig {
    //Registra el contenedor como componente gestionado por Spring
    @Bean 
    // Permite que Spring utilice automáticamente los datos de conexión del contenedor
    @ServiceConnection 
    //Representa una instancia temporal de PostgreSQL
    PostgreSQLContainer postgresContainer(){
        return new PostgreSQLContainer("postgres:17");
    }
    
}
