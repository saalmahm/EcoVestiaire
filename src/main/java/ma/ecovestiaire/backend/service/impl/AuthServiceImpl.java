package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.RegisterRequest;
import ma.ecovestiaire.backend.dto.RegisterResponse;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.enums.Role;
import ma.ecovestiaire.backend.enums.UserStatus;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un utilisateur avec cet email existe déjà"
            );
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);

        return new RegisterResponse(
                saved.getId(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail()
        );
    }
}