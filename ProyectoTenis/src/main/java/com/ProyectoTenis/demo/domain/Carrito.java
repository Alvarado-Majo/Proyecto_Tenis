package com.ProyectoTenis.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrito;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "El carrito debe pertenecer a un cliente")
    private Cliente cliente;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoDetalle> detalles = new ArrayList<>();

    @Positive(message = "El total debe ser mayor a 0")
    private Double total;

    // 👇 NUEVO CAMPO: debe existir porque está en la BD y lo usa el servicio
    @Column(name = "estado")
    private String estado; // "ABIERTO" o "PAGADO"

    public Carrito() {
    }

    public Long getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Long idCarrito) {
        this.idCarrito = idCarrito;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<CarritoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<CarritoDetalle> detalles) {
        this.detalles = detalles;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    // 👇 GETTER y SETTER que te pedía el compilador
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

