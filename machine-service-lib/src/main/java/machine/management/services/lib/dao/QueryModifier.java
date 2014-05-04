package machine.management.services.lib.dao;

import org.hibernate.Criteria;

/**
 * Converts queries to a type, allowing for custom queries within {@link ModelDAO}.
 *
 * @param <QT> query result type
 */
public interface QueryModifier<QT> {

    /**
     * Converts the Criteria to a result of type {@link QT}.
     *
     * @param criteria
     * @return
     */
    public QT call(Criteria criteria);

}
