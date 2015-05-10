package io.openright.infrastructure.server;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonResourceController implements Controller {
    private ResourceApi resourceApi;

    public JsonResourceController(ResourceApi applicationApi) {
        this.resourceApi = applicationApi;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req.getMethod().equals("POST")) {
            createResource(req, resp);
        } else {
            resp.sendError(400);
        }
    }

    private void createResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            String id = resourceApi.createResource(jsonObject);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setHeader("Location", req.getRequestURL() + "/" + id);
        }
    }
}