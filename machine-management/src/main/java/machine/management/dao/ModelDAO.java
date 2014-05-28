package machine.management.dao;


import machine.management.api.entities.Model;
import org.hibernate.Criteria;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Provides save / read / delete methods, where for all methods on an existing {@link Model} the
 * {@link Model}'s Id must be known.
 *
 * @param <T> any persistable entity model with operations matching the methods available on this class.
 */
public interface ModelDAO<T extends Model> {

    /**
     * Saves an entity, if it does not have an id, it will have a new UUID assigned to it. When the instance
     * has an id, the instance will instead be updated by id.
     *
     * @param instance {@link T} instance whose properties to use for instantiating the entity
     * @return given instance with modified properties, and minimally an id assigned.
     */
    public T save(T instance);

    /**
     * Finds an entity by id.
     *
     * @param id id of the entity to be found.
     * @return matching entity or null.
     */
    public T read(UUID id);

    /**
     * Deletes an entity by id.
     *
     * @param id id of the entity to be deleted
     */
    public void delete(UUID id);

    /**
     * Performs a query by {@link org.hibernate.criterion.Example} on {@link T}.
     *
     * @param example an example instance
     * @return matching entities
     */
    public List<T> query(T example);

    /**
     * Performs a query using a given queryModifier function.
     *
     * @param queryModifier modifier
     * @return querymodifier result
     */
    public <QT> QT query(Function<Criteria, QT> queryModifier);

    /**
     * Performs a count by {@link org.hibernate.criterion.Example} on {@link T}.
     *
     * @param example an example instance
     * @return result count
     */
    public long count(T example);

    /**
     * Performs a query by {@link org.hibernate.criterion.Example} on {@link T}, with a given offset and limit for pagination.
     *
     * @param example an example instance
     * @param offset  offset to use in querying
     * @param limit   limit to use in querying
     * @return matching entities
     */
    public List<T> query(T example, int offset, int limit);

}
