package io.openright.infrastructure.rest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonResourceServlet extends HttpServlet {

    private final JsonResourceController resourceController;

    public JsonResourceServlet(ResourceApi resourceApi) {
        this.resourceController = new JsonResourceController(resourceApi) {
            protected String getResourceId(HttpServletRequest req) {
                String[] parts = req.getPathInfo().split("/");
                return parts.length > 1 ? parts[1] : null;
            }
        };
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resourceController.handle(req, resp);
        } catch (RequestException e) {
            resp.sendError(e.getStatusCode(), e.getMessage());
        }
    }

}
