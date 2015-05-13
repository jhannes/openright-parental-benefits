package io.openright.parental.domain.application;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONObject;

import java.time.Instant;
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

    @Getter
    private final JSONObject applicationForm;

    public Application(String applicantId, Instant createdAt, Instant updatedAt, JSONObject applicationForm) {
        this.applicantId = applicantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.applicationForm = applicationForm;
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("id", id)
                .put("applicant", applicantId)
                .put("createdAt", createdAt)
                .put("updatedAt", updatedAt)
                .put("application", applicationForm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(applicationForm.toString(), that.applicationForm.toString()) &&
                Objects.equals(applicantId, that.applicantId) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
