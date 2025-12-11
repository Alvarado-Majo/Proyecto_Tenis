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

    // LISTAR TODOS (catálogo, admin)
    public List<Tenis> listarTenis() {
        return tenisRepository.findAll();
    }

    // GUARDAR (crear / editar)
    public Tenis guardar(Tenis tenis) {
        return tenisRepository.save(tenis);
    }

    // BUSCAR POR ID (devuelve Optional para usar en controladores)
    public Optional<Tenis> buscarPorId(Long id) {
        return tenisRepository.findById(id);
    }

    // ELIMINAR
    public void eliminar(Long id) {
        tenisRepository.deleteById(id);
    }

    // LISTAR POR CATEGORÍA (por id)
    public List<Tenis> listarPorCategoria(Long idCategoria) {
        return tenisRepository.findByCategoriaIdCategoria(idCategoria);
    }

    // LISTAR POR CATEGORÍA (por entidad)
    public List<Tenis> listarPorCategoria(Categoria categoria) {
        return tenisRepository.findByCategoria(categoria);
    }

    // BÚSQUEDA POR NOMBRE (catálogo)
    public List<Tenis> buscarPorNombreSimilar(String nombre) {
        return tenisRepository.findByNombreContaining(nombre);
    }
}
