package com.lapalma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.lapalma.config.PostgresTestConfig;
import com.lapalma.management.ManagementApplication;




@SpringBootTest(classes = ManagementApplication.class) 
@ActiveProfiles ("test")
@Import(PostgresTestConfig.class) 
class LaPalmaApplicationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
        // La prueba pasa si Spring inicia correctamente.
    }

    @Test
    void shouldCreateDatabaseSchema() {

        Boolean schemaExists = jdbcTemplate.queryForObject(
            """
            SELECT EXISTS (
                SELECT 1
                FROM information_schema.schemata
                WHERE schema_name = 'lapalma'
            )
            """,
            Boolean.class
        );

        assertTrue(schemaExists);
    }

    @Test
    void shouldExecuteInitialFlywayMigration() {

        Integer migrationCount = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM public.flyway_schema_history
            WHERE version = '1'
              AND success = true
            """,
            Integer.class
        );

        assertEquals(1, migrationCount);
    }
}
