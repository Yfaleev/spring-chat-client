package com.yfaleev.springchatclient.ui.utils;

import com.yfaleev.springchatclient.dto.ChatMessageDto;
import com.yfaleev.springchatclient.dto.ChatMessageHistoryDto;
import com.yfaleev.springchatclient.dto.ChatUsersNamesDto;

public final class UiUtils {

    public static void displayChatMessage(ChatMessageDto chatMessage) {
        ChatMessageDto.ChatMessageType messageType = chatMessage.getMessageType();

        if (messageType != null) {
            switch (messageType) {
                case JOIN:
                case LEAVE:
                    System.out.println(String.format("%s: %s", chatMessage.getSendDate(), chatMessage.getMessageText()));
                    break;
                case CHATTING:
                default:
                    System.out.println(String.format("%s %s: %s", chatMessage.getSendDate(), chatMessage.getSender(), chatMessage.getMessageText()));
                    break;
            }
        }
    }

    public static void printActiveUsers(ChatUsersNamesDto chatUsersNamesDto) {
        System.out.println("Active users:");
        chatUsersNamesDto.getUserNames().forEach(System.out::println);
    }

    public static void printMessageHistory(ChatMessageHistoryDto chatMessageHistoryDto) {
        chatMessageHistoryDto.getChatMessages().forEach(UiUtils::displayChatMessage);
    }
}
