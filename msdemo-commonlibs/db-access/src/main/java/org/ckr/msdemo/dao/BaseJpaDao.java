package org.ckr.msdemo.dao;

import org.ckr.msdemo.util.DbAccessUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * The parent class for all JPA Dao.
 * All JPA based Dao class should extends this class.
 * This class provide some features that are common for all Dao classes. Such as, define an reference to hold an
 * entity manager object(used to manipulate DB through JPA API), define some util methods that are convenient for
 * DB query.
 */
public class BaseJpaDao {

    /**
     * An reference to an JPA Entity manager.
     * This is used to manipulate DB through JPA API.
     */
    protected EntityManager entityManager = null;


    /**
     * Setup the entity manager for current Dao object.
     * When a Dao is init in Spring container, this method is used to insert a valid entity manager object into
     * the Dao object.
     * @param entityManager An valid EntityManager object.
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private EntityManager getEntityManager() {
        if (entityManager == null) {
            throw new RuntimeException("entity manager is not initialized. "
                                      + "Please use setEntityManager() method to inject an entity manager.");
        }

        return entityManager;
    }

    /**
     * An util method for DB query.
     *
     * @param ql       An JPQL string. Please refer
     *                 <a href="https://www.tutorialspoint.com/jpa/jpa_jpql.htm">JPQL tutorial</a> for more detail
     *                 about JPQL. This parameter support dynamic QL feature which is mentioned in
     *                 {@link DbAccessUtil#adjustDynamicQueryString(String, Set)}
     * @param params   An Map object include query parameters. The key is the parameter name which is mentioned in the
     *                 QL. The value is the corresponding parameter value.
     * @param mapper  An mapper object that map the result record from raw data type to expected data type.
     * @param <T>     The expected result record data type.
     * @return        A List of query result records. The data type of each element in this List is T if mapper is
     *                 not null.
     */
    protected <T> List<T> executeDynamicQuery(final String ql,
                                              final Map<String, Object> params,
                                              final Function<Object[], T> mapper) {


        String adjustedQl = DbAccessUtil.adjustDynamicQueryString(ql, params.keySet());

        Query query = getEntityManager().createQuery(adjustedQl);

        DbAccessUtil.setQueryParameter(query, params);

        List rawResultList = query.getResultList();
        List<T> resultList = DbAccessUtil.convertRawListToTargetList(rawResultList, mapper);

        return resultList;
    }
}
