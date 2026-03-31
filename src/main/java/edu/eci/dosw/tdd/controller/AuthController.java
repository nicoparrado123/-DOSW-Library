package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.core.repository.UserRepositoryPort;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("credenciales invalidas"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("credenciales invalidas");
        }
        String token = jwtService.generateToken(user.getId(), user.getRole().name());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
