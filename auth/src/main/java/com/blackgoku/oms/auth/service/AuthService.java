package com.blackgoku.oms.auth.service;

import com.blackgoku.oms.auth.dto.response.AuthResponse;
import com.blackgoku.oms.auth.dto.request.LoginRequest;
import com.blackgoku.oms.auth.dto.request.RegisterRequest;
import com.blackgoku.oms.auth.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}