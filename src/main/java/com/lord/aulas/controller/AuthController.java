package com.lord.aulas.controller;

import com.lord.aulas.dto.request.LoginRequestDTO;
import com.lord.aulas.dto.request.RegisterRequestDTO;
import com.lord.aulas.dto.response.AuthResponseDTO;
import com.lord.aulas.dto.response.EstudianteResponseDTO;
import com.lord.aulas.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<EstudianteResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        EstudianteResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}