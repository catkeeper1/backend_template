package org.ckr.msdemo.util;

import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * An util used to retrieve current transaction info.
 * Sometimes, some programs want to know the current transaction info. Such as, want to know
 * whether current transaction is READ ONLY. At this moment, programmer can call static
 * method in this class to do so. This class utilize the transaction info stored in a ThreadLocal private
 * field in TransactionAspectSupport. So, if there is no AOP transaction config, the methods in this
 * class cannot retrieve any transaction info.
 */
public class TransactionInfoHolder extends TransactionAspectSupport {

    /**
     * Return current transaction attribute.
     *
     * @return return null if there is no AOP transaction. Otherwise return current transaction attrivute.
     */
    public static final TransactionAttribute getTransactionAttribute() {
        TransactionInfo info = currentTransactionInfo();

        if(info == null) {
            return null;
        }

        return info.getTransactionAttribute();
    }
}
