package org.ckr.msdemo.repository;


import org.ckr.msdemo.dbaccesstest.repository.UserRepository;
import org.ckr.msdemo.utility.annotation.DbUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DbUnitTest
public class UserRepositoryDbTests {

    @Autowired
    private UserRepository userRepository;




    @Test
    public void testfindExistingByUserName() {
        this.userRepository.findAll();

    }



}