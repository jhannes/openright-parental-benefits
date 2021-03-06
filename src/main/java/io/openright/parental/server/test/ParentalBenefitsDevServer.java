package io.openright.parental.server.test;

import io.openright.infrastructure.rest.JsonResourceServlet;
import io.openright.infrastructure.util.IOUtil;
import io.openright.infrastructure.util.LogUtil;
import io.openright.parental.server.ParentalBenefitsConfig;
import io.openright.parental.server.ParentalBenefitsConfigFile;
import io.openright.parental.server.ParentalBenefitsServer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.MovedContextHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ParentalBenefitsDevServer extends ParentalBenefitsServer {

    private final DummyApplicantService applicantService = new DummyApplicantService();

    public ParentalBenefitsDevServer(ParentalBenefitsConfig config) {
        super(config);
    }

    @Override
    protected HandlerList createHandlers() {
        HandlerList handlers = new HandlerList();
        handlers.addHandler(new ShutdownHandler("dsgsdglsdgsdgnk", false, true));
        handlers.addHandler(createWebApi());
        handlers.addHandler(createWebApp());
        handlers.addHandler(createDummyServices());
        handlers.addHandler(new MovedContextHandler(null, "/", "/parental"));
        return handlers;
    }

    private Handler createDummyServices() {
        ServletContextHandler contextHandler = new ServletContextHandler(null, "/dummy");
        contextHandler.addServlet(new ServletHolder(new JsonResourceServlet(applicantService)), "/applicant/*");
        return contextHandler;
    }

    @Override
    public void start() throws Exception {
        super.start();
        System.setProperty("parental.endpoint.applicant",
                getURI().resolve("/dummy/applicant/").toString());
    }

    public static void main(String[] args) throws Exception {
        LogUtil.setupLogging("logging-parental-benefits.xml");
        ParentalBenefitsConfig config = new ParentalBenefitsConfigFile(IOUtil.extractResourceFile("parental-benefits.properties"));

        ParentalBenefitsDevServer devServer = new ParentalBenefitsDevServer(config);
        devServer.loadSeedData();
        devServer.start();
    }

    private void loadSeedData() {
        applicantService.insert(SampleData.sampleApplicant("06015707439", "0314"));
        applicantService.insert(SampleData.sampleApplicant("23127946732", "0314"));
        applicantService.insert(SampleData.sampleApplicant("17048316526", "0316"));
        applicantService.insert(SampleData.sampleApplicant("29017376771", "0231"));
    }

    public DummyApplicantService getApplicantService() {
        return applicantService;
    }
}
