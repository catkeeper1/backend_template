package org.ckr.msdemo.entity;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The base class for other JPA entity.
 *
 * <p> Most of the JPA entity class should use this class as parent class. When this class is used as parent class, the
 * entity class will include below features:
 * <ul>
 *     <li> The entity class will have fields for audit trail. Such as who create/update this entity...
 *     <li> The entity class will be managed by {@link AuditEntityListener}. That means, when an instance of entity
 *     class is create or updated, the audit trail fields will be updated automatically.
 *     <li> The entity class will has a field for version control about optimistic lock. This field will be updated
 *     by JPS automatically when an entity instance is created/updated.
 * </ul>
 * If an entity class do not want features mentioned above for whatever reason, that entity class should not extend
 * this class. If any entity class just want features mentioned above partially, developer should refer the source
 * of this class an create another version of this class base on what is needed.
 *
 */
@MappedSuperclass
@EntityListeners(value = AuditEntityListener.class)
public abstract class BaseEntity implements CreatedByInfo, UpdatedByInfo, Serializable {

    private Timestamp createdAt;
    private String createdBy;
    private String createdByName;

    private Timestamp updatedAt;
    private String updatedBy;
    private String updatedByName;

    private Long versionNo;

    /**
     * @see CreatedByInfo#setCreatedAt(Timestamp)
     */
    @Column(name = "CREATED_AT")
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @see CreatedByInfo#setCreatedBy(String)
     *
     */
    @Column(name = "CREATED_BY", length = 200)
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     *
     * @see CreatedByInfo#setCreatedByDesc(String)
     */
    @Column(name = "CREATED_BY_DESC", length = 200)
    public String getCreatedByDesc() {
        return createdByName;
    }

    @Override
    public void setCreatedByDesc(String createdByName) {
        this.createdByName = createdByName;
    }

    @Column(name = "UPDATED_AT")
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Column(name = "UPDATED_BY")
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "UPDATED_BY_DESC")
    public String getUpdatedByDesc() {
        return updatedByName;
    }

    @Override
    public void setUpdatedByDesc(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    @Version
    @Column(name = "VERSION_NO")
    public Long getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("createdAt", createdAt)
                .add("createdBy", createdBy)
                .add("createdByName", createdByName)
                .add("updatedAt", updatedAt)
                .add("updatedBy", updatedBy)
                .add("updatedByName", updatedByName)
                .add("versionNo", versionNo)
                .toString();
    }
}
