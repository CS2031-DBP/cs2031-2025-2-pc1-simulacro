package com.lord.aulas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteResponseDTO {
    private Long id;
    private String nombre;
    private Integer edad;
    private String email;
    private String rol;
    private Long salonId;
}