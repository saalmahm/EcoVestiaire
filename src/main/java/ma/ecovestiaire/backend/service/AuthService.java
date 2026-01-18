package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.LoginRequest;
import ma.ecovestiaire.backend.dto.LoginResponse;
import ma.ecovestiaire.backend.dto.RegisterRequest;
import ma.ecovestiaire.backend.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}