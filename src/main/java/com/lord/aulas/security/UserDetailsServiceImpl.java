package com.lord.aulas.security;

import com.lord.aulas.exception.UserNotFoundException;
import com.lord.aulas.model.Estudiante;
import com.lord.aulas.model.Tutor;
import com.lord.aulas.repository.EstudianteRepository;
import com.lord.aulas.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EstudianteRepository estudianteRepository;
    private final TutorRepository tutorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscar primero en estudiantes
        Optional<Estudiante> estudiante = estudianteRepository.findByEmail(email);
        if (estudiante.isPresent()) {
            Estudiante e = estudiante.get();
            return new User(
                    e.getEmail(),
                    e.getPassword(),
                    Collections.singleton(new SimpleGrantedAuthority(e.getRol().name()))
            );
        }

        // Si no es estudiante, buscar en tutores
        Optional<Tutor> tutor = tutorRepository.findByEmail(email);
        if (tutor.isPresent()) {
            Tutor t = tutor.get();
            return new User(
                    t.getEmail(),
                    t.getPassword(),
                    Collections.singleton(new SimpleGrantedAuthority(t.getRol().name()))
            );
        }

        throw new UserNotFoundException("Usuario no encontrado: " + email);
    }
}