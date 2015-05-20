package io.openright.parental.domain.application;

import io.openright.infrastructure.rest.RequestException;
import io.openright.infrastructure.rest.ResourceApi;
import io.openright.parental.domain.applicant.Applicant;
import io.openright.parental.domain.applicant.ApplicantGateway;
import io.openright.parental.domain.users.ApplicationUser;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationResource implements ResourceApi {

    private final ApplicationRepository applicationRepository;
    private final ApplicantGateway applicantGateway;

    public ApplicationResource(ApplicationRepository applicationRepository, ApplicantGateway applicantGateway) {
        this.applicationRepository = applicationRepository;
        this.applicantGateway = applicantGateway;
    }

    @Override
    public JSONObject listResources() {
        List<JSONObject> applications = applicationRepository.list()
                .stream().map(Application::toJSON).collect(Collectors.toList());
        return new JSONObject().put("applications", applications);
    }

    @Override
    public JSONObject getResource(String id) {
        Application application = applicationRepository.retrieve(Long.valueOf(id))
                .orElseThrow(RequestException.notFound(Application.class, id));
        return application.toJSON()
                .put("applicant", applicantGateway.retrieve(application.getApplicantId()).get().toJSON());
    }

    @Override
    public String createResource(JSONObject jsonObject) {
        Application application = new Application(getApplicant(),
                jsonObject.getString("applicationType"));
        applicationRepository.insert(application);
        return application.getId().toString();
    }

    @Override
    public void updateResource(String id, JSONObject jsonObject) {
        JSONObject form = jsonObject.optJSONObject("application");
        Application application = applicationRepository.retrieve(Long.valueOf(id))
                .orElseThrow(RequestException.notFound(Application.class, id));
        String status = jsonObject.has("status") ? jsonObject.getString("status") : "draft";
        application.addRevision(status, form);
        applicationRepository.update(Long.valueOf(id), application);
    }

    private Applicant getApplicant() {
        return this.applicantGateway.retrieve(ApplicationUser.getCurrent().getPersonId())
                .orElseThrow(() -> new RuntimeException("Invalid user"));
    }
}
