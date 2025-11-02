package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    Administrador findByUsuario(String usuario);
}

