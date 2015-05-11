package io.openright.infrastructure.server;

import org.json.JSONObject;

public interface ResourceApi {
    String createResource(JSONObject jsonObject);

    JSONObject getJSON(String id);

    void updateResource(String id, JSONObject jsonObject);
}
