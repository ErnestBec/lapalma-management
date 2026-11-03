package com.lapalma.management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.flywaydb.core.Flyway;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lapalma.management.config.PostgresTestConfig;

@SpringBootTest(classes = ManagementApplication.class)
@ActiveProfiles("test")
@Import(PostgresTestConfig.class)
class LaPalmaApplicationTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
        // La prueba pasa si Spring inicia correctamente.
    }

    @Test
    void shouldNotExecuteMigrationsTwice() {

        var result = flyway.migrate();

        assertEquals(0, result.migrationsExecuted);
    }

    @Test
    void shouldCreate21BusinessIndexes() {

        Integer total = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM pg_indexes
                        WHERE schemaname = 'lapalma'
                          AND indexname LIKE 'ix_%'
                        """,
                Integer.class);

        assertEquals(21, total);
    }

    @Test
    void shouldExecuteAllFlywayMigrations() {

        Integer total = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM public.flyway_schema_history
                        WHERE version IN ('1', '2', '3')
                          AND success = true
                        """,
                Integer.class);

        assertEquals(3, total);
    }

    @Test
    void shouldCreate39BusinessTables() {
        Integer total = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'lapalma'
                    AND table_type = 'BASE TABLE'
                """, Integer.class);

        assertEquals(39, total);
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
                Boolean.class);

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
                Integer.class);

        assertEquals(1, migrationCount);
    }
}
