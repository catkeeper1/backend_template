package org.ckr.msdemo.pagination;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Enable pagination feature in an application.
 *
 * <p>To enable pagination feature, please place this annotation in any java configuration class as below:
 * <pre>
 *     <code>
 *         &#064;EnablePagination
 *         public class PaginationConfig {
 *         ...
 *         }
 *     </code>
 * </pre>
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Configuration
@ComponentScan(value = "org.ckr.msdemo.pagination")
public @interface EnablePagination {
}
