package it.alessandrohan.pollsystem.service;

import it.alessandrohan.pollsystem.web.dto.request.LoginRequest;
import it.alessandrohan.pollsystem.web.dto.request.RegistrationRequest;
import it.alessandrohan.pollsystem.web.dto.response.LoginResponse;

public interface AuthService {

    void registerUser(RegistrationRequest request);

    LoginResponse userLogin(LoginRequest request);
}
