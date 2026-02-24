package it.alessandrohan.pollsystem.service;

import it.alessandrohan.pollsystem.mapper.UserMapper;
import it.alessandrohan.pollsystem.model.User;
import it.alessandrohan.pollsystem.repository.UserRepository;
import it.alessandrohan.pollsystem.security.JwtService;
import it.alessandrohan.pollsystem.web.dto.request.LoginRequest;
import it.alessandrohan.pollsystem.web.dto.request.RegistrationRequest;
import it.alessandrohan.pollsystem.web.dto.response.LoginResponse;
import it.alessandrohan.pollsystem.web.exception.DuplicateResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtService jwtService;

    @Override
    public void registerUser(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("email already exists");
        }

        User user = userMapper.userCreateReqToUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Override
    public LoginResponse userLogin(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Username or password not valid"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Username or password not valid");
        }

        return jwtService.generateToken(user);
    }
}
