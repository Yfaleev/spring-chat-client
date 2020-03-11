package com.yfaleev.springchatclient.ui.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StompSessionExceptionHandler extends StompSessionHandlerAdapter {

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        handleException(exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        handleException(exception);
    }

    private void handleException(Throwable exception) {
        log.error(exception.getMessage(), exception);
        System.out.println("Exception occurred");
    }
}
