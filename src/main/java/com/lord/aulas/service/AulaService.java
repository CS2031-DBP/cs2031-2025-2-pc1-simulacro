package com.lord.aulas.service;

import com.lord.aulas.dto.response.EstudianteResponseDTO;
import com.lord.aulas.dto.response.MessageResponseDTO;
import com.lord.aulas.dto.response.SalonResponseDTO;
import com.lord.aulas.exception.SalonNotFoundException;
import com.lord.aulas.exception.UnauthorizedAssignmentException;
import com.lord.aulas.exception.UserNotFoundException;
import com.lord.aulas.model.Estudiante;
import com.lord.aulas.model.Salon;
import com.lord.aulas.model.Tutor;
import com.lord.aulas.repository.EstudianteRepository;
import com.lord.aulas.repository.SalonRepository;
import com.lord.aulas.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AulaService {

    private final SalonRepository salonRepository;
    private final EstudianteRepository estudianteRepository;
    private final TutorRepository tutorRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<SalonResponseDTO> listarSalones() {
        return salonRepository.findAll().stream()
                .map(this::mapToSalonDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> listarEstudiantesPorSalon(Long salonId) {
        if (!salonRepository.existsById(salonId)) {
            throw new SalonNotFoundException("Salón no encontrado con ID: " + salonId);
        }

        return estudianteRepository.findBySalonId(salonId).stream()
                .map(this::mapToEstudianteDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageResponseDTO asignarEstudianteASalon(Long tutorId, Long estudianteId, Long salonId) {
        // Verificar que el tutor existe
        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new UserNotFoundException("Tutor no encontrado con ID: " + tutorId));

        // Verificar que el salón existe
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new SalonNotFoundException("Salón no encontrado con ID: " + salonId));

        // Verificar que el estudiante existe
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new UserNotFoundException("Estudiante no encontrado con ID: " + estudianteId));

        // Verificar que el tutor pertenece a ese salón
        if (salon.getTutor() == null || !salon.getTutor().getId().equals(tutorId)) {
            throw new UnauthorizedAssignmentException(
                    "El tutor no está asignado a este salón. No puede asignar estudiantes."
            );
        }

        // Asignar el estudiante al salón
        estudiante.setSalon(salon);
        estudianteRepository.save(estudiante);

        String message = String.format(
                "Estudiante %s asignado al salón %s por el tutor %s",
                estudiante.getNombre(),
                salon.getCodigo(),
                tutor.getNombre()
        );

        return new MessageResponseDTO(message);
    }

    @Transactional(readOnly = true)
    public SalonResponseDTO obtenerSalonDeEstudiante(Long estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new UserNotFoundException("Estudiante no encontrado con ID: " + estudianteId));

        Salon salon = estudiante.getSalon();
        if (salon == null) {
            throw new SalonNotFoundException("El estudiante no tiene salón asignado");
        }

        return mapToSalonDTO(salon);
    }

    // Métodos auxiliares para mapeo
    private SalonResponseDTO mapToSalonDTO(Salon salon) {
        SalonResponseDTO dto = modelMapper.map(salon, SalonResponseDTO.class);
        if (salon.getTutor() != null) {
            dto.setTutorNombre(salon.getTutor().getNombre());
        }
        return dto;
    }

    private EstudianteResponseDTO mapToEstudianteDTO(Estudiante estudiante) {
        EstudianteResponseDTO dto = modelMapper.map(estudiante, EstudianteResponseDTO.class);
        dto.setRol(estudiante.getRol().name().replace("ROLE_", ""));
        if (estudiante.getSalon() != null) {
            dto.setSalonId(estudiante.getSalon().getId());
        }
        return dto;
    }
}