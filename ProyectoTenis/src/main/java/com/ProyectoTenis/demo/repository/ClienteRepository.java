package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Cliente findByCorreo(String correo);
}
