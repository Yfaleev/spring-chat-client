package com.yfaleev.springchatclient.ui.handler;

import com.yfaleev.springchatclient.dto.ChatMessageDto;
import com.yfaleev.springchatclient.ui.utils.ConsoleUiUtils;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class ChatMessageHandler extends StompSessionHandlerAdapter {

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ChatMessageDto.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        ChatMessageDto messageDto = (ChatMessageDto) payload;
        ConsoleUiUtils.displayChatMessage(messageDto);
    }
}
