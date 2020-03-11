package com.yfaleev.springchatclient.ui.handler;

import com.yfaleev.springchatclient.dto.ChatUsersNamesDto;
import com.yfaleev.springchatclient.ui.utils.UiUtils;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class ChatUserNamesMessageHandler extends StompSessionExceptionHandler {

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ChatUsersNamesDto.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        ChatUsersNamesDto usersNamesDto = (ChatUsersNamesDto) payload;
        UiUtils.printActiveUsers(usersNamesDto);
    }
}
