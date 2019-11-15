package org.ckr.msdemo.adminservice.form;

public class UserForm {
    private String userName;
    private String userDescription;
    private String password;

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

    @Override
    public String toString() {
        return "UserForm{" +
                "userName='" + userName + '\'' +
                ", userDescription='" + userDescription + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
