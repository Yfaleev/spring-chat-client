package com.yfaleev.springchatclient.ui;

import com.yfaleev.springchatclient.dto.ChatMessageDto;
import com.yfaleev.springchatclient.dto.ChatMessageHistoryDto;
import com.yfaleev.springchatclient.dto.ChatUsersNamesDto;
import com.yfaleev.springchatclient.dto.UserRegistrationResponse;
import com.yfaleev.springchatclient.service.api.UserRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class ChatUi implements CommandLineRunner {

    private final UserRegistrationService userRegistrationService;

    private Scanner scanner = new Scanner(System.in);

    public ChatUi(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {

            System.out.println("---------------------------");
            System.out.println(String.format("Type '%s' to register a new user", InputCommand.REGISTER));
            System.out.println(String.format("Type '%s' to join chat", InputCommand.JOIN));
            System.out.println(String.format("Type '%s' to exit chat", InputCommand.EXIT));
            System.out.println("---------------------------");

            String input = scanner.nextLine();

            switch (input) {
                case InputCommand.REGISTER:
                    showUserRegistration();
                    break;
                case InputCommand.JOIN:
                    showChat();
                    break;
                case InputCommand.EXIT:
                    return;
                default:
                    System.out.println("Unknown command!");
            }
        }
    }

    private void displayChatMessage(ChatMessageDto chatMessage) {
        System.out.println(String.format("%s %s: %s", chatMessage.getSendDate(), chatMessage.getSender(), chatMessage.getMessageText()));
    }

    private void showUserRegistration() {
        System.out.println("Type username: ");
        String userName = scanner.nextLine();

        System.out.println("Type password: ");
        String password = scanner.nextLine();

        UserRegistrationResponse userRegistrationResponse = userRegistrationService.registerUser(userName, password);

        System.out.println("---------------------------");
        if (userRegistrationResponse.isSuccess()) {
            System.out.println("Successful registration!");
        } else {
            System.out.println("User registration failed!");
            List<String> errors = userRegistrationResponse.getErrors();
            if (!errors.isEmpty()) {
                System.out.println("Errors: ");
                errors.forEach(System.out::println);
            }
        }
        System.out.println("---------------------------");
    }

    private StompHeaders buildAuthHeaders(String username, String password) {
        StompHeaders authHeaders = new StompHeaders();
        authHeaders.add("login", username);
        authHeaders.add("password", password);
        return authHeaders;
    }

    private void showChat() {
        System.out.println("Type username: ");
        String username = scanner.nextLine();

        System.out.println("Type password: ");
        String password = scanner.nextLine();

        WebSocketClient client = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = null;
        StompHeaders authHeaders = buildAuthHeaders(username, password);

        try {
            stompSession = stompClient.connect("ws://localhost:8080/ws", new WebSocketHttpHeaders(), authHeaders, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    System.out.println("You are in chat!");

                    session.subscribe("/topic/public", new StompSessionHandlerAdapter() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return ChatMessageDto.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            displayChatMessage((ChatMessageDto) payload);
                        }
                    });

                    session.subscribe("/user/queue/activeUsers", new StompSessionHandlerAdapter() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return ChatUsersNamesDto.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            ChatUsersNamesDto message = (ChatUsersNamesDto) payload;
                            System.out.println("Active users:");
                            message.getUserNames().forEach(System.out::println);
                        }
                    });

                    session.subscribe("/user/queue/messageHistory", new StompSessionHandlerAdapter() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return ChatMessageHistoryDto.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            ChatMessageHistoryDto message = (ChatMessageHistoryDto) payload;
                            message.getChatMessages().forEach(ChatUi.this::displayChatMessage);
                        }
                    });

                    session.send("/app/chat.messageHistory", "");
                }


                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    log.error(exception.getMessage(), exception);
                    System.out.println("Exception occurred");
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    log.error(exception.getMessage(), exception);
                    System.out.println("Connection failed, try again");
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            return;
        }

        System.out.println("Type a message to communicate in chat");
        System.out.println(String.format("Type '%s' to show active chat users", InputCommand.ACTIVE_USERS));
        System.out.println(String.format("Type '%s' to exit chat", InputCommand.EXIT));

        while (true) {
            String input = scanner.nextLine();

            switch (input) {
                case InputCommand.ACTIVE_USERS:
                    stompSession.send("/app/chat.activeUsers", "");
                    break;
                case InputCommand.EXIT:
                    stompSession.disconnect();
                    return;
                default:
                    stompSession.send("/app/chat.sendMessage", new ChatMessageDto(input));
            }
        }
    }
}
