package com.ProyectoTenis.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;
import java.util.List;


@Data
@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {
    @OneToOne(mappedBy = "cliente")
    private Carrito carrito;

    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos;


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @NotNull
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotNull
    @Size(max = 100)
    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    @NotNull
    @Size(max = 255)
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 255)
    @Column(name = "direccion", length = 255)
    private String direccion;
}
