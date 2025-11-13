package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.Tenis;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenisRepository extends JpaRepository<Tenis, Long> {

    Tenis findByNombre(String nombre);
    
    List<Tenis> findByNombreContaining(String nombre);

    List<Tenis> findByMarca(String marca);

    List<Tenis> findByMarcaContaining(String marca);

    List<Tenis> findByCategoria_IdCategoria(Long idCategoria);
}
