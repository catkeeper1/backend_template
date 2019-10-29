package org.ckr.msdemo.adminservice.repository;

import org.ckr.msdemo.adminservice.entity.User;
import org.ckr.msdemo.adminservice.repository.UserRepository;
import org.ckr.msdemo.utility.annotation.DbUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DbUnitTest
public class UserRepositoryDbTests {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void testfindExistingByUserName() {
        User u = new User();
        u.setUserName("apple");
        u.setUserDescription("apple1");
        u.setPassword("123");
        this.userRepository.save(u);

        User user = this.userRepository.findByUserName("apple");
        assertThat(user.getUserName()).isEqualTo("apple");
    }

}
