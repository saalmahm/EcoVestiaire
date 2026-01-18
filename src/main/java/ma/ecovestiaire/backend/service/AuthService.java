package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.RegisterRequest;
import ma.ecovestiaire.backend.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);
}