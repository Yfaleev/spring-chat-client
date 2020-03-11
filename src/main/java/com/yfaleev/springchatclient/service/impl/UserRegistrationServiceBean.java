package com.yfaleev.springchatclient.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yfaleev.springchatclient.dto.ApiResponse;
import com.yfaleev.springchatclient.dto.UserDto;
import com.yfaleev.springchatclient.dto.UserRegistrationResponse;
import com.yfaleev.springchatclient.service.api.UserRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static com.yfaleev.springchatclient.ChatApplicationPaths.USER_REGISTRATION_URL;

@Service
@Slf4j
public class UserRegistrationServiceBean implements UserRegistrationService {

    private static final String SERVICE_UNAVAILABLE = "User registration service unavailable: ";

    private final RestTemplate restTemplate;

    public UserRegistrationServiceBean(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserRegistrationResponse registerUser(String userName, String password) {
        HttpEntity<UserDto> entity = new HttpEntity<>(new UserDto(userName, password));
        try {
            restTemplate.postForObject(USER_REGISTRATION_URL, entity, ApiResponse.class);
            return new UserRegistrationResponse(true);
        } catch (HttpStatusCodeException e) {
            log.error(e.getMessage(), e);

            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new UserRegistrationResponse(false, readErrors(e.getResponseBodyAsString()));
            } else {
                return new UserRegistrationResponse(false, Collections.singletonList(getExceptionError(e)));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            return new UserRegistrationResponse(false, Collections.singletonList(getExceptionError(e)));
        }
    }

    private String getExceptionError(Throwable ex) {
        return SERVICE_UNAVAILABLE + ex.getMessage();
    }

    private List<String> readErrors(String body) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            ApiResponse apiResponse = mapper.readValue(body, ApiResponse.class);
            return apiResponse.getErrors();
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
