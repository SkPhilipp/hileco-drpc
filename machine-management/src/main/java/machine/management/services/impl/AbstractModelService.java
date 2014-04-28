package machine.management.services.impl;

import com.google.common.reflect.TypeToken;
import machine.management.model.Model;
import machine.management.services.ModelService;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

public abstract class AbstractModelService<T extends Model> implements ModelService<T> {

    private final EntityManager entityManager;
    private final Class<? super T> type;

    public AbstractModelService() {
        TypeToken<T> token = new TypeToken<T>(getClass()) {};
        this.type = token.getRawType();
        // TODO: get entity manager
        this.entityManager = null;
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public UUID create(T instance) {
        UUID id = UUID.randomUUID();
        instance.setId(id);
        entityManager.persist(instance);
        return id;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    @Override
    public T read(@PathParam("id") UUID id) {
        return (T) entityManager.find(type, id);
    }


    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public void update(@PathParam("id") UUID id, T instance) {
        instance.setId(id);
        entityManager.persist(instance);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    @Override
    public void delete(@PathParam("id") UUID id) {
        try {
            T instance = (T) type.newInstance();
            instance.setId(id);
            entityManager.remove(instance);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to instantiate an instance to assign an id to.");
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

}
