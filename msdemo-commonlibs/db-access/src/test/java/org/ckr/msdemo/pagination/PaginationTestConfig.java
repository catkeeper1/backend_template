package org.ckr.msdemo.pagination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;


@Configuration
public class PaginationTestConfig {

    @Autowired
    EntityManager entityManager;

    /**
     * Inject entityManager into JpaRestPaginationService.
     *
     * @return JpaRestPaginationService
     */
    @Bean
    public JpaRestPaginationService loadJpaRestPaginationService() {
        JpaRestPaginationService result = new JpaRestPaginationService();
        result.setEntityManager(this.entityManager);

        return result;
    }
}


