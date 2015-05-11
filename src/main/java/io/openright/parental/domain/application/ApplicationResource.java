package io.openright.parental.domain.application;

import io.openright.infrastructure.rest.RequestException;
import io.openright.infrastructure.server.ResourceApi;
import org.json.JSONObject;

public class ApplicationResource implements ResourceApi {

    private ApplicationRepository applicationRepository;

    public ApplicationResource(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public String createResource(JSONObject jsonObject) {
        Application application = toApplication(jsonObject);
        applicationRepository.insert(application);
        return application.getId().toString();
    }

    private Application toApplication(JSONObject jsonObject) {
        JSONObject form = jsonObject.optJSONObject("application");
        if (form == null) {
            form = new JSONObject();
        }
        return new Application(form);
    }

    @Override
    public JSONObject getJSON(String id) {
        return applicationRepository.retrieve(Long.valueOf(id))
                .orElseThrow(RequestException.notFound(Application.class, id))
                .toJSON();
    }

    @Override
    public void updateResource(String id, JSONObject jsonObject) {
        applicationRepository.update(Long.valueOf(id), toApplication(jsonObject));
    }
}
