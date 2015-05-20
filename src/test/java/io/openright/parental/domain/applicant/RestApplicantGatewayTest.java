package io.openright.parental.domain.applicant;

import io.openright.infrastructure.rest.JsonResourceServlet;
import io.openright.parental.server.ParentalBenefitsTestConfig;
import io.openright.parental.server.test.DummyApplicantService;
import io.openright.parental.server.test.SampleData;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RestApplicantGatewayTest {

    @Test
    public void shouldRetrieveApplicant() throws Exception {
        DummyApplicantService service = new DummyApplicantService();

        Server server = new Server(0);
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.addServlet(new ServletHolder(new JsonResourceServlet(service)), "/applicant/*");
        server.setHandler(contextHandler);
        server.start();

        System.setProperty("parental.endpoint.applicant", server.getURI() + "/applicant/");
        ParentalBenefitsTestConfig config = new ParentalBenefitsTestConfig();
        RestApplicantGateway gateway = new RestApplicantGateway(config);

        Applicant applicant1 = SampleData.sampleApplicant();
        Applicant applicant2 = SampleData.sampleApplicant();
        service.insert(applicant1);
        service.insert(applicant2);

        assertThat(gateway.retrieve(applicant1.getId()).get()).isEqualTo(applicant1);
        assertThat(gateway.retrieve(applicant2.getId()).get()).isNotEqualTo(applicant1);
        assertThat(gateway.retrieve(SampleData.samplePersonId())).isEmpty();
    }

}