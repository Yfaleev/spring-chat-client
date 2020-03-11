package com.yfaleev.springchatclient.ui.handler;

import com.yfaleev.springchatclient.dto.ChatMessageDto;
import com.yfaleev.springchatclient.ui.utils.UiUtils;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class ChatMessageHandler extends StompSessionExceptionHandler {

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ChatMessageDto.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        ChatMessageDto messageDto = (ChatMessageDto) payload;
        UiUtils.displayChatMessage(messageDto);
    }
}
