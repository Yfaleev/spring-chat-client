package com.yfaleev.springchatclient.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yfaleev.springchatclient.dto.UserDto;
import com.yfaleev.springchatclient.dto.UserRegistrationResponse;
import com.yfaleev.springchatclient.service.api.UserRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final RestTemplate restTemplate;

    private static final String REGISTRATION_URL = "http://localhost:8080/api/users";

    public UserRegistrationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserRegistrationResponse registerUser(String userName, String password) {
        HttpEntity<UserDto> entity = new HttpEntity<>(new UserDto(userName, password));
        try {
            restTemplate.postForLocation(REGISTRATION_URL, entity);
            return new UserRegistrationResponse(true);
        } catch (HttpStatusCodeException e) {
            log.error(e.getMessage(), e);

            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new UserRegistrationResponse(false, readErrors(e.getResponseBodyAsString()));
            } else {
                return new UserRegistrationResponse(false);
            }
        }
    }

    private List<String> readErrors(String body) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(body, List.class); // todo
        } catch (JsonProcessingException ex) {
            throw new RuntimeException();
        }
    }
}
