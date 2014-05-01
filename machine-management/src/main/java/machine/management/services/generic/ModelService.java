package machine.management.services.generic;

import machine.management.model.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

/**
 *
 * @param <T> any persistable entity model with operations matching the methods available on this class.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ModelService<T extends Model> {

    /**
     * Creates an entity, assigns an ID to it.
     *
     * @param instance {@link T} instance whose properties to use for instantiating the entity
     * @return the {@link UUID} assigned to the new entity
     */
    @POST
    @Path("/")
    public UUID create(T instance);

    /**
     * Finds an entity by id.
     *
     * @param id id of the entity to be found.
     * @return matching entity or null.
     */
    @GET
    @Path("/{id}")
    public T read(@PathParam("id") UUID id);

    /**
     * Updates an entity by id.
     *
     * @param instance {@link T} instance whose properties to assign to the entity with the given id
     */
    @PUT
    @Path("/")
    public void update(T instance);

    /**
     * Deletes an entity by id.
     *
     * @param id id of the entity to be deleted
     */
    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") UUID id);

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
