package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Categoria findByNombre(String nombre);
    List<Categoria> findByNombreContaining(String nombre);
}
