package org.ckr.msdemo.exception;


import org.junit.Test;
import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class ApplictionExceptionHelperTest {

    @Test
    public void addExceptionTest() {
        ApplictionExceptionHelper helper = new ApplictionExceptionHelper();


        helper.addExceptionMessage("11", "exception 11==11");



        helper.addExceptionMessage("12", "exception 12==12");


        helper.addExceptionMessage("13", "exception 13==13");

        try {
            helper.throwThisIfValid("test exception");
        } catch (ApplicationException e) {
            assertThat(e.getMessageList().size()).isEqualTo(3);
            return;
        }

        fail("It is expected ApplicationException is caught");


    }
}
