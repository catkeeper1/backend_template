package org.ckr.msdemo.exception;

import com.google.common.base.Joiner;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;

/**
 * This exception should be used when an user message should be shown to end users to explain what action should be
 * taken. For example if an mandatory is not input, program can throw an instance of this class and include an message
 * to prompt use to key in that mandatory field.
 *
 * <p>When a validation failed and developer want to prompt error message to end user, please throw exception as below:
 * <pre>
 *     <code>
 *     throw new ApplicationException("User '" + userName + "' is not exist.")
 *               .addMessage("security.maintain_user.not_existing_user", userName);
 *
 *     </code>
 * </pre>
 * The "security.maintain_user.not_existing_user" is a property code that can be used to load the
 * real message from {@link MessageSource}. Assume the error message in properties files are defined as below:<br>
 * "security.maintain_user.not_existing_user=The user {0} is not exist." Then, userName is an parameter value that
 * will replace {0}.
 *
 *
 *
 */

public class ApplicationException extends BaseException {

    private static final long serialVersionUID = 1799296168836812569L;

    private final List<ExceptionMessage> messageList;


    protected ApplicationException() {
        super();
        this.messageList = new ArrayList<>();
    }

    protected ApplicationException(String shortDescription, boolean generateExpId) {
        super(shortDescription, generateExpId);
        this.messageList = new ArrayList<>();
    }




    public ApplicationException(String arg0 ,  List<ExceptionMessage> messagesList) {
        super(arg0);
        this.messageList = messagesList;


    }

    /**
     * Retrieve all messages that should be displayed to end users.
     * @return a list of {@link ExceptionMessage} that store the exception message info.
     */
    public List<ExceptionMessage> getMessageList() {
        List<ExceptionMessage> result = new ArrayList<>();
        result.addAll(messageList);

        return result;
    }

    @Override
    public String getMessage() {
        StringBuilder result = new StringBuilder( 1024);

        for (ExceptionMessage msg : messageList) {
            result.append("message code: ");
            result.append(msg.getMessageCode());
            result.append(".");

            Object[] msgParams = msg.getMessageParams();

            if (msgParams.length > 0) {
                result.append(" message parameters: [");
                Joiner.on(',').appendTo(result, msgParams);
                result.append("]");
            }

            result.append("\r\n");

            if (msg.getStacks() != null) {
                result.append("exception is thrown at:\r\n");

                //print 5 line only to tell where this exception is thrown.
                for (int ind = 0 ; ind < 5 && ind < msg.getStacks().length; ind++) {
                    result.append("\tat ");
                    result.append(msg.getStacks()[ind]);
                    result.append("\r\n");
                }
            }
        }


        result.append(super.getMessage());

        return result.toString();
    }

}
