package com.yfaleev.springchatclient.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class UserDto {

    private String userName;

    private String password;

    @JsonCreator
    public UserDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
