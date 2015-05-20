package io.openright.parental.domain.users;

import io.openright.infrastructure.rest.Controller;
import io.openright.infrastructure.rest.RequestException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticatedController implements Controller {
    private Controller controller;

    public AuthenticatedController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        ApplicationUser.setCurrent(getUser(req));
        try {
            controller.handle(req, resp);
        } finally {
            ApplicationUser.setCurrent(null);
        }
    }

    private static ApplicationUser getUser(HttpServletRequest req) {
        ApplicationUser applicationUser = (ApplicationUser) req.getSession().getAttribute("user");
        if (applicationUser == null) {
            throw new RequestException(401, "Unauthorized");
        }
        return applicationUser;
    }

    public static void setUser(HttpServletRequest req, JSONObject jsonObject) {
        req.getSession(true).setAttribute("user", toUser(jsonObject));
    }

    private static ApplicationUser toUser(JSONObject jsonObject) {
        if (jsonObject.has("caseWorker")) {
            return new ApplicationUser(jsonObject.getString("caseWorker"),
                    jsonObject.getString("office"),
                    ApplicationUserRole.CASE_WORKER);
        } else {
            return new ApplicationUser(jsonObject.getString("personId"), null, null);
        }
    }
}
