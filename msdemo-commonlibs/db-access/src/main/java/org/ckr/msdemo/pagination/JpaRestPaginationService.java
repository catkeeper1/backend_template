package org.ckr.msdemo.pagination;

import org.ckr.msdemo.pagination.PaginationContext.QueryRequest;
import org.ckr.msdemo.pagination.PaginationContext.QueryResponse;
import org.ckr.msdemo.pagination.PaginationContext.SortCriteria;
import org.ckr.msdemo.util.DbAccessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Implement pagination query base on JPA.
 *
 * <p>This service provide below features:
 * <ul>
 *     <li>Construct dynamic QL base on parameter.
 *     <li>Retrieve an range of records from a data set that should be returned by a query.
 *     <li>Sort records by multiple fields.
 *     <li>Retrun the actual range of records
 *     <li>Return the total number of records available in a query.
 * </ul>
 *
 * <p>Before it is used, please register this as a bean in Spring container and inject a valid JPA EntityManager.
 * Below is an example:
 * <pre>
 *     <code>
 *         &#64;Configuration
 *         public class PaginationConfig {
 *             ...
 *             //retrieve a valid entityManager here.
 *             &#64;Autowired
 *             EntityManager entityManager;
 *             ...
 *
 *             //create a instance of JpaRestPaginationService
 *             &#64;Bean
 *             public JpaRestPaginationService loadJpaRestPaginationService() {
 *                 JpaRestPaginationService result = new JpaRestPaginationService();
 *                 //inject a valid entity manager.
 *                 result.setEntityManager(this.entityManager);
 *                 return result;
 *             }
 *         }
 *     </code>
 * </pre>
 * After it is created in spring container, developer can call
 * {@link JpaRestPaginationService#query(String, Map, Function, Long)} to do query.
 *
 *
 */
public class JpaRestPaginationService {
    private static final Logger LOG = LoggerFactory.getLogger(JpaRestPaginationService.class);

    /**
     * EntityManager should be set before using JpaRestPaginationService.
     */
    private EntityManager entityManager;




    private static String adjustQueryString(String ql, Map<String, Object> params) {


        return DbAccessUtil.adjustDynamicQueryString(ql, params != null ? params.keySet() : null);

    }


    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Do query base on a JPQL.
     *
     * <p>This method will access
     * {@link PaginationContext#getQueryRequest()} to retrieve pagination request info and use
     * {@link PaginationContext#setResponseInfo(Long, Long, Long)} to return
     * pagination response info. Before this method is called, please make sure the pagination request info is
     * available in {@link PaginationContext}.
     *
     * <p>Below is an example about how to call this method:
     * <pre>
     *     <code>
     *
     *         String queryStr = "select u.userName as userName, u.userDescription, u.locked, u.password , g.roleCode"
     *         + ", g.roleDescription from User u left join u.roles as g where 1=1 "
     *         + "&#47;*userName| and u.userName = :userName *&#47;"
     *         + "&#47;*userDesc| and u.userDescription like :userDesc *&#47;";
     *
     *         Function&#60;Object[], UserWithRole&#62; mapper = new Function&#60;Object[], UserWithRole&#62;() {
     *
     *             &#64;Override
     *             public UserWithRole apply(Object[] row) {
     *
     *                 UserWithRole view = new UserWithRole();
     *
     *                 view.setUserName((String) row[0]);
     *                 view.setUserDescription((String) row[1]);
     *                 view.setLocked(((Boolean) row[2]));
     *                 view.setPassword((String) row[3]);
     *                 view.setRoleCode((String) row[4]);
     *                 view.setRoleDescription((String) row[5]);
     *
     *                 return view;
     *             }
     *
     *
     *         };
     *         List&#60;UserWithRole&#62; result = jpaRestPaginationService.query(queryStr, params, mapper);
     *     </code>
     * </pre>
     *
     *
     * @param ql                 The JPQL for the query. Please refer
     *                            <a href="https://www.tutorialspoint.com/jpa/jpa_jpql.htm">JPQL tutorial</a> for more
     *                            detail info. Please note that if there is a "&#47;* ... *&#47;" inside the JPQL,
     *                            it means that is a dynamic part of the JPQL. In the example above, if parameter
     *                            "userName" is in "params", " and u.userName = :userName " will be added to the JPQL.
     *                            Otherwise, this part will not be involved in the query. Developer should utilize
     *                            this feature when query need dynamic JPQL.
     * @param params              A map that include all parameters that will be used in the JPQL.
     *                            The key of this map object is the parameter name. The value of this map object is
     *                            the parameter value.
     *                            This method call javax.persistence.Query#setParameter(String, Object)
     *                            for each pair in this map object.
     * @param mapper              If this is not null, it will be used to map object type that returned by JPA
     *                            to object type that will be returned.
     *                            For example, the JPQL will return an Object[]. However, the expected object type is
     *                            DateView. Then, need to use this mapper to map Object[] to DataView.
     * @param maxNoRecordsPerPage The max no of records that will be returned by this method.
     * @return                   The content of records within the requested range.
     *
     */
    public <R> List<R> query(final String ql,
                             final Map<String, Object> params,
                             final Function<Object[], R> mapper,
                             final Long maxNoRecordsPerPage) {
        QueryRequest queryRequest = PaginationContext.getQueryRequest();
        queryRequest = this.adjustRange(queryRequest, maxNoRecordsPerPage);
        String queryStr = adjustQueryString(ql, params);
        QueryResponse response = new QueryResponse();
        List<R> resultList = doQueryContent(response, queryRequest, queryStr, params, mapper);
        doQueryTotalNoRecords(response, resultList.size(), queryRequest, queryStr, params);
        PaginationContext.setResponseInfo(response.getStart(), response.getEnd(), response.getTotal());

        //TransactionAttribute att = TransactionInfoHolder.getTransactionAttribute();

        return resultList;
    }

    /**
     * Do query base on a JPQL.
     *
     * <p>This is the same as {@link JpaRestPaginationService#query(String, Map, Function, Long)} except
     * the maxNoRecordsPerPage parameter value is always 500.
     *
     * @see JpaRestPaginationService#query(String, Map, Function, Long)
     */
    public <R> List<R> query(final String ql,
                             final Map<String, Object> params,
                             Function<Object[], R> mapper) {

        return query(ql, params, mapper, 500L);

    }

    @SuppressWarnings("unchecked")
    private <R> List<R> doQueryContent(QueryResponse response,
                                       QueryRequest request,
                                       String queryStr,
                                       Map<String, Object> params,
                                       Function<Object[], R> mapper) {

        String queryString = appendSortCriteria(queryStr, request);

        LOG.debug("get data JPQL:{}", queryString);
        Query query = entityManager.createQuery(queryString);
        DbAccessUtil.setQueryParameter(query, params);
        if (request != null && request.getStart() != null) {
            query.setFirstResult(request.getStart().intValue() - 1);
        }
        if (request != null && request.getEnd() != null) {
            query.setMaxResults((int) (request.getEnd() - request.getStart()) + 1);
        }

        List rawResultList = query.getResultList();
        List<R> resultList = DbAccessUtil.convertRawListToTargetList(rawResultList, mapper);


        if (request == null || request.getStart() == null) {
            response.setStart(1L);
        } else {
            response.setStart(request.getStart());
        }


        if ((long) resultList.size() > 0) {
            response.setEnd(response.getStart() + (long) resultList.size() - 1);
        } else {
            response.setEnd((long) resultList.size());
        }
        return resultList;
    }



    private QueryRequest adjustRange(QueryRequest request, Long maxNoRecordsPerPage) {
        if (request == null) {
            return null;
        }
        if (request.getStart() == null) {
            request.setStart((long) 0);
        }
        if (maxNoRecordsPerPage != null) {
            if (request.getEnd() == null || request.getEnd() - request.getStart() > maxNoRecordsPerPage - 1) {
                //make sure the total number records will not exceed the maxNoRecordPerPage
                request.setEnd(request.getStart() + maxNoRecordsPerPage - 1);
            }
        }

        return request;
    }

    private void doQueryTotalNoRecords(QueryResponse response,
                                       int contentSize,
                                       QueryRequest request,
                                       String queryStr,
                                       Map<String, Object> params) {

        if (request == null || (request.getStart() == 0 && request.getEnd() == null)) {
            response.setTotal((long) contentSize);
            LOG.debug("request IS NULL");
            return;
        }


        String queryString = getQlForTotalNoRecords(queryStr);

        LOG.debug("get total no of records JPQL:{}", queryString);

        Query query = (Query) entityManager.createQuery(queryString);

        DbAccessUtil.setQueryParameter(query, params);

        response.setTotal((Long) query.getSingleResult());
        LOG.info("getContent = {}", contentSize);
        LOG.debug("total number of records {}", response.getTotal());
        return;
    }

    private String getQlForTotalNoRecords(String queryStr) {

        String result;

        String upperQueryStr = queryStr.toUpperCase();


        int start = queryStr.indexOf("(");

        int end = queryStr.indexOf(")");

        int fromIndex;

        int queryStrLen = queryStr.length() - 1;

        do {
            if (queryStrLen < 0) {
                LOG.info("cannot find a top level 'FROM' from query string: '"
                    + queryStr + "' . The i is < 0 already");
            }

            fromIndex = upperQueryStr.lastIndexOf("FROM", queryStrLen);
            if (fromIndex < 0) {
                LOG.info("cannot find a top level 'FROM' from query string: '"
                    + queryStr + "' . Cannot find 'FROM'. The i = " + queryStrLen);
            }

            if (fromIndex <= end && fromIndex >= start) {
                queryStrLen = fromIndex - 1;
            } else {
                break;
            }

        }
        while (true);

        result = "SELECT COUNT(*) " + queryStr.substring(fromIndex);

        LOG.debug("JPQL to get total number of records {}", result);

        return result;
    }

    private String appendSortCriteria(String queryString, QueryRequest request) {

        if (request == null) {
            return queryString;
        }

        List<SortCriteria> sortCriList = request.getSortCriteriaList();

        if (sortCriList == null || sortCriList.isEmpty()) {
            return queryString;
        }


        StringBuilder result = new StringBuilder(queryString);
        result.append(" order by ");

        for (int i = 0; i < sortCriList.size(); i++) {
            SortCriteria criteria = sortCriList.get(i);
            result.append(criteria.getFieldName());

            if (criteria.isAsc()) {
                result.append(" asc ");
            } else {
                result.append(" desc ");
            }

            if (i < sortCriList.size() - 1) {
                result.append(" , ");
            }

        }
        return result.toString();
    }


}
