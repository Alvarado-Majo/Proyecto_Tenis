package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    List<Carrito> findByClienteAndEstado(Cliente cliente, String estado);
}
