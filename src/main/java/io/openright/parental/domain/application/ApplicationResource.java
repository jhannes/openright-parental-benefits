package io.openright.parental.domain.application;

import io.openright.infrastructure.server.ResourceApi;
import org.json.JSONObject;

public class ApplicationResource implements ResourceApi {

    private static JSONObject application;

    @Override
    public String createResource(JSONObject jsonObject) {
        application = jsonObject;
        return null;
        //throw new RuntimeException();
    }

    @Override
    public JSONObject getJSON(String id) {
        return application;
    }
}
