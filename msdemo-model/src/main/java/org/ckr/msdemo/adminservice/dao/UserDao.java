package org.ckr.msdemo.adminservice.dao;

import org.ckr.msdemo.adminservice.entity.User;
import org.ckr.msdemo.dao.BaseJpaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Repository
public class UserDao extends BaseJpaDao {

    @Autowired
    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<User> queryUserByUserName(String userName){
        Map<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(userName)) {
            params.put("userName", userName);
        }
        String sql = "select userName , userDescription , locked , password " +
                "from User " +
                "where " +
                "userName = :userName";
        Function<Object[],User> mapper = new Function<Object[], User>() {

            @Override
            public User apply(Object[] row) {

                User view = new User();
                view.setUserName((String) row[0]);
                view.setUserDescription((String) row[1]);
                view.setLocked(((Boolean) row[2]));
                view.setPassword((String) row[3]);
                return view;
            }
        };
        List<User> result = this.executeDynamicQuery(sql, params, mapper);
        return result;
    }

}
