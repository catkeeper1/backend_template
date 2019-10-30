package org.ckr.msdemo.adminservice.entity;

import org.ckr.msdemo.entity.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Administrator on 2017/11/5.
 */
@Entity()
@Table(name = "USER_TO_USER_ROLE_MAP",
        indexes = {@Index(name = "USER_TO_USER_ROLE_INDEX_1", columnList = "USER_NAME ASC", unique = false),
                @Index(name = "USER_TO_USER_ROLE_INDEX_2", columnList = "ROLE_CODE", unique = false)})
public class UserToUserRoleMap extends BaseEntity {

    private UserToUserRoleMapKey primaryKey;

    private User user;

    private UserRole userRole;

    @EmbeddedId
    public UserToUserRoleMapKey getPk() {
        return primaryKey;
    }

    public void setPk(UserToUserRoleMapKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USER_NAME", insertable = false, updatable = false)
    public User getUser() {
        return user;
    }


    @ManyToOne(optional = false)
    @JoinColumn(name = "ROLE_CODE", insertable = false, updatable = false)
    public UserRole getUserRole() {
        return userRole;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Embeddable
    public static class UserToUserRoleMapKey implements Serializable {

        private String userName;

        private String roleCode;


        @Column(name = "USER_NAME", unique = true, nullable = false, length = 100)
        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }


        @Column(name = "ROLE_CODE", unique = true, nullable = false, length = 100)
        public String getRoleCode() {
            return roleCode;
        }

        public void setRoleCode(String roleCode) {
            this.roleCode = roleCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            UserToUserRoleMapKey that = (UserToUserRoleMapKey) obj;
            return Objects.equals(userName, that.userName)
                   && Objects.equals(roleCode, that.roleCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userName, roleCode);
        }
    }
}
