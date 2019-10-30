/**
 * Include classes for exception handling.
 *
 * <p>This package include below exceptions:
 * <ul>
 *     <li>{@link org.ckr.msdemo.exception.BaseException}: All exception defined in this project should extends this
 *     class.
 *     <li>{@link org.ckr.msdemo.exception.SystemException}: If an exception was caught and this exception is something
 *     cannot be handled by end user(such as DB is down). This exception should be wrapped with SystemException and
 *     thrown
 *     <li>{@link org.ckr.msdemo.exception.ApplicationException}: If want to show error messages to user and ask end
 *     user to do something, this exception should be thrown. Such as, a field cannot be empty. When this validation is
 *     failed, an ApplicationException with message "Field XXX cannot be empty" should be thrown.
 *     <li>{@link org.ckr.msdemo.exception.ReThrownSystemException}: This is used for cross service exception handling.
 *     If service A call service B and service B throw an SystemException, error info(please refer
 *     {@link org.ckr.msdemo.exception.util.RestExceptionHandler}) will be returned to service A. Then, a
 *     {@link org.ckr.msdemo.exception.ReThrownSystemException} should be thrown in service A so that the global
 *     exception handler will handle this exception.
 *     <li>{@link org.ckr.msdemo.exception.ReThrownApplicationException}: This is similar to
 *     {@link org.ckr.msdemo.exception.ReThrownSystemException}. The difference is this is used for the scenariio that
 *     some messages should be returned to end users.
 * </ul>
 *
 * This package also include {@link org.ckr.msdemo.exception.util.RestExceptionHandler} that is used to generate json
 * message when exception is thrown.
 *
 *
 */
package org.ckr.msdemo.exception;