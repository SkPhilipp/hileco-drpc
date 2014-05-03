package machine.management.services.lib.services;

import machine.management.services.lib.model.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * Provides create / read / update / delete methods, where for all operations on an existing {@link machine.management.services.lib.model.Model} the
 * {@link machine.management.services.lib.model.Model}'s Id must be known.
 *
 * @param <T> any persistable entity model with operations matching the methods available on this class.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ModelService<T extends Model> {

    /**
     * Creates an entity, assigns a new UUID to it, even if it already has a UUID assigned.
     *
     * @param instance {@link T} instance whose properties to use for instantiating the entity
     * @return the {@link java.util.UUID} assigned to the new entity
     */
    @POST
    @Path("/entity")
    public UUID create(T instance);

    /**
     * Finds an entity by id.
     *
     * @param id id of the entity to be found.
     * @return matching entity or null.
     */
    @GET
    @Path("/entity/{id}")
    public T read(@PathParam("id") UUID id);

    /**
     * Updates an entity by id.
     *
     * @param instance {@link T} instance whose properties to assign to the entity with the given id
     */
    @PUT
    @Path("/entity")
    public void update(T instance);

    /**
     * Deletes an entity by id.
     *
     * @param id id of the entity to be deleted
     */
    @DELETE
    @Path("/entity/{id}")
    public void delete(@PathParam("id") UUID id);

}
