package org.ckr.msdemo.entity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.sql.Timestamp;

/**
 * Entity listener that update audit trail field when an instance of entity is created or updated.
 *
 */
public class AuditEntityListener {

    /**
     * 0 parameters constructor.
     * 0 parameters constructor must be exist so that JPA entity manager can create an instance of this class
     * properly.
     */
    public AuditEntityListener() {
        super();
    }

    /**
     * Update audit trail fields when an instance of entity is created.
     * If entity implement {@link CreatedByInfo}, call {@link #setCreatedByInfo(CreatedByInfo, Timestamp)}.
     * If entity implement {@link UpdatedByInfo}, call {@link #setUpdatedByInfo(UpdatedByInfo, Timestamp)}.
     * @param entity     The instance of entity that is created.
     */
    @PrePersist
    public void callbackForCreate(AuditInfo entity) {

        Timestamp now = new Timestamp(System.currentTimeMillis());

        if(entity instanceof CreatedByInfo) {
            setCreatedByInfo((CreatedByInfo) entity, now);
        }

        if(entity instanceof UpdatedByInfo) {
            setUpdatedByInfo((UpdatedByInfo) entity, now);
        }

    }

    /**
     * Update the audit trail fields which are defined in {@link CreatedByInfo} of current entity.
     * @param createdByInfo     An entity that implement {@link CreatedByInfo}
     * @param now               Current timestamp. To make sure the the timestampe set to
     *                          {@link CreatedByInfo#setCreatedAt(Timestamp)} and
     *                          {@link UpdatedByInfo#setUpdatedAt(Timestamp)} is the same when an entity is created.
     *                          The timestamp "now" is
     *                          created in {@link #callbackForCreate} and pass it to
     *                          {@link #setCreatedByInfo(CreatedByInfo, Timestamp)}
     *                          {@link #setUpdatedByInfo(UpdatedByInfo, Timestamp)}. This is why this parameter is
     *                          defined.
     */
    protected void setCreatedByInfo(CreatedByInfo createdByInfo, Timestamp now) {

        createdByInfo.setCreatedAt(now);

    }

    /**
     * Update the audit trail fields which are defined in {@link UpdatedByInfo} of current entity.
     * @param updatedByInfo        An entity that implement {@link UpdatedByInfo}
     * @param now                  please refer {@link #setCreatedByInfo(CreatedByInfo, Timestamp)}
     */
    protected void setUpdatedByInfo(UpdatedByInfo updatedByInfo, Timestamp now) {

        updatedByInfo.setUpdatedAt(now);

    }


    /**
     * Update audit trail fields when an instance of entity is upated.
     * @param updatedByInfo          Current entity.
     */
    @PreUpdate
    public void callbackForUpdate(UpdatedByInfo updatedByInfo) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        setUpdatedByInfo((UpdatedByInfo) updatedByInfo, now);

    }
}
