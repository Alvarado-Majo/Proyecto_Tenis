package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.Categoria;
import com.ProyectoTenis.demo.domain.Tenis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenisRepository extends JpaRepository<Tenis, Long> {

    Tenis findByNombre(String nombre);

    List<Tenis> findByNombreContaining(String nombre);

    List<Tenis> findByMarca(String marca);

    List<Tenis> findByMarcaContaining(String marca);

    List<Tenis> findByCategoriaIdCategoria(Long idCategoria);

    List<Tenis> findByCategoria(Categoria categoria);
}
