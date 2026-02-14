package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.LoginRequest;
import ma.ecovestiaire.backend.dto.LoginResponse;
import ma.ecovestiaire.backend.dto.RegisterRequest;
import ma.ecovestiaire.backend.dto.RegisterResponse;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.enums.Role;
import ma.ecovestiaire.backend.enums.UserStatus;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.security.JwtService;
import ma.ecovestiaire.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String uploadDir = "uploads/profiles/";

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un utilisateur avec cet email existe déjà"
            );
        }

        String profilePhotoUrl = null;
        if (request.getProfilePicture() != null && !request.getProfilePicture().isEmpty()) {
            profilePhotoUrl = saveProfilePicture(request.getProfilePicture());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePhotoUrl(profilePhotoUrl)
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

    private String saveProfilePicture(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), path);
            return "/api/uploads/profiles/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not save profile picture", e);
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Email ou mot de passe invalide"
                ));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Email ou mot de passe invalide"
            );
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}