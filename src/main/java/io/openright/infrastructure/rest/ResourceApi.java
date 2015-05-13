package io.openright.infrastructure.rest;

import org.json.JSONObject;

public interface ResourceApi {
    String createResource(JSONObject jsonObject);

    JSONObject getResource(String id);

    void updateResource(String id, JSONObject jsonObject);

    JSONObject listResources();
}
