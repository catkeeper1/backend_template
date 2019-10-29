package org.ckr.msdemo.utility.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented

public @interface MockedTest {
}

