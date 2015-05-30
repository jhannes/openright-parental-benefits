package io.openright.infrastructure.server;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppConfigFile {
    private static Logger log = LoggerFactory.getLogger(AppConfigFile.class);

    private long nextCheckTime = 0;
    private long lastLoadTime = 0;
    private Properties properties = new Properties();
    private final File configFile;

    public AppConfigFile(File configFile) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        this.configFile = configFile;
    }

    public Dictionary<Object, Object> getProperties() {
        ensureConfigurationIsFresh();
        return properties;
    }

    protected DataSource createDataSource(String prefix) {
        return migrateDataSource(prefix, createDataSource(prefix, prefix));
    }

    protected DataSource migrateDataSource(String prefix, DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("classpath:db/" + prefix);
        flyway.migrate();
        return dataSource;
    }

    protected DataSource createTestDataSource(String prefix) {
        DataSource dataSource = createDataSource(prefix, prefix + "_test");

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("classpath:db/" + prefix);
        if ("true".equals(getProperty(prefix + ".db.test.clean", "false"))) {
            flyway.clean();
        }
        flyway.migrate();

        return dataSource;
    }

    protected DataSource createDataSourceFromEnv(String databaseUrl) {
        return parseDataSource(databaseUrl);
    }

    static DataSource parseDataSource(String databaseUrl) {
        Pattern pattern = Pattern.compile("^([^:]+)://([^:]+):([^@]+)@(([-a-z0-9.]+):(\\d+)/(\\w+))$");
        Matcher matcher = pattern.matcher(databaseUrl);
        if (!matcher.matches()) {
            throw new RuntimeException("Unexpected database URL " + databaseUrl);
        }

        HikariDataSource dataSource = new HikariDataSource();
        if (matcher.group(1).equals("postgres")) {
            dataSource.setJdbcUrl("jdbc:postgresql://" + matcher.group(4));
        } else {
            throw new RuntimeException("Unexpected database type " + databaseUrl);
        }
        dataSource.setUsername(matcher.group(2));
        dataSource.setPassword(matcher.group(3));
        return dataSource;
    }

    private DataSource createDataSource(String prefix, String defaultName) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(getProperty(prefix + ".db.username", defaultName));
        dataSource.setPassword(getProperty(prefix + ".db.password", dataSource.getUsername()));
        dataSource.setJdbcUrl(
                getProperty(prefix + ".db.url", "jdbc:postgresql://localhost:5432/" + dataSource.getUsername()));
        return dataSource;
    }

    public String getProperty(String propertyName, String defaultValue) {
        String result = getProperty(propertyName);
        if (result == null) {
            log.trace("Missing property {} in {}", propertyName, properties.keySet());
            return defaultValue;
        }
        return result;
    }

    public String getRequiredProperty(String propertyName) {
        String result = getProperty(propertyName);
        if (result == null) {
            throw new RuntimeException("Missing property " + propertyName + " in " + configFile);
        }
        return result;
    }

    private String getProperty(String propertyName) {
        if (System.getProperty(propertyName) != null) {
            log.trace("Reading {} from system properties", propertyName);
            return System.getProperty(propertyName);
        }
        if (System.getenv(propertyName.replace('.', '_')) != null) {
            log.trace("Reading {} from environment", propertyName);
            return System.getenv(propertyName.replace('.', '_'));
        }

        ensureConfigurationIsFresh();
        return properties.getProperty(propertyName);
    }

    private synchronized void ensureConfigurationIsFresh() {
        if (System.currentTimeMillis() < nextCheckTime) return;
        nextCheckTime = System.currentTimeMillis() + 10000;
        log.trace("Rechecking {}", configFile);

        if (!configFile.exists()) {
            log.error("Missing configuration file {}", configFile);
        }

        if (lastLoadTime >= configFile.lastModified()) return;
        lastLoadTime = configFile.lastModified();
        log.debug("Reloading {}", configFile);

        try (FileInputStream inputStream = new FileInputStream(configFile)) {
            properties.clear();
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + configFile, e);
        }
    }
}
