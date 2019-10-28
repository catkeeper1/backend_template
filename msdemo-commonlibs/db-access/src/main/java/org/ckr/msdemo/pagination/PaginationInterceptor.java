package org.ckr.msdemo.pagination;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Intercept HTTP request to parse pagination request info and clear context data after HTTP.
 * @see <a href="https://docs.spring.io/spring/docs/4.3.7.RELEASE/spring-framework-reference/htmlsingle/#mvc-handlermapping-interceptor">
 *       spring reference for interceptor
 *       </a>
 */
public class PaginationInterceptor implements HandlerInterceptor {

    /**
     * Call {@link PaginationContext#parseRestPaginationParameters()} to parse
     * the patination request info from HTTP request.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  handler
     * @return Always return true so that the HTTP request will be handled by other intercepter or controllers.
     * @throws Exception Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        PaginationContext.parseRestPaginationParameters();

        return true;
    }

    /**
     * Do nothing.
     *
     * <p>This method is <strong>NOT</strong> used to modify HTTP response header to return pagination response
     * info.
     *
     * <p>Please read
     * <a href="https://docs.spring.io/spring/docs/4.3.7.RELEASE/spring-framework-reference/htmlsingle/#mvc-handlermapping-interceptor">
     * the end of this section
     * </a> to understand about why this method cannot be used to modify HTTP response header to return pagination
     * response info.
     *
     * <p> For the reason above, {@link RestPaginationResponseAdvice} should be used to return pagination response info
     * to HTTP client.
     *
     * @param request      HttpServletRequest
     * @param response     HttpServletResponse
     * @param handler      handler
     * @param modelAndView modelAndView
     * @throws Exception Exception
     *
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    /**
     * Call {@link PaginationContext#clearContextData()} to make sure the data stored in
     * thread local in PaginationContext is cleared before the thread is released back to the pool.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  handler
     * @param ex       Exception
     * @throws Exception Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        PaginationContext.clearContextData();
    }
}
