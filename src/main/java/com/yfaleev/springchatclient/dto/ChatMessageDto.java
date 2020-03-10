package com.yfaleev.springchatclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {
    public enum ChatMessageType {
        CHATTING,
        JOIN,
        LEAVE
    }

    private ChatMessageType messageType;

    private String messageText;

    private String sender;

    private String sendDate;

    public ChatMessageDto(String messageText) {
        this.messageText = messageText;
    }
}
