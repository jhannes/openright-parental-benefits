package io.openright.infrastructure.server;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppConfigFileTest {

    @Test
    public void testParseDataSource() throws Exception {
        String databaseUrl = "postgres://user3123:passkja83kd8@ec2-117-21-174-214.compute-1.amazonaws.com:6212/db982398";
        HikariDataSource dataSource = (HikariDataSource) AppConfigFile.parseDataSource(databaseUrl);
        assertThat(dataSource.getJdbcUrl()).isEqualTo("jdbc:postgresql://ec2-117-21-174-214.compute-1.amazonaws.com:6212/db982398");
        assertThat(dataSource.getUsername()).isEqualTo("user3123");
        assertThat(dataSource.getPassword()).isEqualTo("passkja83kd8");
    }

    @Test
    public void testLocalDataSource() throws Exception {
        AppConfigFile.parseDataSource("postgres://parental_test:parental_test@localhost:5432/parental_test")
            .getConnection().close();
    }
}