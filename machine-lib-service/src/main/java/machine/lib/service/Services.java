package machine.lib.service;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class Services extends Application {

    private static Set<Object> services = new HashSet<>();

    /**
     * Adds a service to the set of services.
     *
     * @param object any jax rs service
     * @return true if this set did not already contain the specified element
     */
    public static boolean add(Object object) {
        return services.add(object);
    }

    @Override
    public Set<Object> getSingletons() {
        return services;
    }

}