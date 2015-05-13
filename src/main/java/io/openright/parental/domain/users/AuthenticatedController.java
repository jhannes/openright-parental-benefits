package io.openright.parental.domain.users;

import io.openright.infrastructure.rest.Controller;
import io.openright.infrastructure.rest.RequestException;

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

    private ApplicationUser getUser(HttpServletRequest req) {
        String personId = (String) req.getSession().getAttribute("personId");
        if (personId == null) {
            throw new RequestException(401, "Unauthorized");
        }
        return new ApplicationUser(personId, null);
    }
}
