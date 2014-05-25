package machine.lib.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class Services extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(Services.class);
    private static ThreadLocal<Set<Object>> inheritableLocalServices = new InheritableThreadLocal<>();

    /**
     * Adds a service to the set of services.
     *
     * @param object any jax rs service
     * @return true if this set did not already contain the specified element
     */
    public static boolean add(Object object) {
        Set<Object> services = inheritableLocalServices.get();
        if(services == null){
            services = new HashSet<>();
            inheritableLocalServices.set(services);
        }
        return services.add(object);
    }

    @Override
    public Set<Object> getSingletons() {
        return inheritableLocalServices.get();
    }

}
