package com.yfaleev.springchatclient.service.api;

import com.yfaleev.springchatclient.dto.UserRegistrationResponse;

public interface UserRegistrationService {

    UserRegistrationResponse registerUser(String userName, String password);

}
