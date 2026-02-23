package it.alessandrohan.pollsystem.web.controller.v1;

import it.alessandrohan.pollsystem.service.AuthService;
import it.alessandrohan.pollsystem.web.dto.request.LoginRequest;
import it.alessandrohan.pollsystem.web.dto.request.RegistrationRequest;
import it.alessandrohan.pollsystem.web.dto.response.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AuthController.BASE_URL)
public class AuthController {
    public static final String BASE_URL = "/rest/api/v0";

    @Autowired
    private AuthService authService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody RegistrationRequest request) {
        authService.registerUser(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse userLogin(@Valid @RequestBody LoginRequest request) {
        return authService.userLogin(request);
    }
}
