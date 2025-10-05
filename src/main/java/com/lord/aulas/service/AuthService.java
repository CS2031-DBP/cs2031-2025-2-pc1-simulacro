package com.lord.aulas.service;

import com.lord.aulas.dto.request.LoginRequestDTO;
import com.lord.aulas.dto.request.RegisterRequestDTO;
import com.lord.aulas.dto.response.AuthResponseDTO;
import com.lord.aulas.dto.response.EstudianteResponseDTO;
import com.lord.aulas.exception.InvalidCredentialsException;
import com.lord.aulas.exception.UserAlreadyExistsException;
import com.lord.aulas.exception.UserNotFoundException;
import com.lord.aulas.model.Estudiante;
import com.lord.aulas.model.Rol;
import com.lord.aulas.model.Tutor;
import com.lord.aulas.repository.EstudianteRepository;
import com.lord.aulas.repository.TutorRepository;
import com.lord.aulas.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EstudianteRepository estudianteRepository;
    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ModelMapper modelMapper;

    @Transactional
    public EstudianteResponseDTO register(RegisterRequestDTO request) {
        // Verificar si el email ya existe
        if (estudianteRepository.existsByEmail(request.getEmail()) ||
                tutorRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("El email ya está registrado");
        }

        // Crear estudiante
        Estudiante estudiante = Estudiante.builder()
                .nombre(request.getNombre())
                .edad(request.getEdad())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.ROLE_ESTUDIANTE)
                .build();

        Estudiante saved = estudianteRepository.save(estudiante);

        // Mapear a DTO
        EstudianteResponseDTO response = modelMapper.map(saved, EstudianteResponseDTO.class);
        response.setRol(saved.getRol().name().replace("ROLE_", ""));
        if (saved.getSalon() != null) {
            response.setSalonId(saved.getSalon().getId());
        }

        return response;
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        // Buscar usuario (estudiante o tutor)
        Estudiante estudiante = estudianteRepository.findByEmail(request.getEmail()).orElse(null);
        Tutor tutor = tutorRepository.findByEmail(request.getEmail()).orElse(null);

        String password = null;
        String rol = null;

        if (estudiante != null) {
            password = estudiante.getPassword();
            rol = estudiante.getRol().name();
        } else if (tutor != null) {
            password = tutor.getPassword();
            rol = tutor.getRol().name();
        } else {
            throw new UserNotFoundException("Usuario no encontrado");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), password)) {
            throw new InvalidCredentialsException("Credenciales incorrectas");
        }

        // Generar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails, rol);

        return new AuthResponseDTO(token, jwtService.getExpirationTime());
    }
}