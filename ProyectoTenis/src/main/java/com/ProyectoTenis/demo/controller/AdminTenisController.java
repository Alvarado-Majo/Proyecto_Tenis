package com.ProyectoTenis.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.service.TenisService;
import com.ProyectoTenis.demo.service.CategoriaService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/tenis")
@SessionAttributes("admin")
public class AdminTenisController {

    @Autowired
    private TenisService tenisService;

    @Autowired
    private CategoriaService categoriaService;

    /** LISTAR TENIS */
    @GetMapping
    public String listarTenis(
            @SessionAttribute(name = "admin", required = false) Object admin,
            Model model,
            RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        List<Tenis> tenisList = tenisService.listarTenis();
        model.addAttribute("tenisList", tenisList);
        return "admin/tenis_list";
    }

    /** FORMULARIO NUEVO TENIS */
    @GetMapping("/nuevo")
    public String nuevoTenis(
            @SessionAttribute(name = "admin", required = false) Object admin,
            Model model,
            RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        model.addAttribute("tenis", new Tenis());
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "admin/tenis_form";
    }

    /** GUARDAR TENIS */
    @PostMapping("/guardar")
    public String guardarTenis(
            @SessionAttribute(name = "admin", required = false) Object admin,
            @ModelAttribute Tenis tenis,
            RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        try {
            tenisService.guardar(tenis);
            ra.addFlashAttribute("ok", "Tenis guardado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el tenis: " + e.getMessage());
        }

        return "redirect:/admin/tenis";
    }

    /** EDITAR TENIS */
    @GetMapping("/editar/{id}")
    public String editarTenis(
            @PathVariable("id") Long idTenis,
            @SessionAttribute(name = "admin", required = false) Object admin,
            Model model,
            RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        Optional<Tenis> tenisOpt = tenisService.buscarPorId(idTenis);
        if (tenisOpt.isEmpty()) {
            ra.addFlashAttribute("error", "El producto no existe.");
            return "redirect:/admin/tenis";
        }

        model.addAttribute("tenis", tenisOpt.get());
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "admin/tenis_form";
    }

    /** ELIMINAR TENIS */
    @GetMapping("/eliminar/{id}")
    public String eliminarTenis(
            @PathVariable("id") Long idTenis,
            @SessionAttribute(name = "admin", required = false) Object admin,
            RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/login";
        }

        try {
            tenisService.eliminar(idTenis);
            ra.addFlashAttribute("ok", "Producto eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo eliminar el producto: " + e.getMessage());
        }

        return "redirect:/admin/tenis";
    }
}
