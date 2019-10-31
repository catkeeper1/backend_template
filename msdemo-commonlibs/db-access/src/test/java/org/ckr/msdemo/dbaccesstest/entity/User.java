package org.ckr.msdemo.dbaccesstest.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * This table store user info.
 */
@Entity()
@Table(name = "DB_TEST_USER",
    indexes = {@Index(name = "user_index_1", columnList = "USER_DESCRIPTION ASC ,IS_LOCKED DESC", unique = true),
        @Index(name = "user_index_2", columnList = "IS_LOCKED", unique = false)})
public class User implements Serializable {

    public User() {
        super();
    }

    /**
     * Constract user with user name and user description.
     *
     * @param userName userName
     * @param userDescription userDescription
     */
    public User(String userName, String userDescription) {
        super();
        this.userName = userName;
        this.userDescription = userDescription;
    }

    private static final long serialVersionUID = 7028458717583173058L;


    private String userName;


    private String userDescription;


    private String password;


    private Boolean locked;


    private List<Role> roles;


    private Timestamp lastModifiedTimestamp;

    /**
     * The unique ID of a user.
     */
    @Id
    @Column(name = "USER_NAME", unique = true, nullable = false, length = 100)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "PASSWORD")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "USER_DESCRIPTION", length = 200)
    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    @Column(name = "IS_LOCKED")
    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "DB_TEST_USER_ROLE",
        joinColumns = {@JoinColumn(name = "USER_NAME")},
        inverseJoinColumns = {@JoinColumn(name = "ROLE_CODE")})
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Version
    @Column(name = "LAST_MODIFIED_TIMESTAMP")
    public Timestamp getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(Timestamp lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

}
