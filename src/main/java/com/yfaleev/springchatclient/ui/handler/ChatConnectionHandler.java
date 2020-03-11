package com.yfaleev.springchatclient.ui.handler;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import static com.yfaleev.springchatclient.ChatApplicationPaths.CHAT_BROKER;
import static com.yfaleev.springchatclient.ChatApplicationPaths.MESSAGE_HISTORY_DESTINATION;

@Component
public class ChatConnectionHandler extends StompSessionHandlerAdapter {

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
}
