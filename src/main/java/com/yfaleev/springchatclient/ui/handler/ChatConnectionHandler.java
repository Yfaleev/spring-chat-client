package com.yfaleev.springchatclient.ui.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;

import static com.yfaleev.springchatclient.ChatApplicationPaths.*;

@Component
@Slf4j
public class ChatConnectionHandler extends StompSessionExceptionHandler {

    private final ChatMessageHandler chatMessageHandler;
    private final ChatMessageHistoryHandler chatMessageHistoryHandler;

    public ChatConnectionHandler(ChatMessageHandler chatMessageHandler, ChatUserNamesMessageHandler chatUserNamesMessageHandler, ChatMessageHistoryHandler chatMessageHistoryHandler) {
        this.chatMessageHandler = chatMessageHandler;
        this.chatMessageHistoryHandler = chatMessageHistoryHandler;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("You are in chat!");

        session.subscribe(CHAT_BROKER, chatMessageHandler);
        session.subscribe(MESSAGE_HISTORY_DESTINATION, chatMessageHistoryHandler);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error(exception.getMessage(), exception);
        System.out.println("Connection failed. Try again.");
    }
}
