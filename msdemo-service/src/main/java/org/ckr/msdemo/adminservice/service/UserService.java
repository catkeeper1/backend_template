package org.ckr.msdemo.adminservice.service;

import org.ckr.msdemo.adminservice.controller.UserController;
import org.ckr.msdemo.adminservice.form.UserForm;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Cacheable("userService")
    public UserController.UserSession getUserFromCache(UserForm form) {

        System.out.println("call service");

        UserController.UserSession userSession = new UserController.UserSession();
        userSession.setUserDescription(form.getUserDescription());
        userSession.setUserName(form.getUserName());

        return userSession;

    }

}
