package com.lord.aulas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tutores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String especialidad;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @OneToOne(mappedBy = "tutor", fetch = FetchType.LAZY)
    private Salon salon;

    @PrePersist
    public void prePersist() {
        if (this.rol == null) {
            this.rol = Rol.ROLE_TUTOR;
        }
    }
}