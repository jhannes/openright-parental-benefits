package io.openright.parental.server;

import io.openright.infrastructure.db.Database;
import io.openright.infrastructure.server.AppConfigFile;
import io.openright.infrastructure.util.ExceptionUtil;
import lombok.SneakyThrows;
import org.eclipse.jetty.plus.jndi.EnvEntry;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.net.URI;

public class ParentalBenefitsConfigFile extends AppConfigFile implements ParentalBenefitsConfig {
    private Database database;

    public ParentalBenefitsConfigFile(File file) {
        super(file);
        try {
            new EnvEntry("jdbc/parentalBenefitsDb", createDataSource());
        } catch (NamingException e) {
            throw ExceptionUtil.soften(e);
        }
    }

    protected DataSource createDataSource() {
        if (System.getenv("DATABASE_URL") != null) {
            return migrateDataSource("parental", createDataSourceFromEnv(System.getenv("DATABASE_URL")));
        }
        return createDataSource("parental");
    }

    @Override
    public Database getDatabase() {
        if (database == null) {
            return new Database("jdbc/parentalBenefitsDb");
        }
        return database;
    }

    @Override
    public int getHttpPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return Integer.parseInt(getProperty("parental.http.port", "8080"));
    }

    @Override
    @SneakyThrows
    public URI getApplicantEndpointUrl() {
        return new URI(getRequiredProperty("parental.endpoint.applicant"));
    }
}
