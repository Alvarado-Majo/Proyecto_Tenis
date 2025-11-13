package com.ProyectoTenis.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ProyectoTenis.demo.domain.Categoria;
import com.ProyectoTenis.demo.service.CategoriaService;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categorias")
@SessionAttributes("admin")
public class AdminCategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    /* ============================
       LISTAR
       ============================ */
    @GetMapping
    public String listarCategorias(
            @SessionAttribute(name = "admin", required = false) Object admin,
            Model model,
            RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        List<Categoria> categorias = categoriaService.listarCategorias();
        model.addAttribute("categorias", categorias);
        return "admin/categorias_list";
    }

    /* ============================
       NUEVA
       ============================ */
    @GetMapping("/nueva")
    public String nuevaCategoria(
            @SessionAttribute(name = "admin", required = false) Object admin,
            Model model,
            RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        model.addAttribute("categoria", new Categoria());
        return "admin/categorias_form";
    }

    /* ============================
       GUARDAR (crear o editar)
       ============================ */
    @PostMapping("/guardar")
    public String guardarCategoria(
            @SessionAttribute(name = "admin", required = false) Object admin,
            @ModelAttribute Categoria categoria,
            RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        try {
            if (categoria.getIdCategoria() == null) {
                // NUEVA
                categoriaService.crear(categoria.getNombre());
                ra.addFlashAttribute("ok", "Categoría creada correctamente.");
            } else {
                // EDITAR
                categoriaService.actualizar(categoria.getIdCategoria(), categoria.getNombre());
                ra.addFlashAttribute("ok", "Categoría actualizada correctamente.");
            }

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/categorias";
    }

    /* ============================
       EDITAR
       ============================ */
    @GetMapping("/editar/{id}")
    public String editarCategoria(
            @PathVariable("id") Long idCategoria,
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
        return "admin/categorias_form";
    }

    /* ============================
       ELIMINAR
       ============================ */
    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(
            @PathVariable("id") Long idCategoria,
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
            ra.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }

        return "redirect:/admin/categorias";
    }
}
