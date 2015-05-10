package io.openright.parental.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.MovedContextHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

public class ParentalBenefitsServer {

    private Server server;

    public ParentalBenefitsServer() {
        this.server = new Server(8080);

        server.setHandler(createHandlers());
    }

    private Handler createHandlers() {
        HandlerList handlers = new HandlerList();
        handlers.addHandler(new ShutdownHandler("dsgsdglsdgsdgnk", false, true));
        handlers.addHandler(createWebApi());
        handlers.addHandler(createWebApp());
        handlers.addHandler(new MovedContextHandler(null, "/", "/parental"));
        return handlers;
    }

    private ServletContextHandler createWebApi() {
        ServletContextHandler context = new ServletContextHandler(null, "/parental/api");
        context.addServlet(ParentalBenefitsFrontController.class, "/*");
        return context;
    }

    private WebAppContext createWebApp() {
        WebAppContext webAppContext = new WebAppContext(null, "/parental");
        webAppContext.setBaseResource(Resource.newClassPathResource("parental"));
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        return webAppContext;
    }

    public static void main(String[] args) throws Exception {
        new ParentalBenefitsServer().start();
    }

    private void start() throws Exception {
        this.server.start();
    }
}
