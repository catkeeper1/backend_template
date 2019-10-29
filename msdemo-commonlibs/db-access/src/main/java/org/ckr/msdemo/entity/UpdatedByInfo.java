package org.ckr.msdemo.entity;

import java.sql.Timestamp;

/**
 * Entity that need to capture audit info when it is updated should implement this interface.
 * Please refer {@link AuditEntityListener} about how it is used.
 */
public interface UpdatedByInfo extends AuditInfo {

    /**
     * Used to set the user name of who update current entity.
     * @param updatedBy   The current user name.
     */
    void setUpdatedBy(String updatedBy);

    /**
     * Used to set the user description of who update current entity.
     * @param updatedByDesc   The current user description.
     */
    void setUpdatedByDesc(String updatedByDesc);

    /**
     * Used to set the timestamp when current entity is updated.
     * @param updatedAt    The current timestamp.
     */
    void setUpdatedAt(Timestamp updatedAt);

}
