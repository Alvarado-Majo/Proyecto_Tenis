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

        model.addAttribute("tenisList", tenisService.listarTenis());
        return "admin/tenis_list";
    }

    /** NUEVO */
    @GetMapping("/nuevo")
    public String nuevoTenis(
            @SessionAttribute(name = "admin", required = false) Object admin,
            Model model,
            RedirectAttributes ra) {

        if (admin == null) return "redirect:/login";

        model.addAttribute("tenis", new Tenis());
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "admin/tenis_form";
    }

    /** GUARDAR */
    @PostMapping("/guardar")
    public String guardarTenis(
            @SessionAttribute(name = "admin", required = false) Object admin,
            @ModelAttribute Tenis tenis,
            RedirectAttributes ra) {

        if (admin == null) return "redirect:/login";

        tenisService.guardar(tenis);
        ra.addFlashAttribute("ok", "Tenis guardado correctamente.");
        return "redirect:/admin/tenis";
    }

    /** EDITAR */
    @GetMapping("/editar/{id}")
    public String editarTenis(
            @PathVariable Long id,
            @SessionAttribute(name = "admin", required = false) Object admin,
            Model model,
            RedirectAttributes ra) {

        if (admin == null) return "redirect:/login";

        model.addAttribute("tenis", tenisService.buscarPorId(id).orElse(null));
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "admin/tenis_form";
    }

    /** ELIMINAR */
    @GetMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            @SessionAttribute(name = "admin", required = false) Object admin,
            RedirectAttributes ra) {

        if (admin == null) return "redirect:/login";

        tenisService.eliminar(id);
        ra.addFlashAttribute("ok", "Producto eliminado");
        return "redirect:/admin/tenis";
    }
}

