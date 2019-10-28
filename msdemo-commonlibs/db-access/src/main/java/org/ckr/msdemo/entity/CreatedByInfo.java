package org.ckr.msdemo.entity;

import java.sql.Timestamp;

/**
 * Entity that need to capture audit info when it is created should implement this interface.
 * Please refer {@link AuditEntityListener} about how it is used.
 */
public interface CreatedByInfo extends AuditInfo {

    /**
     * Used to set the user name of who create current entity.
     * @param createdBy   The current user name.
     */
    void setCreatedBy(String createdBy);

    /**
     * Used to set the user description of who create current entity.
     * @param createdByDesc   The current user description.
     */
    void setCreatedByDesc(String createdByDesc);

    /**
     * Used to set the timestamp when current entity is created.
     * @param createdAt    The current timestamp.
     */
    void setCreatedAt(Timestamp createdAt);

}
