package org.ckr.msdemo.adminservice.dao;

import org.ckr.msdemo.dao.BaseJpaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class UserDao extends BaseJpaDao {

    @Autowired
    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
