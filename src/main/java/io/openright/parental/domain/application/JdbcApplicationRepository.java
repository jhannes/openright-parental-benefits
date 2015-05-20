package io.openright.parental.domain.application;

import io.openright.infrastructure.db.Database;
import io.openright.lib.db.JdbcTable;
import io.openright.parental.domain.users.ApplicationUser;
import io.openright.parental.domain.users.ApplicationUserRole;
import io.openright.parental.server.ParentalBenefitsConfig;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class JdbcApplicationRepository implements ApplicationRepository {
    private final JdbcTable table;
    private final JdbcTable revisionTable;
    private final Database database;

    public JdbcApplicationRepository(ParentalBenefitsConfig config) {
        this.database = config.getDatabase();
        this.table = new JdbcTable(database, "Parental_Applications");
        this.revisionTable = new JdbcTable(database, "Parental_Application_Revisions");
    }

    @Override
    public void insert(Application application) {
        database.transactional(() -> {
            long id = table.insertValues(row -> {
                row.put("applicant_id", application.getApplicantId());
                row.put("application_type", application.getApplicationType());
                row.put("status", application.getStatus());
                row.put("office", application.getOffice());
                row.put("created_at", application.getCreatedAt());
                row.put("updated_at", application.getUpdatedAt());
            });
            application.setId(id);
            insertNewApplicationRevisions(application);
        });
    }

    private void insertNewApplicationRevisions(Application application) {
        for (ApplicationForm applicationForm : application.getApplicationHistory()) {
            if (!applicationForm.isNewRow()) continue;
            revisionTable.insertValues(row -> {
                row.put("application_id", application.getId());
                row.put("created_at", applicationForm.getCreatedAt());
                row.put("user_id", applicationForm.getUserId());
                row.put("form", applicationForm.getForm());
                row.put("status", applicationForm.getStatus());
            });
            applicationForm.setNewRow(false);
        }
    }

    @Override
    public List<Application> list() {
        return getTable().orderBy("updated_at desc").list(this::toApplication);
    }

    @Override
    public Optional<Application> retrieve(Long id) {
        return getTable().where("id", id).single((row) -> {
            Application application = toApplication(row);
            application.setApplicationHistory(listApplicationHistory(application.getId()));
            return application;
        });
    }

    private List<ApplicationForm> listApplicationHistory(Long id) {
        return revisionTable.where("application_id", id).orderBy("created_at")
                .list(row -> new ApplicationForm(
                        row.getString("user_id"),
                        row.getString("status"),
                        row.getJSON("form"),
                        false));
    }

    private JdbcTable getTable() {
        if (ApplicationUser.getCurrent().getUserRole() == ApplicationUserRole.CASE_WORKER) {
            return table.whereCondition("status <> ?", "draft");
        } else {
            return table.where("applicant_id", ApplicationUser.getCurrent().getPersonId());
        }
    }

    @Override
    public void update(Long id, Application application) {
        database.transactional(() -> {
            application.setUpdatedAt(Instant.now());
            getTable().where("id", id).updateValues(row -> {
                row.put("updated_at", application.getUpdatedAt());
                row.put("status", application.getStatus());
            });
            insertNewApplicationRevisions(application);
        });
    }

    private Application toApplication(Database.Row row) throws SQLException {
        Application application = new Application(
                row.getString("applicant_id"),
                row.getString("application_type"),
                row.getString("office"),
                row.getInstant("created_at"),
                row.getInstant("updated_at"));
        application.setId(row.getLong("id"));
        application.setStatus(row.getString("status"));
        return application;
    }

}
