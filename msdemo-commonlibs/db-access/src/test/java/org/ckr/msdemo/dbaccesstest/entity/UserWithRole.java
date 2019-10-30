package org.ckr.msdemo.dbaccesstest.entity;

import java.io.Serializable;

/**
 * Created by yukai.a.lin on 8/15/2017.
 */
public class UserWithRole implements Serializable {
    private static final long serialVersionUID = -3615936503981341238L;
    private String userName;
    private String userDescription;
    private String password;
    private Boolean locked;
    private String roleCode;
    private String roleDescription;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }


    @Override
    public String toString() {
        return "UserWithRole{"
            + "userName='" + userName + '\''
            + ", userDescription='" + userDescription + '\''
            + ", password='" + password + '\''
            + ", locked=" + locked
            + ", roleCode='" + roleCode + '\''
            + ", roleDescription='" + roleDescription + '\''
            + '}';
    }
}
