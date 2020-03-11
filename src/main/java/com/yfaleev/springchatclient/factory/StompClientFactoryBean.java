package com.yfaleev.springchatclient.factory;

import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Component
public class StompClientFactoryBean implements StompClientFactory {

    private final MessageConverter messageConverter;

    public StompClientFactoryBean(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    public WebSocketStompClient newClient() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(messageConverter);

        return stompClient;
    }
}
