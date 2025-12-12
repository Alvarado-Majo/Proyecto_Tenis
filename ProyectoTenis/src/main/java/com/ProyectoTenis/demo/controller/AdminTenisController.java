package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.service.CategoriaService;
import com.ProyectoTenis.demo.service.TenisService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tenis")
public class AdminTenisController {

    private final TenisService tenisService;
    private final CategoriaService categoriaService;

    public AdminTenisController(TenisService tenisService,
                                CategoriaService categoriaService) {
        this.tenisService = tenisService;
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listarTenis(@SessionAttribute(name = "admin", required = false) Object admin,
                              Model model,
                              RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/admin/login";
        }

        model.addAttribute("tenisList", tenisService.listarTodos());
        return "admin/tenis_list";  // templates/admin/tenis_list.html
    }

    @GetMapping("/nuevo")
    public String nuevoTenis(@SessionAttribute(name = "admin", required = false) Object admin,
                             Model model,
                             RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/admin/login";
        }

        model.addAttribute("tenis", new Tenis());
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "admin/tenis_form";  // templates/admin/tenis_form.html
    }

    @PostMapping("/guardar")
    public String guardarTenis(@SessionAttribute(name = "admin", required = false) Object admin,
                               @Valid @ModelAttribute("tenis") Tenis tenis,
                               BindingResult result,
                               Model model,
                               RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/admin/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarCategorias());
            return "admin/tenis_form";
        }

        tenisService.guardar(tenis);
        ra.addFlashAttribute("ok", "Tenis guardado correctamente.");
        return "redirect:/admin/tenis";
    }

    @GetMapping("/editar/{id}")
    public String editarTenis(@PathVariable Long id,
                              @SessionAttribute(name = "admin", required = false) Object admin,
                              Model model,
                              RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/admin/login";
        }

        var tenis = tenisService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenis no encontrado"));

        model.addAttribute("tenis", tenis);
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "admin/tenis_form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           @SessionAttribute(name = "admin", required = false) Object admin,
                           RedirectAttributes ra) {

        if (admin == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador.");
            return "redirect:/admin/login";
        }

        tenisService.eliminarPorId(id);
        ra.addFlashAttribute("ok", "Tenis eliminado correctamente.");
        return "redirect:/admin/tenis";
    }
}
