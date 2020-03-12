package com.yfaleev.springchatclient.ui;

import com.yfaleev.springchatclient.dto.ChatMessageDto;
import com.yfaleev.springchatclient.dto.UserRegistrationResponse;
import com.yfaleev.springchatclient.factory.StompClientFactory;
import com.yfaleev.springchatclient.service.api.UserRegistrationService;
import com.yfaleev.springchatclient.ui.handler.ChatConnectionHandler;
import com.yfaleev.springchatclient.ui.handler.ChatUserNamesMessageHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static com.yfaleev.springchatclient.ChatApplicationPaths.*;

@Component
public class ChatUi implements CommandLineRunner {

    private final Scanner scanner;

    private final UserRegistrationService userRegistrationService;

    private final StompClientFactory stompClientFactory;

    private final ChatConnectionHandler chatConnectionHandler;
    private final ChatUserNamesMessageHandler chatUserNamesMessageHandler;

    public ChatUi(
            Scanner scanner,
            UserRegistrationService userRegistrationService,
            StompClientFactory stompClientFactory,
            ChatConnectionHandler chatConnectionHandler,
            ChatUserNamesMessageHandler chatUserNamesMessageHandler) {
        this.scanner = scanner;
        this.userRegistrationService = userRegistrationService;
        this.stompClientFactory = stompClientFactory;
        this.chatConnectionHandler = chatConnectionHandler;
        this.chatUserNamesMessageHandler = chatUserNamesMessageHandler;
    }


    @Override
    public void run(String... args) {
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


    private void showUserRegistration() {
        System.out.println("Type username: ");
        String userName = scanner.nextLine();

        System.out.println("Type password: ");
        String password = scanner.nextLine();

        UserRegistrationResponse userRegistrationResponse = userRegistrationService.registerUser(userName, password);

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
    }

    private void showChat() {
        System.out.println("Type username: ");
        String username = scanner.nextLine();

        System.out.println("Type password: ");
        String password = scanner.nextLine();

        WebSocketStompClient stompClient = stompClientFactory.newClient();
        StompHeaders authHeaders = buildAuthHeaders(username, password);

        StompSession stompSession;
        try {
            stompSession = stompClient.connect(
                    HANDSHAKE_URL,
                    new WebSocketHttpHeaders(),
                    authHeaders,
                    chatConnectionHandler
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Connection failed. Try again.");
            return;
        }

        System.out.println("You are in chat!");
        System.out.println("Type a message to communicate in chat");
        System.out.println(String.format("Type '%s' to show active chat users", InputCommand.ACTIVE_USERS));
        System.out.println(String.format("Type '%s' to go back to login", InputCommand.EXIT));

        while (true) {
            String input = scanner.nextLine();

            try {
                switch (input) {
                    case InputCommand.ACTIVE_USERS:
                        stompSession.subscribe(ACTIVE_USERS_DESTINATION, chatUserNamesMessageHandler);
                        break;
                    case InputCommand.EXIT:
                        stompSession.disconnect();
                        return;
                    default:
                        stompSession.send(CHAT_DESTINATION, new ChatMessageDto(input));
                }
            } catch (Exception e) {
                System.out.println("Exception occurred: " + e.getMessage());
                return;
            }
        }
    }

    private StompHeaders buildAuthHeaders(String username, String password) {
        StompHeaders authHeaders = new StompHeaders();
        authHeaders.add("login", username);
        authHeaders.add("password", password);
        return authHeaders;
    }
}
