package machine.management.services.lib.services;

import machine.management.services.lib.dao.GenericModelDAO;
import machine.management.services.lib.dao.Model;

import java.util.UUID;

/**
 * Complete implementation of {@link ModelService} using {@link machine.management.services.lib.dao.GenericModelDAO}
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
    public UUID create(T instance) {
        return modelDAO.create(instance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T read(UUID id) {
        return modelDAO.read(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T read(Model model) {
        return modelDAO.read(model.getId());
    }

    @Override
    public void update(T instance) {
        modelDAO.update(instance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete(UUID id) {
        modelDAO.delete(id);
    }
}
