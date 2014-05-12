package machine.lib.service;

import machine.lib.service.exceptions.ExceptionHandler;
import org.codehaus.jackson.jaxrs.Annotations;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import java.util.Set;

public class EmbeddedServer {

    public static final String CONTEXT_PATH = "/";
    public static final String PATH_SPEC = "/*";
    public static final String JAVAX_WS_RS_APPLICATION = "javax.ws.rs.Application";

    private final int port;

    public EmbeddedServer(int port) {
        this.port = port;
    }

    /**
     * Starts the server on port {@link #port}.
     *
     * @param services set of jax rs services, `machine-lib-service` providers are already included in this.
     * @throws Exception
     */
    public void start(Set<Object> services) throws Exception {
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(CONTEXT_PATH);
        Services.add(new JacksonJaxbJsonProvider(Annotations.JACKSON));
        Services.add(new ExceptionHandler());
        for(Object service : services){
            Services.add(service);
        }
        ServletHolder servletHolder = new ServletHolder(new HttpServletDispatcher());
        servletHolder.setInitParameter(JAVAX_WS_RS_APPLICATION, Services.class.getName());
        context.addServlet(servletHolder, PATH_SPEC);
        server.setHandler(context);
        server.start();
    }

}