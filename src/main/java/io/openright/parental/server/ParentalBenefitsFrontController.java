package io.openright.parental.server;

import io.openright.parental.domain.users.AuthenticatedController;
import io.openright.infrastructure.server.Controller;
import io.openright.infrastructure.server.JsonResourceController;
import io.openright.parental.domain.application.ApplicationRepository;
import io.openright.parental.domain.application.ApplicationResource;
import io.openright.parental.domain.application.JdbcApplicationRepository;
import io.openright.parental.domain.users.LoginController;
import lombok.SneakyThrows;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;

public class ParentalBenefitsFrontController extends io.openright.infrastructure.server.ApiFrontController {

    private ParentalBenefitsConfig config;
    private ApplicationRepository applicationRepository;

    @Override
    @SneakyThrows(NamingException.class)
    public void init() throws ServletException {
        this.config = (ParentalBenefitsConfig)new InitialContext().lookup("parental/config");
        this.applicationRepository = new JdbcApplicationRepository(config);
    }

    @Override
    protected Controller getControllerForPath(String prefix) {
        switch (prefix) {
            case "applications": return createApplicationController();
            case "login":
                return new LoginController();
            default: return null;
        }
    }

    private Controller createApplicationController() {
        return new AuthenticatedController(
                new JsonResourceController(new ApplicationResource(applicationRepository)));
    }

}
