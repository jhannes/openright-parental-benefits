package io.openright.parental.server;

import io.openright.infrastructure.server.Controller;
import io.openright.infrastructure.server.JsonResourceController;
import io.openright.parental.domain.application.ApplicationResource;

public class ParentalBenefitsFrontController extends io.openright.infrastructure.server.ApiFrontController {

    @Override
    protected Controller getControllerForPath(String prefix) {
        switch (prefix) {
            case "applications": return new JsonResourceController(new ApplicationResource());
            default: return null;
        }
    }

}
