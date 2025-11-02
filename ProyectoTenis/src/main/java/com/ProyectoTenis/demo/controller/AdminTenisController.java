package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.service.CategoriaService;
import com.ProyectoTenis.demo.service.TenisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tenis")
public class AdminTenisController {

    @Autowired
    private TenisService tenisService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/listado")
    public String listado(Model model) {
        var tenis = tenisService.getTenis();
        model.addAttribute("tenis", tenis);
        model.addAttribute("totalTenis", tenis.size());
        return "/admin/tenis/listado";
    }

    @GetMapping("/modificar/{idTenis}")
    public String modificar(@PathVariable Long idTenis, Model model) {
        var tenis = (idTenis != 0)
                ? tenisService.getTenis(idTenis).orElse(new Tenis())
                : new Tenis();

        model.addAttribute("tenis", tenis);
        model.addAttribute("categorias", categoriaService.getCategorias());
        return "/admin/tenis/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Tenis tenis,
                          @RequestParam("imagenFile") MultipartFile imagenFile,
                          RedirectAttributes redirect) {

        tenisService.save(tenis, imagenFile);
        redirect.addFlashAttribute("mensaje", "Producto guardado correctamente");
        return "redirect:/admin/tenis/listado";
    }

    @GetMapping("/eliminar/{idTenis}")
    public String eliminar(@PathVariable Long idTenis, RedirectAttributes redirect) {
        tenisService.delete(idTenis);
        redirect.addFlashAttribute("mensaje", "Producto eliminado correctamente");
        return "redirect:/admin/tenis/listado";
    }
}
