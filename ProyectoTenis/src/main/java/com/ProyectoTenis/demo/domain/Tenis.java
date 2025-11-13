package com.ProyectoTenis.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tenis")
public class Tenis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tenis")
    private Long idTenis;

    @NotNull
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotNull
    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @Size(max = 100)
    @Column(name = "marca", length = 100)
    private String marca;

    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Size(max = 255)
    @Column(name = "imagen", length = 255)
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}
