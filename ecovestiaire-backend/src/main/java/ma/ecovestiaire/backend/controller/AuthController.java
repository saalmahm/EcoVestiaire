package ma.ecovestiaire.backend.controller;
import jakarta.validation.Valid;
import ma.ecovestiaire.backend.dto.LoginRequest;
import ma.ecovestiaire.backend.dto.LoginResponse;
import ma.ecovestiaire.backend.dto.RegisterRequest;
import ma.ecovestiaire.backend.dto.RegisterResponse;
import ma.ecovestiaire.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RegisterResponse> register(
            @ModelAttribute RegisterRequest request) {

        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}