package com.ProyectoTenis.demo.service;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ProyectoTenis.demo.domain.Categoria;
import com.ProyectoTenis.demo.repository.CategoriaRepository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /* ===========================
       Consultas / Listados
       =========================== */

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Optional<Categoria> buscarPorNombre(String nombre) {
        if (nombre == null) return Optional.empty();

        Categoria encontrado = categoriaRepository.findByNombre(normalizar(nombre));

        return Optional.ofNullable(encontrado);
    }

    public boolean existeNombre(String nombre) {
        return buscarPorNombre(nombre).isPresent();
    }

    /* ===========================
       Crear / Actualizar
       =========================== */

    public Categoria crear(String nombre) {
        validarNombreObligatorio(nombre);
        String normalizado = normalizar(nombre);

        if (existeNombre(normalizado)) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(normalizado);

        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, String nuevoNombre) {
        validarNombreObligatorio(nuevoNombre);
        String normalizado = normalizar(nuevoNombre);

        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));

        // Validar nombre duplicado si cambió
        if (!existente.getNombre().equalsIgnoreCase(normalizado)
                && existeNombre(normalizado)) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        }

        existente.setNombre(normalizado);
        return categoriaRepository.save(existente);
    }

    /* ===========================
       Eliminar
       =========================== */

    public void eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));

        try {
            categoriaRepository.delete(categoria);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                "No se puede eliminar la categoría porque tiene productos asociados.", ex
            );
        }
    }

    /* ===========================
       Helpers
       =========================== */

    private void validarNombreObligatorio(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
    }

    private String normalizar(String nombre) {
        String trimmed = nombre.trim().replaceAll("\\s+", " ");
        if (trimmed.isEmpty()) return trimmed;

        String lower = trimmed.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

}
