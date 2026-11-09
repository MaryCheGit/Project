package com.example.shop.controller;

import com.example.shop.dto.AuthResponse;
import com.example.shop.dto.LoginRequest;
import com.example.shop.dto.RegisterRequest;
import com.example.shop.entity.User;
import com.example.shop.enums.Role;
import com.example.shop.repository.UserRepository;
import com.example.shop.security.CustomUserDetailsService;
import com.example.shop.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Пользователь с таким именем уже существует");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = jwtService.generateToken(userDetails);

        AuthResponse response = new AuthResponse(
                token,
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getId(),
                savedUser.getRole()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("=== ПОПЫТКА ВХОДА ===");
            System.out.println("Username: [" + request.getUsername() + "]");
            System.out.println("Password: [" + request.getPassword() + "]");

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            System.out.println("=== АУТЕНТИФИКАЦИЯ УСПЕШНА ===");

        } catch (Exception e) {
            System.out.println("=== ОШИБКА АУТЕНТИФИКАЦИИ ===");
            System.out.println("Тип ошибки: " + e.getClass().getName());
            System.out.println("Сообщение: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body("Неверное имя пользователя или пароль");
        }

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body("Пользователь не найден");
        }
        User user = userOpt.get();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);

        AuthResponse response = new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getId(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // ВРЕМЕННЫЙ ЭНДПОИНТ: генерация BCrypt-хеша
    // Удалите его после настройки пароля!
    // ============================================================
    @GetMapping("/generate-hash")
    public ResponseEntity<String> generateHash(@RequestParam String password) {
        String hash = passwordEncoder.encode(password);
        return ResponseEntity.ok(hash);
    }
}