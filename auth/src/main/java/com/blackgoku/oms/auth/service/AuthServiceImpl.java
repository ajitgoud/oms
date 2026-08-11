package com.blackgoku.oms.auth.service.impl;

import com.blackgoku.oms.auth.dto.response.AuthResponse;
import com.blackgoku.oms.auth.dto.request.LoginRequest;
import com.blackgoku.oms.auth.dto.request.RegisterRequest;
import com.blackgoku.oms.auth.dto.response.RegisterResponse;
import com.blackgoku.oms.auth.entity.Role;
import com.blackgoku.oms.auth.entity.User;
import com.blackgoku.oms.auth.exception.AuthAlreadyExistsException;
import com.blackgoku.oms.auth.exception.InvalidCredentialsException;
import com.blackgoku.oms.auth.repository.UserRepository;
import com.blackgoku.oms.auth.security.JwtService;
import com.blackgoku.oms.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new AuthAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new AuthAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        try {
            User savedUser = userRepository.save(user);
            return RegisterResponse.builder()
                    .id(savedUser.getId())
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .role(savedUser.getRole().name())
                    .message("User registered successfully")
                    .build();

        } catch (DataIntegrityViolationException ex) {
            throw new AuthAlreadyExistsException("Username or email already exists");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid username or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new InvalidCredentialsException(
                    "Invalid username or password"
            );
        }

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .username(user.getUsername())
                .role(user.getRole().name())
                .token(token)
                .build();
    }
}