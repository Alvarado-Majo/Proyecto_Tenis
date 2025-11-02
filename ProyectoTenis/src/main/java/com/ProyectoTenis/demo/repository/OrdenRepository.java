package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.domain.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findByCliente(Cliente cliente);
}
