package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Categoria;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.repository.TenisRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenisService {

    private final TenisRepository tenisRepository;

    public TenisService(TenisRepository tenisRepository) {
        this.tenisRepository = tenisRepository;
    }

    /* ======================= LISTAR ======================= */

    
    public List<Tenis> listarTenis() {
        return tenisRepository.findAll();
    }

    public List<Tenis> listarTodos() {
        return tenisRepository.findAll();
    }

    /* ======================= GUARDAR ======================= */

    public Tenis guardar(Tenis tenis) {
        return tenisRepository.save(tenis);
    }

    /* ======================= BUSCAR ======================= */

    public Optional<Tenis> buscarPorId(Long id) {
        return tenisRepository.findById(id);
    }

    /* ======================= ELIMINAR ======================= */

    public void eliminar(Long id) {
        tenisRepository.deleteById(id);
    }

    public void eliminarPorId(Long id) {
        tenisRepository.deleteById(id);
    }

    /* ======================= FILTROS / BÚSQUEDAS ======================= */

    // Listar por categoría (id)
    public List<Tenis> listarPorCategoria(Long idCategoria) {
        return tenisRepository.findByCategoriaIdCategoria(idCategoria);
    }

    // Listar por categoría (entidad)
    public List<Tenis> listarPorCategoria(Categoria categoria) {
        return tenisRepository.findByCategoria(categoria);
    }

    // Búsqueda por nombre (para el catálogo)
    public List<Tenis> buscarPorNombreSimilar(String nombre) {
        return tenisRepository.findByNombreContaining(nombre);
    }
}
