package io.openright.parental.server;

import io.openright.infrastructure.server.Controller;
import io.openright.infrastructure.server.JsonResourceController;
import io.openright.parental.domain.application.ApplicationResource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ParentalBenefitsFrontController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getController(req.getPathInfo()).handle(req, resp);
    }

    private Controller getController(String pathInfo) {
        Controller defaultController = (req, res) -> super.service(req, res);
        Controller controller = getControllerForPath(pathInfo.split("/")[1]);
        return controller != null ? controller : defaultController;
    }

    private Controller getControllerForPath(String prefix) {
        switch (prefix) {
            case "application": return new JsonResourceController(new ApplicationResource());
        }
        return null;
    }

}
