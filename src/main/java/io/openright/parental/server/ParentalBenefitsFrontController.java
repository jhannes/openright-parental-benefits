package io.openright.parental.server;

import io.openright.infrastructure.rest.ApiFrontController;
import io.openright.infrastructure.rest.Controller;
import io.openright.infrastructure.rest.JsonResourceController;
import io.openright.parental.domain.applicant.ApplicantGateway;
import io.openright.parental.domain.applicant.RestApplicantGateway;
import io.openright.parental.domain.application.ApplicationRepository;
import io.openright.parental.domain.application.ApplicationResource;
import io.openright.parental.domain.application.JdbcApplicationRepository;
import io.openright.parental.domain.users.AuthenticatedController;
import io.openright.parental.domain.users.LoginController;
import lombok.SneakyThrows;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;

public class ParentalBenefitsFrontController extends ApiFrontController {

    private ParentalBenefitsConfig config;
    private ApplicationRepository applicationRepository;
    private ApplicantGateway applicantGateway;

    @Override
    @SneakyThrows(NamingException.class)
    public void init() throws ServletException {
        this.config = (ParentalBenefitsConfig)new InitialContext().lookup("parental/config");
        this.applicationRepository = new JdbcApplicationRepository(config);
        this.applicantGateway = new RestApplicantGateway(config);
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
                new JsonResourceController(new ApplicationResource(applicationRepository, applicantGateway)));
    }

}
