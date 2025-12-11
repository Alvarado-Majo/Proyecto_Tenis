package com.ProyectoTenis.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

@Entity
@Table(name = "tenis")
public class Tenis implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTenis;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(length = 1000)
    private String descripcion;

    // Nombre de archivo o URL de la imagen
    private String imagen;

    @NotNull(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser mayor a 0")
    private Integer stock;

    @NotBlank(message = "La talla es obligatoria")
    private String talla;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    @NotNull(message = "Debe seleccionar una categoría")
    private Categoria categoria;

    public Tenis() {
    }

    public Long getIdTenis() {
        return idTenis;
    }

    public void setIdTenis(Long idTenis) {
        this.idTenis = idTenis;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}

