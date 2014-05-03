package machine.management.services.lib.services;

import machine.management.services.lib.dao.Model;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * An extension of {@link ModelService} allowing for querying by example object.
 *
 * @param <T> any persistable entity model with operations matching the methods available on this class.
 */
public interface QueryableModelService<T extends Model> extends ModelService<T> {

    /**
     * Performs a query by {@link org.hibernate.criterion.Example} on {@link T}.
     *
     * @param example an example instance
     * @return matching entities
     */
    @POST
    @Path("/query")
    public List<T> query(T example);

    /**
     * Performs a query by {@link org.hibernate.criterion.Example} on {@link T}, with a given offset and limit for pagination.
     *
     * @param example an example instance
     * @param offset offset to use in querying
     * @param limit limit to use in querying
     * @return matching entities
     */
    @POST
    @Path("/query")
    public List<T> query(T example, @QueryParam("offset") int offset, @QueryParam("limit") int limit);

    /**
     * Performs a count by {@link org.hibernate.criterion.Example} on {@link T}.
     *
     * @param example an example instance
     * @return result count
     */
    @POST
    @Path("/count")
    public long count(T example);

}
