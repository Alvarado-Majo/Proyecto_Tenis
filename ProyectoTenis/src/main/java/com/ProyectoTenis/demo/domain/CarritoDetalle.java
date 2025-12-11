package com.ProyectoTenis.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;

@Entity
@Table(name = "carrito_detalle")
public class CarritoDetalle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")  
    private Long idCarritoDetalle;

    @ManyToOne
    @JoinColumn(name = "id_carrito", nullable = false)
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "id_tenis", nullable = false)
    private Tenis tenis;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser mayor a 0")
    @Column(name = "precio_unit")
    private Double precioUnitario;

    @NotNull(message = "El subtotal es obligatorio")
    @Positive(message = "El subtotal debe ser mayor a 0")
    private Double subtotal;

    public CarritoDetalle() {
    }

    public Long getIdCarritoDetalle() {
        return idCarritoDetalle;
    }

    public void setIdCarritoDetalle(Long idCarritoDetalle) {
        this.idCarritoDetalle = idCarritoDetalle;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Tenis getTenis() {
        return tenis;
    }

    public void setTenis(Tenis tenis) {
        this.tenis = tenis;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        recalcularSubtotal();
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
        recalcularSubtotal();
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Recalcula el subtotal siempre que cantidad y precio estén definidos.
     */
    private void recalcularSubtotal() {
        if (this.cantidad != null && this.precioUnitario != null) {
            this.subtotal = this.cantidad * this.precioUnitario;
        }
    }
}
