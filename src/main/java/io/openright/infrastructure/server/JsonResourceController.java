package io.openright.infrastructure.server;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class JsonResourceController implements Controller {
    private ResourceApi resourceApi;

    public JsonResourceController(ResourceApi applicationApi) {
        this.resourceApi = applicationApi;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req.getMethod().equals("POST")) {
            createResource(req, resp);
        } else if (req.getMethod().equals("PUT")) {
            updateResource(req, resp);
        } else if (req.getMethod().equals("GET")) {
            getResource(req, resp);
        } else {
            resp.sendError(400);
        }
    }

    private void getResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = req.getPathInfo().split("/");
        if (parts.length > 2) {
            sendResponse(resp, resourceApi.getJSON(parts[2]));
        } else {
            sendResponse(resp, resourceApi.listJSON());
        }
    }

    private void updateResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = req.getPathInfo().split("/");
        try (BufferedReader reader = req.getReader()) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            resourceApi.updateResource(parts[2], jsonObject);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private void sendResponse(HttpServletResponse resp, JSONObject response) throws IOException {
        resp.setHeader("Expires", "-1");
        if (response == null) {
            resp.setStatus(204);
            return;
        }
        resp.setContentType("application/json");
        try (Writer writer = resp.getWriter()) {
            writer.write(response.toString());
        }
    }

    private void createResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            String id = resourceApi.createResource(jsonObject);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setHeader("Location", req.getRequestURL() + id);
        }
    }
}
