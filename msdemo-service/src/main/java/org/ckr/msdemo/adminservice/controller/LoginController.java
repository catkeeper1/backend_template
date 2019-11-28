package org.ckr.msdemo.adminservice.controller;

import io.swagger.annotations.ApiOperation;
import org.ckr.msdemo.exception.ApplicationException;
import org.ckr.msdemo.exception.ApplictionExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;

import static org.ckr.msdemo.exception.ApplictionExceptionHelper.createExpHelper;

@RestController
public class LoginController {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @PostMapping(value = "/login")
    public String login(@RequestParam("memberToken") String memberToken, HttpServletRequest req) {
        //have to invalid previous session. otherwise, it is possible to cause hijack session attack.
        req.getSession().invalidate();

        req.getSession().setMaxInactiveInterval(300);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(memberToken);

        Authentication authentication = new CustomizedAuthentication(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "Success";
    }

    /**
     * Generate response for the requests that are declined.
     * Just used to generate response in case the request is declined by spring security framework.
     * The spring security framework will forward request to this method when the request is declined.
     * It is not expected that this end-point is called by the frontend directly. It should not be shown
     * in the swagger UI as well.
     */
    /*@ApiOperation(value = "accessDeclined", hidden = true)
    @RequestMapping(value = "/accessDeclined")
    public void accessDeclined() {
        createExpHelper()
                .addExceptionMessage("org.ckr.msdemo.exception")
                .throwThisIfValid("Access declined");
    }*/

    @Service
    public static class CustomUserDetailsService implements UserDetailsService {

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            System.out.println("user name = " + username);


            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");

            Collection<GrantedAuthority> roles = new ArrayList<>();
            roles.add(authority);

            User user = new User(username, "", roles);

            return user;

        }
    }

    public static class CustomizedAuthentication extends AbstractAuthenticationToken {


        public CustomizedAuthentication(UserDetails userDetails) {
            super(userDetails.getAuthorities());
            this.setDetails(userDetails);

            this.setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return getDetails();
        }




    }
}
