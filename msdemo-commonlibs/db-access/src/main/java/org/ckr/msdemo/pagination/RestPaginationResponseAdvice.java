package org.ckr.msdemo.pagination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Modify response header to add pagination response info before HTTP response is returned to HTTP client.
 * See: {@link PaginationContext#setRestPaginationResponse(ServerHttpResponse)}
 */
@RestControllerAdvice()
public class RestPaginationResponseAdvice implements ResponseBodyAdvice<Object> {

    private static Logger LOG = LoggerFactory.getLogger(RestPaginationResponseAdvice.class);

    /**
     * Indicate whether the
     * {@link #beforeBodyWrite(Object, MethodParameter, MediaType, Class, ServerHttpRequest, ServerHttpResponse)}
     * method should be called for a HTTP response.
     * @param returnType
     * @param converterType
     * @return Return true if {@link PaginationContext#getQueryResponse()} is not null. Else, return false.
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return PaginationContext.getQueryResponse() != null;
    }

    /**
     * Call {@link PaginationContext#setRestPaginationResponse(ServerHttpResponse)} to modify HTTP response header
     * to return pagination response info to HTTP client.
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return just return parameter body.
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        PaginationContext.setRestPaginationResponse(response);

        return body;
    }
}
