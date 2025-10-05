package com.lord.aulas.config;

import com.lord.aulas.model.Rol;
import com.lord.aulas.model.Salon;
import com.lord.aulas.model.Tutor;
import com.lord.aulas.repository.SalonRepository;
import com.lord.aulas.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final TutorRepository tutorRepository;
    private final SalonRepository salonRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (tutorRepository.count() == 0) {
                log.info("Inicializando datos de prueba...");

                // Crear 3 tutores (uno por cada salón)
                Tutor tutor1 = Tutor.builder()
                        .nombre("Prof. García")
                        .especialidad("Matemáticas")
                        .email("garcia@utec.edu.pe")
                        .password(passwordEncoder.encode("Tutor123"))
                        .rol(Rol.ROLE_TUTOR)
                        .build();

                Tutor tutor2 = Tutor.builder()
                        .nombre("Ing. Rojas")
                        .especialidad("Programación")
                        .email("rojas@utec.edu.pe")
                        .password(passwordEncoder.encode("Tutor123"))
                        .rol(Rol.ROLE_TUTOR)
                        .build();

                Tutor tutor3 = Tutor.builder()
                        .nombre("Dr. Martínez")
                        .especialidad("Física")
                        .email("martinez@utec.edu.pe")
                        .password(passwordEncoder.encode("Tutor123"))
                        .rol(Rol.ROLE_TUTOR)
                        .build();

                tutor1 = tutorRepository.save(tutor1);
                tutor2 = tutorRepository.save(tutor2);
                tutor3 = tutorRepository.save(tutor3);

                // Crear salones (cada uno con su tutor)
                Salon salon1 = Salon.builder()
                        .codigo("A1")
                        .grado("Tercer ciclo")
                        .tutor(tutor1)
                        .build();

                Salon salon2 = Salon.builder()
                        .codigo("B1")
                        .grado("Cuarto ciclo")
                        .tutor(tutor2)
                        .build();

                Salon salon3 = Salon.builder()
                        .codigo("C1")
                        .grado("Quinto ciclo")
                        .tutor(tutor3)  // CAMBIO AQUÍ: usar tutor3 en vez de tutor1
                        .build();

                salonRepository.save(salon1);
                salonRepository.save(salon2);
                salonRepository.save(salon3);

                log.info("Datos de prueba insertados correctamente");
                log.info("Tutores creados: 3");
                log.info("Salones creados: 3");
                log.info("Login tutores - Email: garcia@utec.edu.pe | Password: Tutor123");
            }
        };
    }
}