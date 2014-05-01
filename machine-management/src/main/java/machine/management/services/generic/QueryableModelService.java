package machine.management.services.generic;

import machine.management.model.Identifyable;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

/**
 * An extension of {@link ModelService} allowing for querying by example object.
 *
 * @param <T> any persistable entity model with operations matching the methods available on this class.
 */
public interface QueryableModelService<T extends Identifyable> extends ModelService<T> {

    /**
     * Performs a query by {@link org.hibernate.criterion.Example} on {@link T}.
     *
     * @param example an example instance
     * @return matching entities
     */
    @POST
    @Path("/query/")
    @SuppressWarnings("unchecked")
    public List<T> query(T example);

}
