package com.lord.aulas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalonResponseDTO {
    private Long id;
    private String codigo;
    private String grado;
    private String tutorNombre;
}