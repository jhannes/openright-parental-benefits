package io.openright.infrastructure.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class ApiFrontController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getController(req).handle(req, resp);
    }

    private Controller getController(HttpServletRequest req) {
        Controller defaultController = (request, res) -> super.service(request, res);
        Controller controller = getControllerForPath(req.getPathInfo().split("/")[1]);
        return controller != null ? controller : defaultController;
    }

    protected abstract Controller getControllerForPath(String prefix);
}
