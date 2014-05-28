package machine.management.services;

import machine.management.api.entities.Model;
import machine.management.api.services.QueryableModelService;
import machine.management.dao.GenericModelDAO;

import java.util.List;

/**
 * Complete implementation of {@link QueryableModelService} using {@link GenericModelDAO}
 *
 * @param <T> any persistable and identifyable entity model.
 */
public abstract class AbstractQueryableModelService<T extends Model> extends AbstractModelService<T> implements QueryableModelService<T> {

    public AbstractQueryableModelService(GenericModelDAO<T> modelDAO) {
        super(modelDAO);
    }

    @Override
    public List<T> query(T example) {
        return getModelDAO().query(example);
    }

    @Override
    public List<T> query(T example, int offset, int limit) {
        return getModelDAO().query(example, offset, limit);
    }

    @Override
    public long count(T example) {
        return getModelDAO().count(example);
    }

}
