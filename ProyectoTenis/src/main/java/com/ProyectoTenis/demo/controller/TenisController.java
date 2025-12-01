

package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Categoria;
import com.ProyectoTenis.demo.repository.CategoriaRepository;
import com.ProyectoTenis.demo.repository.TenisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tenis")
public class TenisController {

    @Autowired
    private TenisRepository tenisRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    /** LISTADO GENERAL */
    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("listaTenis", tenisRepository.findAll());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "tenis/listado";
    }

    /** FILTRO POR CATEGORÍA */
    @GetMapping("/categoria/{idCategoria}")
    public String tenisPorCategoria(@PathVariable Long idCategoria, Model model) {

        Categoria categoria = categoriaRepository
                .findById(idCategoria)
                .orElseThrow();

        List<Tenis> listaTenis = tenisRepository.findByCategoria(categoria);

        model.addAttribute("listaTenis", listaTenis);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("categoria", categoria);

        return "tenis/listado";
    }
}

