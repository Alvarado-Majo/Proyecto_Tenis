package com.ProyectoTenis.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "carrito")
public class Carrito implements Serializable {

    @OneToOne
    private Orden orden;
    private double total;
    private LocalDate fechaCreacion;
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long idCarrito;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotNull
    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // ABIERTO o PAGADO

    @Column(name = "fecha_creacion")
    private String fechaCreacion;
}
