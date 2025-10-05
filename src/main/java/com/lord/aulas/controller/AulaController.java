package com.lord.aulas.controller;

import com.lord.aulas.dto.response.EstudianteResponseDTO;
import com.lord.aulas.dto.response.MessageResponseDTO;
import com.lord.aulas.dto.response.SalonResponseDTO;
import com.lord.aulas.service.AulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aulas")
@RequiredArgsConstructor
public class AulaController {

    private final AulaService aulaService;

    @GetMapping("/salones")
    public ResponseEntity<List<SalonResponseDTO>> listarSalones() {
        return ResponseEntity.ok(aulaService.listarSalones());
    }

    @GetMapping("/salones/{id}/estudiantes")
    public ResponseEntity<List<EstudianteResponseDTO>> listarEstudiantesPorSalon(@PathVariable Long id) {
        return ResponseEntity.ok(aulaService.listarEstudiantesPorSalon(id));
    }

    @PostMapping("/tutores/{tutorId}/asignar/{estudianteId}/salon/{salonId}")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<MessageResponseDTO> asignarEstudianteASalon(
            @PathVariable Long tutorId,
            @PathVariable Long estudianteId,
            @PathVariable Long salonId) {
        MessageResponseDTO response = aulaService.asignarEstudianteASalon(tutorId, estudianteId, salonId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/estudiantes/{id}/salon")
    public ResponseEntity<SalonResponseDTO> obtenerSalonDeEstudiante(@PathVariable Long id) {
        return ResponseEntity.ok(aulaService.obtenerSalonDeEstudiante(id));
    }
}