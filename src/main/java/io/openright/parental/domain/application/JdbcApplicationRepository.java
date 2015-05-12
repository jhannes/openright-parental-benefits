package io.openright.parental.domain.application;

import io.openright.infrastructure.db.Database;
import io.openright.lib.db.JdbcTable;
import io.openright.parental.domain.users.ApplicationUser;
import io.openright.parental.domain.users.ApplicationUserRole;
import io.openright.parental.server.ParentalBenefitsConfig;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcApplicationRepository implements ApplicationRepository {
    private final JdbcTable table;

    public JdbcApplicationRepository(ParentalBenefitsConfig config) {
        this.table = new JdbcTable(config.getDatabase(), "Parental_Applications");
    }

    @Override
    public void insert(Application application) {
        application.setId(table.insertValues(row -> {
            row.put("applicant_id", application.getApplicantId());
            row.put("created_at", application.getCreatedAt());
            row.put("updated_at", application.getUpdatedAt());
            row.put("form", application.getApplicationForm());
        }));
    }

    @Override
    public List<Application> list() {
        return getTable().orderBy("updated_at desc").list(this::toApplication);
    }

    @Override
    public Optional<Application> retrieve(Long id) {
        return getTable().where("id", id).single(this::toApplication);
    }

    private JdbcTable getTable() {
        if (ApplicationUser.getCurrent().getUserRole() != ApplicationUserRole.CASE_WORKER) {
            return table.where("applicant_id", ApplicationUser.getCurrent().getPersonId());
        }
        return table;
    }

    @Override
    public void update(Long id, Application application) {
        getTable().where("id", id).updateValues(row -> row.put("form", application.getApplicationForm()));
    }

    private Application toApplication(Database.Row row) throws SQLException {
        Application application = new Application(
                row.getString("applicant_id"),
                row.getInstant("created_at"),
                row.getInstant("updated_at"),
                row.getJSON("form"));
        application.setId(row.getLong("id"));
        return application;
    }

}
