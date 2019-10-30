package org.ckr.msdemo.dbaccesstest.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity()
@Table(name = "DB_TEST_ROLE")
public class Role implements Serializable {

    private static final long serialVersionUID = 4939126105741432131L;

    private String roleCode;

    private String parentRoleCode;

    private String roleDescription;

    private Timestamp lastModifiedTimestamp;

    @Id
    @Column(name = "ROLE_CODE", unique = true, nullable = false, length = 100)
    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }


    @Column(name = "PARENT_ROLE_CODE")
    public String getParentRoleCode() {
        return parentRoleCode;
    }

    public void setParentRoleCode(String parentRoleCode) {
        this.parentRoleCode = parentRoleCode;
    }

    @Column(name = "ROLE_DESCRIPTION")
    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
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
