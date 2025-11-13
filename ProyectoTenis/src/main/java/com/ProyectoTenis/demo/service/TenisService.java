package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ProyectoTenis.demo.domain.Categoria;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.repository.TenisRepository;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Service
public class TenisService {

    @Autowired
    private TenisRepository tenisRepository;

    /**
     * Ruta donde se guardarán las imágenes subidas.
     * Puedes cambiarla según tu estructura de proyecto.
     */
    private static final String UPLOAD_DIR = "src/main/resources/static/img/";

    /**
     * Devuelve todos los tenis registrados.
     */
    public List<Tenis> listarTenis() {
        return tenisRepository.findAll();
    }

    /**
     * Guarda un nuevo tenis o actualiza uno existente.
     * Incluye validaciones básicas y manejo de imagen opcional.
     */
    public void guardarTenis(Tenis tenis, MultipartFile imagenArchivo) throws IOException {
        // Validación de precio
        if (tenis.getPrecio() == null || tenis.getPrecio().doubleValue() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }

        // Validar nombre y categoría
        if (tenis.getNombre() == null || tenis.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del tenis es obligatorio.");
        }
        if (tenis.getCategoria() == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoría válida.");
        }

        // Si se subió una imagen, guardarla en /static/img/
        if (imagenArchivo != null && !imagenArchivo.isEmpty()) {
            String nombreArchivo = imagenArchivo.getOriginalFilename();
            Path ruta = Paths.get(UPLOAD_DIR + nombreArchivo);
            Files.createDirectories(ruta.getParent());
            Files.copy(imagenArchivo.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);
            tenis.setImagen("/img/" + nombreArchivo);
        }

        tenisRepository.save(tenis);
    }

    /**
     * Busca un tenis por su ID.
     */
    public Optional<Tenis> buscarPorId(Integer idTenis) {
        return tenisRepository.findById(idTenis);
    }

    /**
     * Elimina un tenis del catálogo.
     */
    public void eliminarTenis(Integer idTenis) {
        tenisRepository.deleteById(idTenis);
    }

    /**
     * Lista tenis por categoría específica.
     */
    public List<Tenis> listarPorCategoria(Categoria categoria) {
        return tenisRepository.findByCategoria(categoria);
    }

    /**
     * Permite buscar tenis por nombre (para el catálogo del cliente).
     */
    public List<Tenis> buscarPorNombre(String nombre) {
        return tenisRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
