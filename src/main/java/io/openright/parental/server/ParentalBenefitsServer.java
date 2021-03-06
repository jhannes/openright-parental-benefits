package io.openright.parental.server;

import io.openright.infrastructure.util.IOUtil;
import io.openright.infrastructure.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.MovedContextHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URI;

@Slf4j
public class ParentalBenefitsServer {

    private Server server;
    private ParentalBenefitsConfig config;

    public ParentalBenefitsServer(ParentalBenefitsConfig config) {
        this.config = config;
        this.server = new Server(config.getHttpPort());
    }

    protected HandlerList createHandlers() {
        HandlerList handlers = new HandlerList();
        handlers.addHandler(new ShutdownHandler("dsgsdglsdgsdgnk", false, true));
        handlers.addHandler(createWebApi());
        handlers.addHandler(createWebApp());
        handlers.addHandler(new MovedContextHandler(null, "/", "/parental"));
        return handlers;
    }

    protected ServletContextHandler createWebApi() {
        ServletContextHandler context = new ServletContextHandler(null, "/parental/api");
        context.setSessionHandler(new SessionHandler());
        context.addServlet(ParentalBenefitsFrontController.class, "/*");
        return context;
    }

    protected WebAppContext createWebApp() {
        WebAppContext webAppContext = new WebAppContext(null, "/parental");
        webAppContext.setBaseResource(Resource.newClassPathResource("parental"));
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        return webAppContext;
    }

    public static void main(String[] args) throws Exception {
        LogUtil.setupLogging("logging-parental-benefits.xml");
        ParentalBenefitsConfig config = new ParentalBenefitsConfigFile(IOUtil.extractResourceFile("parental-benefits.properties"));

        new ParentalBenefitsServer(config).start();
    }

    public void start() throws Exception {
        server.setHandler(createHandlers());
        new EnvEntry("parental/config", config);
        this.server.start();
        log.info("Started {}", getURI());
    }

    public URI getURI() {
        return server.getURI().resolve("/");
    }
}
