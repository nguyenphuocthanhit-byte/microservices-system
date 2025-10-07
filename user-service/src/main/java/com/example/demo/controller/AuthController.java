package com.example.demo.controller;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error","username exists"));
        }
        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(Set.of(Role.ROLE_USER))
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("msg","registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        Set<String> roles = principal.getAuthorities().stream().map(a->a.getAuthority()).collect(Collectors.toSet());
        String accessToken = jwtUtil.generateToken(principal.getUsername(), roles);

        RefreshToken rt = RefreshToken.builder()
                .token(jwtUtil.generateRefreshToken())
                .user(userRepository.findByUsername(principal.getUsername()).get())
                .expiryDate(Instant.now().plusMillis(Long.parseLong(System.getProperty("jwt.refresh", "1209600000"))))
                .build();
        refreshTokenRepository.save(rt);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", rt.getToken()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String,String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null) return ResponseEntity.badRequest().body(Map.of("error","refreshToken required"));
        return refreshTokenRepository.findByToken(refreshToken)
                .map(rt -> {
                    if (rt.getExpiryDate().isBefore(Instant.now())) {
                        refreshTokenRepository.delete(rt);
                        return ResponseEntity.status(403).body(Map.of("error","refresh token expired")); 
                    }
                    User user = rt.getUser();
                    Set<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
                    String accessToken = jwtUtil.generateToken(user.getUsername(), roles);
                    return ResponseEntity.ok(Map.of("accessToken", accessToken));
                })
                .orElse(ResponseEntity.status(403).body(Map.of("error","invalid refresh token")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        if (username==null) return ResponseEntity.badRequest().body(Map.of("error","username required"));
        userRepository.findByUsername(username).ifPresent(u -> refreshTokenRepository.deleteByUserId(u.getId()));
        return ResponseEntity.ok(Map.of("msg","logged out")); 
    }

    // DTOs
    @Data static class RegisterRequest { private String username; private String password; }
    @Data static class LoginRequest { private String username; private String password; }
}
