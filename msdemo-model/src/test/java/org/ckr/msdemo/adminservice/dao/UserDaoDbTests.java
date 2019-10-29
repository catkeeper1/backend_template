package org.ckr.msdemo.adminservice.dao;


import org.ckr.msdemo.adminservice.entity.User;
import org.ckr.msdemo.adminservice.repository.UserRepository;
import org.ckr.msdemo.utility.annotation.DbUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DbUnitTest
public class UserDaoDbTests {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testQueryUsersWithRoles_WithUserNameOnly() {
        User u = new User();
        u.setUserName("apple");
        u.setUserDescription("apple1");
        u.setPassword("123");
        this.userRepository.save(u);

        List<User> users = userDao.queryUserByUserName("apple");
        assertThat(users.size()).isEqualTo(1);
    }
}
