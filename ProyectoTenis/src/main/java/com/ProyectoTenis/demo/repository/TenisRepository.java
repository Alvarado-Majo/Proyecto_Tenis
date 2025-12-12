package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.Categoria;
import com.ProyectoTenis.demo.domain.Tenis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenisRepository extends JpaRepository<Tenis, Long> {

    // Buscar un tenis por nombre exacto
    Tenis findByNombre(String nombre);

    // Buscar tenis cuyo nombre contenga un texto
    List<Tenis> findByNombreContaining(String nombre);

    // Buscar por marca exacta
    List<Tenis> findByMarca(String marca);

    // Buscar por marca que contenga un texto
    List<Tenis> findByMarcaContaining(String marca);

    // Buscar por id de categoría (categoria.idCategoria)
    List<Tenis> findByCategoriaIdCategoria(Long idCategoria);

    // Buscar por la categoría como objeto
    List<Tenis> findByCategoria(Categoria categoria);
}
