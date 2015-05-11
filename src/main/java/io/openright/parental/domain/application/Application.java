package io.openright.parental.domain.application;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONObject;

import java.util.Objects;

@ToString
public class Application {
    @Getter
    private final JSONObject applicationForm;

    @Getter @Setter
    private Long id;

    public Application(JSONObject applicationForm) {
        this.applicationForm = applicationForm;
    }

    public JSONObject toJSON() {
        return new JSONObject().put("application", applicationForm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(applicationForm.toString(), that.applicationForm.toString()) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
