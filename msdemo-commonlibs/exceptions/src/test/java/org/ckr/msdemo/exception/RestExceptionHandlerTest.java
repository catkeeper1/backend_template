package org.ckr.msdemo.exception;

import org.ckr.msdemo.exception.util.RestExceptionHandler;
import org.junit.Test;

/**
 * Created by Administrator on 2017/7/9.
 */
public class RestExceptionHandlerTest {

    @Test
    public void testNothing() {
        RestExceptionHandler.getCause(new RuntimeException(), Throwable.class);
    }

}
