package machine.management.api.services;

import machine.management.api.entities.Model;

import javax.ws.rs.*;
import java.util.UUID;

/**
 * Provides create / read / update / delete methods, where for all operations on an existing {@link Model} the
 * {@link Model}'s Id must be known.
 *
 * @param <T> any persistable entity model with operations matching the methods available on this class.
 */
public interface ModelService<T extends Model> {

    /**
     * Saves an entity, if it does not have an id, it will have a new UUID assigned to it. When the instance
     * has an id, the instance will instead be updated by id.
     *
     * @param instance {@link T} instance whose properties to use for instantiating the entity
     * @return the given instance, usually slightly modified, with minimally an id assigned
     */
    @POST
    @Path("/entity")
    public T save(T instance);

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
     * Deletes an entity by id.
     *
     * @param id id of the entity to be deleted
     */
    @DELETE
    @Path("/entity/{id}")
    public void delete(@PathParam("id") UUID id);

}
