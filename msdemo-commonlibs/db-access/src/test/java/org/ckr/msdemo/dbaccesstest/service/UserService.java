package org.ckr.msdemo.dbaccesstest.service;

import org.ckr.msdemo.dbaccesstest.entity.UserWithRole;
import org.ckr.msdemo.pagination.JpaRestPaginationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by yukai.a.lin on 8/16/2017.
 */
@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    JpaRestPaginationService jpaRestPaginationService;

    /**
     * Query UsersWithRoles.
     *
     * @param userName userName
     * @param userDesc userDesc
     * @return List of UserWithRole
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public List<UserWithRole> queryUsersWithRoles(String userName, String userDesc) {
        Map<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(userName)) {
            params.put("userName", userName);
        }
        if (!StringUtils.isEmpty(userDesc)) {
            params.put("userDesc", "%" + userDesc + "%");
        }

        String queryStr = "select u.userName as userName, u.userDescription, u.locked, u.password , g.roleCode"
            + ", g.roleDescription from User u left join u.roles as g where 1=1 "
            + "/*userName| and u.userName = :userName */"
            + "/*userDesc| and u.userDescription like :userDesc */";

        Function<Object[], UserWithRole> mapper = new Function<Object[], UserWithRole>() {

            @Override
            public UserWithRole apply(Object[] row) {

                UserWithRole view = new UserWithRole();

                view.setUserName((String) row[0]);
                view.setUserDescription((String) row[1]);
                view.setLocked(((Boolean) row[2]));
                view.setPassword((String) row[3]);
                view.setRoleCode((String) row[4]);
                view.setRoleDescription((String) row[5]);

                return view;
            }


        };
        List<UserWithRole> result = jpaRestPaginationService.query(queryStr, params, mapper);

        LOG.debug("pagination query result {}", result);

        return result;
    }
}
