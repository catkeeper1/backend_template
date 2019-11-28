package org.ckr.msdemo.adminservice.controller;

import org.ckr.msdemo.adminservice.form.UserForm;
import org.ckr.msdemo.adminservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@RestController("user")
public class UserController {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public void createUser(@RequestBody UserForm form) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        UserSession userSession = new UserSession();
        userSession.setUserDescription(form.getUserDescription());
        userSession.setUserName(form.getUserName());

        HttpSession session = request.getSession();
        session.setAttribute("userSession", userSession);
        session.setMaxInactiveInterval(400);

        redisTemplate.opsForList().rightPush("userList", userSession);

    }

    @GetMapping("/query")
    @PreAuthorize("hasRole('ADMIN')")
    public UserSession getUser() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //return (UserSession) request.getSession().getAttribute("userSession");

        return (UserSession) redisTemplate.opsForList().leftPop("userList");
    }

    @PostMapping("/queryCache")
    public UserSession getUserFromCache(@RequestBody UserForm userForm) {
        System.out.println("call controller");

        return userService.getUserFromCache(userForm);
    }


    public static class UserSession implements Serializable {
        private String userName;
        private String userDescription;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserDescription() {
            return userDescription;
        }

        public void setUserDescription(String userDescription) {
            this.userDescription = userDescription;
        }
    }
}
