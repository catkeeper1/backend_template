package org.ckr.msdemo.pagination;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.ckr.msdemo.utility.annotation.MockedTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by yukai.a.lin on 8/14/2017.
 */
@Category(MockedTest.class)
public class JpaRestPaginationServiceMockedTests {

    private static Logger LOG = LoggerFactory.getLogger(JpaRestPaginationServiceMockedTests.class);

    @Tested
    JpaRestPaginationService jpaRestPaginationService;

    @Injectable
    private EntityManager entityManager;

    @Mocked
    private Query query;

    @Test
    public void testJpaRestPaginationServiceWithoutPageSizeLimit() {
        String hql = "SELECT userName from";

        PaginationContext.QueryRequest queryRequest = constructQueryRequest(5, 6, true, "userName");

        List result = testJpaRestPaginationServiceTemplate(hql,
            (int) (queryRequest.getEnd() - queryRequest.getStart() + 1), 20, null, queryRequest,
            (int) (queryRequest.getEnd() - queryRequest.getStart() + 1), 5, 6, 20);

        assertThat(result.get(0)).isEqualTo(1);
        assertThat(result.get(1)).isEqualTo(1);
    }

    @Test
    public void testJpaRestPaginationServiceWithPageSizeLimit() {
        String hql = "SELECT userName from";

        PaginationContext.QueryRequest queryRequest = constructQueryRequest(5, 100, true, "userName");

        List result = testJpaRestPaginationServiceTemplate(hql, 5, 10, "5", queryRequest,
            5, 5, 9, 10);

        assertThat(result.get(0)).isEqualTo(1);
        assertThat(result.get(1)).isEqualTo(1);
    }


    private PaginationContext.QueryRequest constructQueryRequest(int start, int end, boolean asc, String field) {
        PaginationContext.QueryRequest queryRequest = new PaginationContext.QueryRequest();
        queryRequest.setStart((long) start);
        queryRequest.setEnd((long) end);
        List<PaginationContext.SortCriteria> sortCriteriaList = new ArrayList<PaginationContext.SortCriteria>();
        PaginationContext.SortCriteria sortCriteria = new PaginationContext.SortCriteria();
        sortCriteria.setAsc(asc);
        sortCriteria.setFieldName(field);
        sortCriteriaList.add(sortCriteria);
        queryRequest.setSortCriteriaList(sortCriteriaList);
        return queryRequest;
    }


    protected List testJpaRestPaginationServiceTemplate(
        String hql,
        int mockRecordSize,
        int mockTotalRecordSize,
        String maxNoRecordsPerPage,
        PaginationContext.QueryRequest queryRequest,
        int expectCurrentPageSize,
        int expectStart,
        int expectEnd,
        int expectTotal
    ) {

        List<Object[]> list = new ArrayList<Object[]>();
        for (int i = 0; i < mockRecordSize; i++) {
            list.add(new Object[1]);
        }

        Map<String, Object> params = new HashMap<String, Object>();

        Function<Object[], Object> mapper = new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] row) {
                return new Integer(1);
            }
        };

        new Expectations(PaginationContext.class) {
            {
                PaginationContext.getQueryRequest();
                result = queryRequest;
            }
        };

        new Expectations() {
            {
                entityManager.createQuery(anyString);
                result = query;

                query.getResultList();
                result = list;

                query.getSingleResult();
                result = (long) mockTotalRecordSize;
            }
        };


        jpaRestPaginationService.setEntityManager(entityManager);
        List<Object> result;
        if (StringUtils.isEmpty(maxNoRecordsPerPage)) {
            result = jpaRestPaginationService.query(hql, params, mapper);
        } else {
            result = jpaRestPaginationService.query(hql, params, mapper, Long.valueOf(maxNoRecordsPerPage));
        }

        assertThat(result.size()).isEqualTo(expectCurrentPageSize);
        assertThat(PaginationContext.getQueryResponse().getStart()).isEqualTo((long) expectStart);
        assertThat(PaginationContext.getQueryResponse().getEnd()).isEqualTo((long) expectEnd);
        assertThat(PaginationContext.getQueryResponse().getTotal()).isEqualTo((long) expectTotal);

        return result;
    }

}
