package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Categoria;
import com.ProyectoTenis.demo.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categoria")
public class AdminCategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/listado")
    public String listado(Model model) {
        var categorias = categoriaService.getCategorias();
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        return "/admin/categoria/listado";
    }

    @GetMapping("/modificar/{idCategoria}")
    public String modificar(@PathVariable Long idCategoria, Model model) {
        var categoria = (idCategoria != 0)
                ? categoriaService.getCategoria(idCategoria).orElse(new Categoria())
                : new Categoria();

        model.addAttribute("categoria", categoria);
        return "/admin/categoria/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Categoria categoria, RedirectAttributes redirect) {
        categoriaService.save(categoria);
        redirect.addFlashAttribute("mensaje", "Categoría guardada correctamente");
        return "redirect:/admin/categoria/listado";
    }

    @GetMapping("/eliminar/{idCategoria}")
    public String eliminar(@PathVariable Long idCategoria, RedirectAttributes redirect) {
        categoriaService.delete(idCategoria);
        redirect.addFlashAttribute("mensaje", "Categoría eliminada correctamente");
        return "redirect:/admin/categoria/listado";
    }
}
