package com.yfaleev.springchatclient.factory;

import org.springframework.web.socket.messaging.WebSocketStompClient;

public interface StompClientFactory {

    WebSocketStompClient newClient();

}
