package io.openright.parental.domain.application;

import io.openright.infrastructure.rest.RequestException;
import io.openright.infrastructure.rest.ResourceApi;
import io.openright.parental.domain.users.ApplicationUser;
import org.json.JSONObject;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationResource implements ResourceApi {

    private ApplicationRepository applicationRepository;

    public ApplicationResource(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public JSONObject listResources() {
        List<JSONObject> applications = applicationRepository.list()
                .stream().map(Application::toJSON).collect(Collectors.toList());
        return new JSONObject().put("applications", applications);
    }

    @Override
    public JSONObject getResource(String id) {
        return applicationRepository.retrieve(Long.valueOf(id))
                .orElseThrow(RequestException.notFound(Application.class, id))
                .toJSON();
    }

    @Override
    public String createResource(JSONObject jsonObject) {
        Application application = toApplication(jsonObject);
        applicationRepository.insert(application);
        return application.getId().toString();
    }

    @Override
    public void updateResource(String id, JSONObject jsonObject) {
        applicationRepository.update(Long.valueOf(id), toApplication(jsonObject));
    }

    private Application toApplication(JSONObject jsonObject) {
        JSONObject form = jsonObject.optJSONObject("application");
        if (form == null) {
            form = new JSONObject();
        }
        Instant now = Instant.now();
        return new Application(ApplicationUser.getCurrent().getPersonId(), now, now, form);
    }
}
