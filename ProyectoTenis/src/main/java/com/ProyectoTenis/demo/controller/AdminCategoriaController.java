package com.ProyectoTenis.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import practica.practica.domain.Categoria;
import practica.practica.service.CategoriaService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categorias")
@SessionAttributes("admin")
public class AdminCategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    /**
     * Muestra la lista de categorías existentes.
     */
    @GetMapping
    public String listarCategorias(@SessionAttribute(name = "admin", required = false) Object admin,
                                   Model model,
                                   RedirectAttributes ra) {
        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        List<Categoria> categorias = categoriaService.listarCategorias();
        model.addAttribute("categorias", categorias);
        return "admin/categorias_list"; // plantilla admin/categorias_list.html
    }

    /**
     * Muestra el formulario para crear una nueva categoría.
     */
    @GetMapping("/nueva")
    public String nuevaCategoria(@SessionAttribute(name = "admin", required = false) Object admin,
                                 Model model,
                                 RedirectAttributes ra) {
        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        model.addAttribute("categoria", new Categoria());
        return "admin/categorias_form"; // plantilla admin/categorias_form.html
    }

    /**
     * Guarda una nueva categoría o actualiza una existente.
     */
    @PostMapping("/guardar")
    public String guardarCategoria(@SessionAttribute(name = "admin", required = false) Object admin,
                                   @ModelAttribute Categoria categoria,
                                   RedirectAttributes ra) {
        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        try {
            categoriaService.guardar(categoria);
            ra.addFlashAttribute("ok", "Categoría guardada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar la categoría: " + e.getMessage());
        }

        return "redirect:/admin/categorias";
    }

    /**
     * Muestra el formulario para editar una categoría existente.
     */
    @GetMapping("/editar/{id}")
    public String editarCategoria(@PathVariable("id") Integer idCategoria,
                                  @SessionAttribute(name = "admin", required = false) Object admin,
                                  Model model,
                                  RedirectAttributes ra) {
        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        Optional<Categoria> categoriaOpt = categoriaService.buscarPorId(idCategoria);
        if (categoriaOpt.isEmpty()) {
            ra.addFlashAttribute("error", "La categoría no existe.");
            return "redirect:/admin/categorias";
        }

        model.addAttribute("categoria", categoriaOpt.get());
        return "admin/categorias_form"; // reutiliza el mismo formulario
    }

    /**
     * Elimina una categoría existente.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable("id") Integer idCategoria,
                                    @SessionAttribute(name = "admin", required = false) Object admin,
                                    RedirectAttributes ra) {
        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        try {
            categoriaService.eliminar(idCategoria);
            ra.addFlashAttribute("ok", "Categoría eliminada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo eliminar la categoría: " + e.getMessage());
        }

        return "redirect:/admin/categorias";
    }
}
