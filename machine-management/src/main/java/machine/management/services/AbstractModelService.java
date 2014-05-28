package machine.management.services;

import machine.management.api.entities.Model;
import machine.management.api.services.ModelService;
import machine.management.dao.GenericModelDAO;

import java.util.UUID;

/**
 * Complete implementation of {@link ModelService} using {@link GenericModelDAO}.
 *
 * @param <T> any persistable and identifyable entity model.
 */
public abstract class AbstractModelService<T extends Model> implements ModelService<T> {

    public final GenericModelDAO<T> modelDAO;

    public AbstractModelService(GenericModelDAO<T> modelDAO) {
        this.modelDAO = modelDAO;
    }

    public GenericModelDAO<T> getModelDAO() {
        return modelDAO;
    }

    @Override
    public T save(T instance) {
        return modelDAO.save(instance);
    }

    @Override
    public T read(UUID id) {
        return modelDAO.read(id);
    }

    @Override
    public void delete(UUID id) {
        modelDAO.delete(id);
    }

}
