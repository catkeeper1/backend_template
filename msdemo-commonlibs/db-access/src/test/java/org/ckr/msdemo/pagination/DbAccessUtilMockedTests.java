package org.ckr.msdemo.pagination;

import org.ckr.msdemo.util.DbAccessUtil;
import org.ckr.msdemo.utility.annotation.MockedTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by yukai.a.lin on 8/14/2017.
 */
@Category(MockedTest.class)
public class DbAccessUtilMockedTests {

    private static Logger LOG = LoggerFactory.getLogger(DbAccessUtilMockedTests.class);

    @Test
    public void testAdjustQueryStringAll() {
        String rawQuery = "select u.a, u.b from User u where 1=1 /*userName| and u.Name = :userName */ " +
            "/*Desc| and u.Description like :Desc */";
        Set<String> params = new HashSet<>();
        params.add("userName");
        params.add("Desc");

        String parsedQuery =
            DbAccessUtil.adjustDynamicQueryString(rawQuery, params);
        assertThat(parsedQuery).isEqualTo("select u.a, u.b from User u where 1=1  and u.Name = :userName   " +
            "and u.Description like :Desc ");
    }

    @Test
    public void testAdjustQueryStringPartial() {
        String rawQuery = "select u.a, u.b from User u where 1=1 /*userName| and u.Name = :userName */ /*Desc| " +
            "and u.Description like :Desc */";
        Set<String> params = new HashSet<>();
        params.add("userName");
        String parsedQuery = DbAccessUtil.adjustDynamicQueryString(rawQuery, params);

        assertThat(parsedQuery).contains("select u.a, u.b from User u where 1=1  and u.Name = :userName");
    }

    @Test
    public void testAdjustQueryStringNone() {
        String rawQuery = "select u.a, u.b from User u where 1=1 /*userName| and u.Name = :userName */ /*Desc| and " +
            "u.Description like :Desc */";
        Set<String> params = new HashSet<>();
        String parsedQuery = DbAccessUtil.adjustDynamicQueryString(rawQuery, params);
        assertThat(parsedQuery).contains("select u.a, u.b from User u where 1=1");
    }

}
