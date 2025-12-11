package com.ProyectoTenis.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "administrador")
public class Administrador implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")   // 👈 COINCIDE CON LA BD
    private Long idAdministrador;

    // En la BD ahora mismo hay 'usuario', no 'email'
    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "usuario")    // 👈 mapeamos a la columna 'usuario'
    private String nombre;

    // Esta columna 'email' la puede crear Hibernate sin problema
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ingresar un correo válido")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    private String rol = "ADMIN";

    public Administrador() {}

    public Long getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(Long idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
