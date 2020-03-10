package com.yfaleev.springchatclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserRegistrationResponse {

    private boolean success;

    private List<String> errors = new ArrayList<>();

    public UserRegistrationResponse(boolean success) {
        this.success = success;
    }
}
