package machine.management.services.lib.dao;

import java.util.List;
import java.util.UUID;

/**
 * Provides create / read / update / delete methods, where for all methods on an existing {@link Model} the
 * {@link Model}'s Id must be known.
 *
 * @param <T> any persistable entity model with operations matching the methods available on this class.
 */
public interface ModelDAO<T extends Model> {

    /**
     * Creates an entity, assigns a new UUID to it, even if it already has a UUID assigned.
     *
     * @param instance {@link T} instance whose properties to use for instantiating the entity
     * @return the {@link java.util.UUID} assigned to the new entity
     */
    public UUID create(T instance);

    /**
     * Finds an entity by id.
     *
     * @param id id of the entity to be found.
     * @return matching entity or null.
     */
    public T read(UUID id);

    /**
     * Updates an entity by id.
     *
     * @param instance {@link T} instance whose properties to assign to the entity with the given id
     */
    public void update(T instance);

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
     * Performs a query using a {@link QueryModifier}.
     *
     * @param queryModifier modifier
     * @return querymodifier result
     */
    public<QT> QT query(QueryModifier<QT> queryModifier);

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
     * @param offset offset to use in querying
     * @param limit limit to use in querying
     * @return matching entities
     */
    public List<T> query(T example, int offset, int limit);

    /**
     * Performs a query using a {@link QueryModifier}.
     *
     * @param queryModifier modifier
     * @param offset offset to use in querying
     * @param limit limit to use in querying
     * @return querymodifier result
     */
    public <QT> QT query(QueryModifier<QT> queryModifier, int offset, int limit);
}
