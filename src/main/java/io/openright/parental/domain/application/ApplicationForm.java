package io.openright.parental.domain.application;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.time.Instant;

public class ApplicationForm {
    @Getter
    private final String userId;

    @Getter
    private final Instant createdAt = Instant.now();

    @Getter @Setter
    private String status;

    @Getter
    private JSONObject form;

    @Getter @Setter
    private boolean newRow;

    public ApplicationForm(String userId, String status, JSONObject form, boolean newRow) {
        this.userId = userId;
        this.status = status;
        this.form = form;
        this.newRow = newRow;
    }
}
