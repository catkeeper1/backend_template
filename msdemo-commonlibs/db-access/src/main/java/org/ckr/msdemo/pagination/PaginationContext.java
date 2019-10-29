package org.ckr.msdemo.pagination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Store the pagination request/response info for each HTTP request and its response so that pagination info
 * can be shared by different components.
 *
 * <p>The pagination request info come from HTTP request and it is used by the pagination service component
 * (such as {@link JpaRestPaginationService}) only. In MVC model, there are controllers and services objects between
 * the HTTP request and pagination service component. Becasue it is not a good practise to let every controllers and
 * services to pass pagination info from HTTP request to pagination service component, this class is created.
 * When a HTTP request with pagination info come in,
 * {@link PaginationInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)} will call
 * {@link #parseRestPaginationParameters()} to parse pagination request info and store in a ThreadLocal object. Later,
 * the pagination service component can retrieve this pagination request info to do query. After query is done,
 * pagination service component can store the pagination repsonse info to this class(stored in ThreadLocal object as
 * well). Finally, {@link RestPaginationResponseAdvice} will use the pagination response info in this class to modify
 * the header of HTTP response. In the approach above, the pagination info handling is transparent to controllers and
 * services objects.
 *
 */
public class PaginationContext {


    private static final Logger LOG = LoggerFactory.getLogger(PaginationContext.class);

    private static final ThreadLocal<QueryRequest> requestInfo =
        new ThreadLocal<>();

    private static final ThreadLocal<QueryResponse> responseInfo =
        new ThreadLocal<>();


    private static Long parseNum(String numStr) {
        if (numStr == null) {
            return null;
        }

        try {
            return Long.valueOf(numStr);
        } catch (NumberFormatException exp) {
            return null;
        }
    }

    /**
     * Parse pagination requst info from HTTP request header and store it in ThreadLocal.
     *
     * <p>It is expected that there are below headers in the HTTP request:
     * <ul>
     *     <li>The name is "Range". This header is used to indicate the range of records should be retrieved.
     *     Its value is "items={start}-{end}". "start" /"end" is the start/end position
     *     of records that should be retrieved. For example, if the first 10 records should be retrieved, the value
     *     should be "items=1-10".
     *     <li>The name is "SortBy". This header is used to indicate which fields should be used for sorting.
     *     Its value is "{+/-}{fieldName}". "+"/"-" maeans asc/desc. "fieldName" is the field used for sorting.
     *     For example, if we want to do sorting by field "abc" with asc and field "def" with desc, the value should be
     *     "+abc,-def".
     * </ul>
     * A {@link PaginationContext.QueryRequest} object will be created to store the parsed info mentioned above. This
     * object will be saved in ThreadLocal.
     * set {@link PaginationContext.QueryRequest} (Range, SortBy)
     */
    public static void parseRestPaginationParameters() {

        HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if (request == null) {
            return;
        }

        QueryRequest queryRequest = new QueryRequest();
        queryRequest = parsePageRange(queryRequest, request);

        if (queryRequest.getStart() == null) {
            return;
        }

        queryRequest = parseSortBy(queryRequest, request);
        queryRequest = parseFilterBy(queryRequest, request);

        LOG.debug("queryRequest = {}", queryRequest);

        requestInfo.set(queryRequest);

    }

    /**
     * Modify HTTP response header to return pagination response info to HTTP client.
     * Set response header with header name <code>Content-Range</code>
     * and header value is <code>items {start}-{end}/{total}</code>. "start"/end" is the start/end position of the
     * range of records that will be returned actually. "total" is the total number of records available for this
     * query. For example, if the HTTP client want to retrieve records from the 11th record to 20th record. However,
     * because there are only 15 records available for this query in total, only the records from 11th record to 15th
     * record are returned. In this scenario, the value of <code>Content-Range</code> is <code>item 11-15/15</code>.
     *
     *
     * @param response ServerHttpResponse
     */
    public static void setRestPaginationResponse(ServerHttpResponse response) {

        QueryResponse queryResponse = responseInfo.get();

        if (queryResponse == null) {
            return;
        }

        String headerContent = "items " + queryResponse.getStart() + "-"
            + queryResponse.getEnd() + "/"
            + queryResponse.getTotal();

        LOG.debug("Content-Range={}", headerContent);

        response.getHeaders().set("Content-Range", headerContent);


    }

    /**
     * Store pagination response info into ThreadLocal so that it can be used to modify HTTP response header later.
     *
     * <p>Create an instance of {@link PaginationContext.QueryResponse} to store pagination response info and save
     * it in a ThreadLocal object.
     *
     * @param start start position of range of record that will be returned actually. It started from 1.
     * @param end   end position of range of record that will be returned actually. It started from 1.
     * @param total total number of records are available for this query.
     *
     * @see #setRestPaginationResponse(ServerHttpResponse)
     */
    public static void setResponseInfo(Long start, Long end, Long total) {
        QueryResponse response = new QueryResponse(start, end, total);
        responseInfo.set(response);
    }

    public static QueryRequest getQueryRequest() {
        return requestInfo.get();
    }

    public static QueryResponse getQueryResponse() {
        return responseInfo.get();
    }

    /**
     * Remove all request and response info in ThreadLocal to release memory.
     * Please refer {@link #parseRestPaginationParameters()} and {@link #setResponseInfo(Long, Long, Long)} because
     * they save data in TheadLocal.
     *
     * @see PaginationInterceptor#afterCompletion(HttpServletRequest, HttpServletResponse, Object, Exception)
     */
    public static void clearContextData() {
        if (requestInfo.get() != null) {
            LOG.debug("clear request info in thread local.");
            requestInfo.remove();
        }

        if (responseInfo.get() != null) {
            LOG.debug("clear response info in thread local.");
            responseInfo.remove();
        }
    }

    private static QueryRequest parsePageRange(QueryRequest range, HttpServletRequest webRequest) {

        Enumeration<String> headers = webRequest.getHeaders("Range");

        if (headers == null || (!headers.hasMoreElements())) {
            return range;
        }

        String rangeStr = headers.nextElement();
        LOG.debug("rangeStr = {}", rangeStr);

        if (!rangeStr.startsWith("items=")) {
            return range;
        }

        StringTokenizer tokenizer = new StringTokenizer(rangeStr.substring("items=".length()), "-");

        if (tokenizer.hasMoreTokens()) {
            String startStr = tokenizer.nextToken();

            Long start = parseNum(startStr);

            if (start == null) {
                return range;
            }

            range.setStart(start);
        }

        if (tokenizer.hasMoreTokens()) {
            String startStr = tokenizer.nextToken();

            Long end = parseNum(startStr);

            if (end != null) {
                range.setEnd(end);
            }

        }

        LOG.debug("start={}, end={}", range.getStart(), range.getEnd());

        return range;
    }


    private static QueryRequest parseSortBy(QueryRequest request, HttpServletRequest webRequest) {
        Enumeration<String> headers = webRequest.getHeaders("SortBy");


        if (headers == null || (!headers.hasMoreElements())) {
            request.setSortCriteriaList(new ArrayList<>());
            return request;
        }

        String sortByStr = headers.nextElement();

        LOG.debug("parseSortBy(). sortByStr = {}", sortByStr);


        StringTokenizer tokenizer = new StringTokenizer(sortByStr, ",");

        List<SortCriteria> sortCriteriaList = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            String criteriaStr = tokenizer.nextToken();

            if (criteriaStr.length() <= 1) {
                LOG.error("invlaid sort critiera:{}", criteriaStr);
                continue;
            }

            SortCriteria sortCriteria = new SortCriteria();

            if (criteriaStr.startsWith(" ") || criteriaStr.startsWith("+")) {
                sortCriteria.setAsc(true);

            } else if (criteriaStr.startsWith("-")) {
                sortCriteria.setAsc(false);
            } else {
                LOG.warn("invlaid sort critiera:{}", criteriaStr);
                continue;
            }

            sortCriteria.setFieldName(criteriaStr.substring(1));

            LOG.debug("sortCriteria.fieldName = {}, asc = {}", sortCriteria.getFieldName(), sortCriteria.isAsc());

            sortCriteriaList.add(sortCriteria);
        }

        request.setSortCriteriaList(sortCriteriaList);

        return request;
    }


    private static QueryRequest parseFilterBy(QueryRequest request, HttpServletRequest webRequest) {
        Enumeration<String> headers = webRequest.getHeaders("FilterBy");

        if (headers == null || (!headers.hasMoreElements())) {
            request.setFilterCriteriaList(new ArrayList<>());
            return request;
        }

        String filterByStr = headers.nextElement();

        LOG.debug("parseFilterBy(). filterByStr = {}", filterByStr);

        StringTokenizer tokenizer = new StringTokenizer(filterByStr, ",");

        List<String> criteriaStrList = splitFilterString(filterByStr);

        List<FilterCriteria> filterCriteriaList = new ArrayList<>(criteriaStrList.size());

        for (String criteriaStr : criteriaStrList) {

            if (criteriaStr.length() <= 1) {
                LOG.warn("invlaid filter critiera:{}", criteriaStr);
                continue;
            }

            FilterCriteria filterCriteria = createFilterCriteria(criteriaStr);
            if(filterCriteria == null) {
                continue;
            }

            filterCriteriaList.add(filterCriteria);

        }

        request.setFilterCriteriaList(filterCriteriaList);

        return request;
    }

    static List<String> splitFilterString(String filterByStr) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isEmpty(filterByStr)) {
            return result;
        }
