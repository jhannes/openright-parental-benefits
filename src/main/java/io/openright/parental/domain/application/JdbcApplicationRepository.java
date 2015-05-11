package io.openright.parental.domain.application;

import io.openright.lib.db.JdbcTable;
import io.openright.parental.server.ParentalBenefitsConfig;
import org.json.JSONObject;

import java.util.Optional;

public class JdbcApplicationRepository implements ApplicationRepository {
    private final JdbcTable table;
    private Application application = new Application(new JSONObject().put("application", new JSONObject()));

    public JdbcApplicationRepository(ParentalBenefitsConfig config) {
        this.table = new JdbcTable(config.getDatabase(), "Parental_Applications");
    }

    @Override
    public void insert(Application application) {
        this.application = application;
        application.setId(table.insertValues((row) -> row.put("form", application.getApplicationForm())));
    }

    @Override
    public Optional<Application> retrieve(Long id) {
        return table.where("id", id).single(row -> {
            Application application = new Application(row.getJSON("form"));
            application.setId(row.getLong("id"));
            return application;
        });
    }

    @Override
    public void update(Long id, Application application) {
        table.where("id", id).updateValues(row -> row.put("form", application.getApplicationForm()));
    }
}
