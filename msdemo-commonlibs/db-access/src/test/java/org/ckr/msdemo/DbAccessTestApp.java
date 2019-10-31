package org.ckr.msdemo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

/**
 * This class is only used to init spring boot container for DAO unit test case.
 * Annotation SpringBootApplication is needed for spring container initialization.
 */
@SpringBootApplication(scanBasePackages = {"org.ckr.msdemo.pagination","org.ckr.msdemo.dbaccesstest"})
@Import(DbAccessTestApp.RegisterDaoConfig.class)
public class DbAccessTestApp {

    @Configuration
    @ComponentScan(useDefaultFilters = false,
            includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class)
            })
    public static class RegisterDaoConfig {
    }

}


