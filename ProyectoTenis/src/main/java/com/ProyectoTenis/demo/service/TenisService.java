package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Categoria;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.repository.TenisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenisService {

    @Autowired
    private TenisRepository tenisRepository;

    /** ============================
        LISTAR TODOS
       ============================ */
    public List<Tenis> listarTenis() {
        return tenisRepository.findAll();
    }

    /** ============================
        BUSCAR POR ID
       ============================ */
    public Optional<Tenis> buscarPorId(Long idTenis) {
        return tenisRepository.findById(idTenis);
    }

    /** ============================
        GUARDAR (crear o actualizar)
       ============================ */
    public Tenis guardar(Tenis tenis) {

        // Validaciones importantes
        if (tenis.getNombre() == null || tenis.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }

        if (tenis.getPrecio() == null || tenis.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio debe ser un número positivo.");
        }

        if (tenis.getCategoria() == null || tenis.getCategoria().getIdCategoria() == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoría válida.");
        }

        return tenisRepository.save(tenis);
    }

    /** ============================
        ELIMINAR POR ID
       ============================ */
    public void eliminar(Long idTenis) {

        if (!tenisRepository.existsById(idTenis)) {
            throw new IllegalArgumentException("El producto no existe.");
        }

        tenisRepository.deleteById(idTenis);
    }

    /** ============================
        BUSCAR POR NOMBRE EXACTO
       ============================ */
    public Tenis buscarPorNombre(String nombre) {
        return tenisRepository.findByNombre(nombre);
    }

    /** ============================
        BUSCAR POR NOMBRE SIMILAR
       ============================ */
    public List<Tenis> buscarPorNombreSimilar(String nombre) {
        return tenisRepository.findByNombreContaining(nombre);
    }

    /** ============================
        BUSCAR POR MARCA
       ============================ */
    public List<Tenis> buscarPorMarca(String marca) {
        return tenisRepository.findByMarcaContaining(marca);
    }

    /** ============================
        LISTAR POR CATEGORÍA
       ============================ */
    public List<Tenis> listarPorCategoria(Categoria categoria) {

        if (categoria == null || categoria.getIdCategoria() == null) {
            throw new IllegalArgumentException("La categoría no es válida.");
        }

        return tenisRepository.findByCategoria_IdCategoria(categoria.getIdCategoria());
    }
}
