package org.ckr.msdemo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A util class for query string processing.
 */
public class DbAccessUtil {

    private static Logger LOG = LoggerFactory.getLogger(DbAccessUtil.class);

    /**
     * Adjust query string according to parameters.
     * If the query string include something like
     * "/<span></span>*paramName| ... *<span></span>/" the part after "|" will be appended to the final query string
     * which will be used for real DB access. For example, there is a query string as below:
     * <pre>
     *     <code>
     *             select u.userName, u.userDescription, u.locked from User u where 1=1
     *              /<span></span>*userName| and u.userName = :userName *<span></span>/
     *              /<span></span>*userDesc| and u.userDescription like :userDesc *<span></span>/
     *     </code>
     * </pre>
     * If paramNames include "userName", the query string will be adjusted to:
     * <pre>
     *     <code>
     *             select u.userName, u.userDescription, u.locked from User u where 1=1 and u.userName = :userName
     *     </code>
     * </pre>
     * If paramNames include "userDesc", the query string will be adjusted to:
     * <pre>
     *     <code>
     *             select u.userName, u.userDescription, u.locked from User u where 1=1
     *             and u.userDescription like :userDesc
     *     </code>
     * </pre>
     *
     * This feature is very useful for the dynamic query scenario.
     *
     * @param ql    query string that will be adjusted
     * @param paramNames inject params to ql
     * @return adjusted query string
     */
    public static String adjustDynamicQueryString(String ql, Set<String> paramNames) {

        StringBuilder queryStr = new StringBuilder(ql);

        LOG.debug("before adjustment, the query string is {}", queryStr);


        for (int startInd = queryStr.indexOf("/*"); startInd >= 0; startInd = queryStr.indexOf("/*")) {

            int endInd = queryStr.indexOf("*/", startInd);

            if (endInd < 0) {
                break;
            }

            String criteriaStr = queryStr.substring(startInd + 2, endInd);

            LOG.debug("criteria string: {}", criteriaStr);

            StringTokenizer tokenizer = new StringTokenizer(criteriaStr, "|");
            if (tokenizer.countTokens() < 2) {
                LOG.error("invalid criteria string: {}", criteriaStr);
            }

            String criteriaName = tokenizer.nextToken().trim();

            if (paramNames != null && paramNames.contains(criteriaName)) {

                String criteriaContent = tokenizer.nextToken();

                queryStr.replace(startInd, endInd + 2, criteriaContent);

            } else {

                queryStr.delete(startInd, endInd + 2);
            }
        }

        LOG.debug("after adjustment, the query string is {}", queryStr);
        return queryStr.toString();

    }


    /**
     * Put parameters values into a JPA query object.
     * This method loop the values in params and call query.setParameter() method to put the parameter value
     * into the query object.
     * @param query    A JPA query object.
     * @param params   An Map object that include parameters name and parameters values that will be put into the query
     *                 object.
     */
    public static void setQueryParameter(Query query, Map<String, Object> params) {

        if (params == null) {
            return;
        }

        params.entrySet().forEach(e -> query.setParameter(e.getKey(), e.getValue()));

    }

    /**
     * Convert a query result record from raw data type into an expected date type.
     *
     * @param rawResultList   A list store query result record in raw data type(should be an array).
     * @param mapper          A mapper function that can be used for the converting.
     * @param <R>             The expected data type. All query result records will be converted to this data type.
     * @return                A List that include record in data type R
     */
    public static <R> List<R> convertRawListToTargetList(List rawResultList, Function<Object[], R> mapper) {
        //if the raw list is empty, just return it because nothing need to be converted.
        if (rawResultList.isEmpty()) {
            return rawResultList;
        }

        if (mapper == null) {
            return rawResultList;
        }

        Stream<Object[]> stream = (Stream<Object[]>) rawResultList.stream();

        List<R> resultList = stream.map(mapper)
                .collect(Collectors.toList());

        return resultList;

    }
}
