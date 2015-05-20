package io.openright.parental.server.test;

import io.openright.infrastructure.rest.RequestException;
import io.openright.infrastructure.rest.ResourceApi;
import io.openright.parental.domain.applicant.Applicant;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DummyApplicantService implements ResourceApi {
    private final Map<String, Applicant> applicants = new HashMap<>();

    public void insert(Applicant applicant) {
        this.applicants.put(applicant.getId(), applicant);
    }

    @Override
    public String createResource(JSONObject jsonObject) {
        throw new RequestException(400, "Not supported");
    }

    @Override
    public JSONObject getResource(String id) {
        Applicant applicant = applicants.get(id);
        if (applicant == null) {
            throw RequestException.notFound(Applicant.class, id).get();
        }
        return new JSONObject()
                .put("id", applicant.getId())
                .put("name", applicant.getName())
                .put("streetAddress", applicant.getStreetAddress())
                .put("postalCode", applicant.getPostalCode())
                .put("postalArea", applicant.getPostalArea())
                .put("contactPhone", applicant.getContactPhone())
                .put("email", applicant.getEmail())
                .put("accountNumber", applicant.getAccountNumber())
                .put("navOffice", applicant.getOfficeId());
    }

    @Override
    public void updateResource(String id, JSONObject jsonObject) {
        throw new RequestException(400, "Not supported");
    }

    @Override
    public JSONObject listResources() {
        throw new RequestException(400, "Not supported");
    }
}
