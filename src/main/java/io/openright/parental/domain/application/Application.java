package io.openright.parental.domain.application;

import io.openright.parental.domain.users.ApplicationUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
public class Application {
    @Getter @Setter
    private Long id;

    @Getter
    private final Instant createdAt;

    @Getter @Setter
    private Instant updatedAt;

    @Getter
    private final String applicantId;

    @Getter @Setter
    private String status = "draft";

    @Getter @Setter
    private List<ApplicationForm> applicationHistory = new ArrayList<>();

    Application(String applicantId, Instant createdAt, Instant updatedAt) {
        this.applicantId = applicantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Application(String applicantId) {
        this(applicantId, Instant.now());
    }

    private Application(String applicantId, Instant createdAt) {
        this(applicantId, createdAt, createdAt);
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("id", id)
                .put("applicant", applicantId)
                .put("createdAt", createdAt)
                .put("updatedAt", updatedAt)
                .put("application", getApplicationForm());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(applicantId, that.applicantId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addRevision(String status, JSONObject applicationForm) {
        setStatus(status);
        applicationHistory.add(new ApplicationForm(ApplicationUser.getCurrent().getPersonId(), status, applicationForm, true));
    }

    public List<ApplicationForm> getApplicationHistory() {
        return applicationHistory;
    }

    public JSONObject getApplicationForm() {
        if (applicationHistory == null || applicationHistory.isEmpty()) {
            return new JSONObject();
        }
        return applicationHistory.get(applicationHistory.size()-1).getForm();
    }
}
