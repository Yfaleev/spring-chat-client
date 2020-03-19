package com.yfaleev.springchatclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yfaleev.springchatclient.dto.ApiResponse;
import com.yfaleev.springchatclient.dto.UserRegistrationResponse;
import com.yfaleev.springchatclient.service.impl.UserRegistrationServiceBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Arrays;

import static com.yfaleev.springchatclient.ChatApplicationPaths.USER_REGISTRATION_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(UserRegistrationServiceBean.class)
public class UserRegistrationServiceTest {

    @Autowired
    private UserRegistrationServiceBean userRegistrationService;

    @Autowired
    private MockRestServiceServer server;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void whenUserRegistrationSucceed_thenReturnSuccessResponce() throws Exception {
        ApiResponse successApiResponse = new ApiResponse(true);

        server.expect(requestTo(USER_REGISTRATION_URL))
                .andRespond(withSuccess(objectMapper.writeValueAsString(successApiResponse), MediaType.APPLICATION_JSON));

        UserRegistrationResponse userRegistrationResponse = userRegistrationService.registerUser("username", "password");

        assertThat(userRegistrationResponse.isSuccess()).isTrue();
        assertThat(userRegistrationResponse.getErrors()).isEmpty();
    }

    @Test
    public void whenUserRegistrationFailed_thenReturnResponseWithErrors() throws Exception {
        ApiResponse failApiResponse = new ApiResponse(false, Arrays.asList("error1", "error2"));

        server.expect(requestTo(USER_REGISTRATION_URL))
                .andRespond(withBadRequest().body(objectMapper.writeValueAsBytes(failApiResponse)).contentType(MediaType.APPLICATION_JSON));

        UserRegistrationResponse userRegistrationResponse = userRegistrationService.registerUser("username", "password");

        assertThat(userRegistrationResponse.isSuccess()).isFalse();
        assertThat(userRegistrationResponse.getErrors()).isEqualTo(failApiResponse.getErrors());
    }

    @Test
    public void whenApiRequestFailed_thenReturnResponseWithErrors() throws Exception {
        server.expect(requestTo(USER_REGISTRATION_URL))
                .andRespond(withServerError());

        UserRegistrationResponse userRegistrationResponse = userRegistrationService.registerUser("username", "password");

        assertThat(userRegistrationResponse.isSuccess()).isFalse();
        assertThat(userRegistrationResponse.getErrors()).hasSize(1);
    }
}
