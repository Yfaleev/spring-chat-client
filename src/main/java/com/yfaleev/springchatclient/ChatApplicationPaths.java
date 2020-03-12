package com.yfaleev.springchatclient;

public class ChatApplicationPaths {

    public static final String DESTINATION_PREFIX = "/app";

    public static final String HOST = "localhost:8080";

    public static final String HANDSHAKE_URL = String.format("ws://%s/ws", HOST);

    public static final String ACTIVE_USERS_DESTINATION = DESTINATION_PREFIX + "/chat.activeUsers";
    public static final String MESSAGE_HISTORY_DESTINATION = DESTINATION_PREFIX + "/chat.messageHistory";

    public static final String CHAT_BROKER = "/topic/public";
    public static final String CHAT_DESTINATION = DESTINATION_PREFIX + "/chat.sendMessage";

    public static final String USER_REGISTRATION_URL = String.format("http://%s/users", HOST);
}
