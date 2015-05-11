package io.openright.parental.server;

import io.openright.infrastructure.db.Database;
import io.openright.infrastructure.server.AppConfigFile;
import io.openright.infrastructure.util.ExceptionUtil;
import org.eclipse.jetty.plus.jndi.EnvEntry;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;

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
        return createDataSource("parental");
    }

    @Override
    public Database getDatabase() {
        if (database == null) {
            return new Database("jdbc/parentalBenefitsDb");
        }
        return database;
    }
}
