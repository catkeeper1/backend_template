package org.ckr.msdemo.pagination;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import org.ckr.msdemo.utility.annotation.MockedTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ckr.msdemo.pagination.PaginationContext.FilterOperator.*;


/**
 * Created by Administrator on 2017/7/8.
 */
@Category(MockedTest.class)
public class PaginationContextMockedTests {
    @Mocked
    private RequestContextHolder requestContextHolder;

    @Mocked
    private ServletRequestAttributes servletRequestAttributes;

    @Mocked
    private HttpServletRequest httpServletRequest;

    @Test
    public void testLoadAll() {
        System.out.println(PaginationContext.class.getName());
        System.out.println(PaginationInterceptor.class.getName());
        System.out.println(PaginationInterceptorConfig.class.getName());
        System.out.println(RestPaginationResponseAdvice.class.getName());

    }

    @Test
    public void testParseRestPaginationParameters() {

        doTestParseRestPaginationParameters("items=0-100",
            0L,
            100L,
            "+abCde,-fdsD,+hhh",
            new boolean[] {true, false, true},
            new String[] {"abCde", "fdsD", "hhh"});

        doTestParseRestPaginationParameters("items=20-23",
            20L,
            23L,
            "-ace",
            new boolean[] {false},
            new String[] {"ace", "fdsD", "hhh"});
    }

    private void doTestParseRestPaginationParameters(final String rangeStr,
                                                     final Long start,
                                                     final Long end,
                                                     final String sortStr,
                                                     final boolean[] isAsc,
                                                     final String[] sortedField) {
        new Expectations() {
            {

                RequestContextHolder.getRequestAttributes();
                result = servletRequestAttributes;

                servletRequestAttributes.getRequest();
                result = httpServletRequest;

                List<String> rangeValues = new ArrayList<>();
                rangeValues.add(rangeStr);
                httpServletRequest.getHeaders("Range");
                times = 1;
                result = Collections.enumeration(rangeValues);

                List<String> sortedByValues = new ArrayList<>();
                sortedByValues.add(sortStr);
                httpServletRequest.getHeaders("SortBy");
                times = 1;
                result = Collections.enumeration(sortedByValues);
            }
        };

        PaginationContext.parseRestPaginationParameters();


        PaginationContext.QueryRequest queryRequest = PaginationContext.getQueryRequest();

        assertThat(queryRequest.getStart()).isEqualTo(start);
        assertThat(queryRequest.getEnd()).isEqualTo(end);

        for (int i = 0; i < queryRequest.getSortCriteriaList().size(); i++) {

            PaginationContext.SortCriteria sortCriterial = queryRequest.getSortCriteriaList().get(i);

            assertThat(sortCriterial.isAsc()).isEqualTo(isAsc[i]);
            assertThat(sortCriterial.getFieldName()).isEqualTo(sortedField[i]);

        }


    }


    @Test
    public void testSplitFilterString() {
        List<String> actualResult = PaginationContext.splitFilterString("abc,def234@,v\\,,j\\,a\\,2\\,\\,3,");

        assertThat(actualResult).containsExactly("abc",
                                                 "def234@",
                                                 "v\\,",
                                                 "j\\,a\\,2\\,\\,3");
    }

    @Test
    public void testSplitFilterStringWithEmptyString() {
        List<String> actualResult = PaginationContext.splitFilterString("");

        assertThat(actualResult).isEmpty();
    }

    @Test
    public void testSplitFilterStringWithSpaces() {
        List<String> actualResult = PaginationContext.splitFilterString("  ");

        assertThat(actualResult).containsExactly("  ");
    }

    @Test
    public void testSplitFilterStringWithAllDelims() {
        List<String> actualResult = PaginationContext.splitFilterString(",,,,");

        assertThat(actualResult).containsExactly("", "", "", "");
    }

    @RunWith(Parameterized.class)
    public static class CreateFilterCriteriaMockedTests{

        private String criteriaStr;
        private String expectedFileName;
        private PaginationContext.FilterOperator expectedFilterOperator;
        private String expectedValue;

        public CreateFilterCriteriaMockedTests(String criteriaStr,
                                               String expectedFileName,
                                               PaginationContext.FilterOperator expectedFilterOperator,
                                               String expectedValue) {
            this.criteriaStr = criteriaStr;
            this.expectedFileName = expectedFileName;
            this.expectedFilterOperator = expectedFilterOperator;
            this.expectedValue = expectedValue;
        }

        @Parameterized.Parameters
        public static Collection testParams() {
            Object[][] result = {
                //criteriaStr      expectedFileName   expectedFilterOperator         expectedValue
                { "abc|N"         ,"abc"             , IS_NULL        , null},
                { "abc|NN"        ,"abc"             , IS_NOT_NULL    , null},
                { "abcA|=|1234"   ,"abcA"            , EQUALS         , "1234"},
                { "cbA|<=|124"   ,"cbA"              , EQUALS_OR_LESS , "124"},
                { "cbA|>=|24|@"   ,"cbA"             , EQUALS_OR_LARGER , "24|@"},
                { "cbA|C|24|@"   ,"cbA"              , CONTAINS , "24|@"},
                { "cbA|<>|24|@"   ,"cbA"             , NOT_EQUALS , "24|@"}
            };

            return Arrays.asList(result);
        }


        @Test
        public void testCreateFilterCriteria () {
            PaginationContext.FilterCriteria filterCriteria =
                    Deencapsulation.invoke(PaginationContext.class,
                            "createFilterCriteria",
                            criteriaStr);

            assertThat(filterCriteria.getFiledName()).isEqualTo(expectedFileName);
            assertThat(filterCriteria.getFilterOperator()).isEqualTo(expectedFilterOperator);

            if (expectedValue != null) {
                assertThat(filterCriteria.getValue()).isEqualTo(expectedValue);
            }

        }


    }



}
