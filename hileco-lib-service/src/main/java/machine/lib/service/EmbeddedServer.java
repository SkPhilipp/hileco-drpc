package machine.lib.service;

import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.exceptions.ExceptionHandler;
import org.codehaus.jackson.jaxrs.Annotations;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
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
     * @throws EmbeddedServerStartException
     */
    public void start(Set<Object> services) throws EmbeddedServerStartException {
        try {
            // servlet context handler for services
            ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletContextHandler.setContextPath(CONTEXT_PATH);

            JacksonJsonProvider jsonProvider = new JacksonJaxbJsonProvider(Annotations.JACKSON).configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            LocalServices.add(jsonProvider);
            LocalServices.add(new ExceptionHandler());
            services.forEach(LocalServices::add);
            ServletHolder servletHolder = new ServletHolder(new HttpServletDispatcher());
            servletHolder.setInitParameter(JAVAX_WS_RS_APPLICATION, LocalServices.class.getName());
            servletContextHandler.addServlet(servletHolder, PATH_SPEC);
            Server server = new Server(port);
            server.setHandler(servletContextHandler);
            server.start();
        } catch (Exception e) {
            throw new EmbeddedServerStartException(e);
        }
    }

}