//        StringTokenizer tokenizer = new StringTokenizer(filterByStr, ",",true);
//
//
//        while (tokenizer.hasMoreTokens()) {
//            StringBuilder oneRecord = new StringBuilder();
//            String curToken = tokenizer.nextToken();
//
//            if (",".equals(curToken)) {
//                continue;
//            }
//
//            oneRecord.append(curToken);
//
//            while (tokenizer.hasMoreTokens()) {
//
//                String tmpToken = tokenizer.nextToken();
//
//                if (oneRecord.lastIndexOf("\\") == ( oneRecord.length() - 1 )) {
//                    oneRecord.append(tmpToken);
//                    continue;
//                }
//
//                if (oneRecord.lastIndexOf(",") == ( oneRecord.length() - 1 )) {
//                    if (",".equals(tmpToken)) {
//                        break;
//                    } else {
//                        oneRecord.append(tmpToken);
//                        continue;
//                    }
//                }
//                break;
//
//            }
//            if(oneRecord.length() > 0) {
//                result.add(oneRecord.toString());
//            }
//        }

        int start = 0;
        int end = 0;

        int totalLength = filterByStr.length();

        while (end < totalLength) {

            char curChar = filterByStr.charAt(end);

            if (isDelim(filterByStr, end)) {

                result.add(filterByStr.substring(start, end));
                end++;
                start = end;
                continue;
            }
            end++;
        }

        if (result.isEmpty()) {
            result.add(filterByStr);
        }

        return result;
    }

    static private boolean isDelim(String str, int index) {
        if (str.charAt(index) == ',') {
            if (index > 0 && str.charAt(index - 1) == '\\') {
                return false;
            }

            return true;
        }
        return false;
    }

    private static FilterCriteria createFilterCriteria(String criteriaStr) {
        FilterCriteria filterCriteria = new FilterCriteria();

        StringTokenizer tokenizer = new StringTokenizer(criteriaStr, "|");

        try {
            String fieldName = tokenizer.nextToken();

            if (StringUtils.isEmpty(fieldName)) {
                return null;
            }

            filterCriteria.setFiledName(fieldName);

            String filterTypeStr = tokenizer.nextToken();

            filterCriteria.setFilterOperator(convertFilterType(filterTypeStr));

            if (FilterOperator.IS_NOT_NULL == filterCriteria.getFilterOperator()
                || FilterOperator.IS_NULL == filterCriteria.getFilterOperator()) {

                return filterCriteria;
            }

            filterCriteria.setValue(criteriaStr.substring(fieldName.length() + filterTypeStr.length() + 2));

        } catch (NoSuchElementException exp) {
            LOG.warn("Invalid filter criteria string {}", criteriaStr);

            return null;
        }

        return filterCriteria;

    }



    private static FilterOperator convertFilterType(String filterTypeSymbol) {
        FilterOperator result = FilterOperator.getFilterOperatorBySymbol(filterTypeSymbol);

        if (result == null) {
            result = FilterOperator.IS_NULL;
        }

        return result;
    }

    /**
     * This is used to store the query raw data(the range of records that should be returned).
     * {@link PaginationContext#parseRestPaginationParameters()} extract query data from HTTP request objects and store in a object of this
     * class. When developers implement , they just need to get the
     * query raw data from this class but not HTTP request so that it will not coupled with any thing in controller
     * layer.
     */
    public static class QueryRequest {

        /**
         * This is used to specified the range of records should be returned by the query.
         * If start = 11 and end = 20, it means it is expected that this query should return records from
         * 11th record to 20th record.
         */
        private Long start;

        /**
         * This is used to specify the range of records should be returned by the query.
         *
         * @see QueryRequest#start
         */
        private Long end;

        /**
         * This is used to specify how sorting should be done.
         * Assume there are 2 records in this fields(record1 and record2). record1.isAsc = true,
         * record1.fieldName = "abc, record2.isAsc = false, record2.fieldName = "def". It means something like:
         * "SELECT ... FROM ... ORDER BY abc DESC, def ASC".
         */
        private List<SortCriteria> sortCriteriaList = new ArrayList<>();

        private List<FilterCriteria> filterCriteriaList = new ArrayList<>();

        public Long getStart() {
            return start;
        }

        public void setStart(Long start) {
            this.start = start;
        }

        public Long getEnd() {
            return end;
        }

        public void setEnd(Long end) {
            this.end = end;
        }


        public List<SortCriteria> getSortCriteriaList() {
            return sortCriteriaList;
        }

        public void setSortCriteriaList(List<SortCriteria> sortCriteriaList) {
            this.sortCriteriaList = sortCriteriaList;
        }

        public List<FilterCriteria> getFilterCriteriaList() {
            return filterCriteriaList;
        }

        public void setFilterCriteriaList(List<FilterCriteria> filterCriteriaList) {
            this.filterCriteriaList = filterCriteriaList;
        }

        @Override
        public String toString() {
            return "QueryRequest{"
                + "start=" + start
                + ", end=" + end
                + ", sortCriteriaList=" + sortCriteriaList
                + '}';
        }
    }

    /**
     * This is used by the {@link QueryRequest} to store information about sorting.
     */
    public static class SortCriteria {
        /**
         * Indicate sorting will be done with asc or desc. True means asc.
         */
        private boolean isAsc;

        /**
         * the field used for sorting.
         */
        private String fieldName;

        public boolean isAsc() {
            return isAsc;
        }

        public void setAsc(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String toString() {
            return "SortCriteria{"
                + "isAsc=" + isAsc
                + ", fieldName='" + fieldName + '\''
                + '}';
        }
    }

    /**
     * This is used by the {@link QueryRequest} to store information about filtering.
     */
    public static class FilterCriteria {
        private String filedName;

        private FilterOperator filterOperator;

        private String value;

        public String getFiledName() {
            return filedName;
        }

        public void setFiledName(String filedName) {
            this.filedName = filedName;
        }

        public FilterOperator getFilterOperator() {
            return filterOperator;
        }

        public void setFilterOperator(FilterOperator filterOperator) {
            this.filterOperator = filterOperator;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum FilterOperator {
        EQUALS("="),
        EQUALS_OR_LESS("<="),
        EQUALS_OR_LARGER(">="),
        NOT_EQUALS("<>"),
        CONTAINS("C"),
        IS_NULL("N"),
        IS_NOT_NULL("NN");

        private String symbol;

        private static Map<String, FilterOperator> FILTER_TYPE_MAP = new HashMap<>();

        static {
            FILTER_TYPE_MAP.put("=", FilterOperator.EQUALS);
            FILTER_TYPE_MAP.put("<=", FilterOperator.EQUALS_OR_LESS);
            FILTER_TYPE_MAP.put(">=", FilterOperator.EQUALS_OR_LARGER);
            FILTER_TYPE_MAP.put("<>", FilterOperator.NOT_EQUALS);
            FILTER_TYPE_MAP.put("C", FilterOperator.CONTAINS);
            FILTER_TYPE_MAP.put("N", FilterOperator.IS_NULL);
            FILTER_TYPE_MAP.put("NN", FilterOperator.IS_NOT_NULL);
        }

        private FilterOperator(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return this.symbol;
        }

        public static FilterOperator getFilterOperatorBySymbol(String symbol) {
            return FILTER_TYPE_MAP.get(symbol);
        }
    }

    /**
     * This is used to decouple the query result from HTTP response. Objects of this class are used to store raw data
     * of query result.  should return an instance of this class
     * and the  will use this object to
     * generate an response that can be returned by a Spring MVC controller method.
     */
    public static class QueryResponse {


        /**
         * {@link QueryRequest#start} and {@link QueryRequest#end} is used to specify the range of records the caller
         * want to retrieve. However, it is possible that caller want to retrieve records 100 to 110 but there is only
         * 104 records available in total. At this moment, the  should return
         * an object with {@link QueryResponse#start} = 100, {@link QueryResponse#total} = 104 and
         * include records from 100th record to 104th record.
         */
        private Long start;

        /**
         * @see QueryResponse#start
         */
        private Long end;

        /**
         * This is used to store the actual total number of available records for this query.
         *
         * @see QueryResponse#start
         */
        private Long total;

        public QueryResponse() {
            super();
        }

        /**
         * Constract QueryResponse with parameters.
         *
         * @param start start
         * @param end end
         * @param total total
         */
        public QueryResponse(Long start, Long end, Long total) {
            this.start = start;
            this.end = end;
            this.total = total;
        }

        public QueryResponse(Long start, Long end) {
            this.start = start;
            this.end = end;
        }

        public Long getStart() {
            return start;
        }

        public void setStart(Long start) {
            this.start = start;
        }

        public Long getEnd() {
            return end;
        }


        public Long getTotal() {
            return total;
        }


        public void setEnd(Long end) {
            this.end = end;
        }

        public void setTotal(Long total) {
            this.total = total;
        }
    }

}
