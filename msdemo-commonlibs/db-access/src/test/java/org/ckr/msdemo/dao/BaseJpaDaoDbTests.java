package org.ckr.msdemo.dao;

import org.ckr.msdemo.dbaccesstest.dao.UserDao;
import org.ckr.msdemo.dbaccesstest.entity.UserWithRole;
import org.ckr.msdemo.utility.annotation.DbUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DbUnitTest
public class BaseJpaDaoDbTests {

    @Autowired
    private UserDao userDao;

    @Test
    public void testExecuteDynamicQuery() {

        List<UserWithRole> resultList = userDao.queryUsersWithRoles("ABC", "");

        assertThat(resultList.size()).isEqualTo(3);
    }
}
