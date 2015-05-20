package io.openright.parental.domain.application;

import io.openright.parental.domain.applicant.Applicant;
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
    private final String applicantId;

    @Getter
    private final String applicationType;

    @Getter
    private String office;

    @Getter @Setter
    private String status = "draft";

    @Getter
    private final Instant createdAt;

    @Getter @Setter
    private Instant updatedAt;

    @Getter @Setter
    private List<ApplicationForm> applicationHistory = new ArrayList<>();

    Application(String applicantId, String applicationType, String office, Instant createdAt, Instant updatedAt) {
        this.applicantId = applicantId;
        this.applicationType = applicationType;
        this.office = office;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Application(Applicant applicant, String applicationType) {
        this(applicant, applicationType, Instant.now());
    }

    public Application(Applicant applicant, String applicationType, Instant createdAt) {
        this(applicant.getId(), applicationType, applicant.getOfficeId(), createdAt, createdAt);
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("id", id)
                .put("applicant", applicantId)
                .put("createdAt", createdAt)
                .put("updatedAt", updatedAt)
                .put("status", status)
                .put("applicationType", applicationType)
                .put("office", office)
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

    public JSONObject getApplicationForm() {
        if (applicationHistory == null || applicationHistory.isEmpty()) {
            return new JSONObject();
        }
        return applicationHistory.get(applicationHistory.size()-1).getForm();
    }
}
