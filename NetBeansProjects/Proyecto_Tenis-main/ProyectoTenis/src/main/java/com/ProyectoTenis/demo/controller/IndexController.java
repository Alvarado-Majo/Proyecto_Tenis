package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Categoria;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.service.CarritoService;
import com.ProyectoTenis.demo.service.CategoriaService;
import com.ProyectoTenis.demo.service.TenisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private TenisService tenisService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CarritoService carritoService;

    @GetMapping
    public String mostrarCatalogo(
            @RequestParam(value = "categoria", required = false) Integer idCategoria,
            @RequestParam(value = "buscar", required = false) String buscar,
            Model model) {

        List<Tenis> tenisList;

        // FILTRO POR CATEGORIA
        if (idCategoria != null) {
            Optional<Categoria> categoriaOpt = categoriaService.buscarPorId(idCategoria.longValue()); // CORRECCIÓN

            if (categoriaOpt.isPresent()) {
                tenisList = tenisService.listarPorCategoria(categoriaOpt.get());
            } else {
                tenisList = tenisService.listarTenis();
            }

        // FILTRO POR BÚSQUEDA
        } else if (buscar != null && !buscar.isBlank()) {
            tenisList = tenisService.buscarPorNombreSimilar(buscar.trim());

        // LISTAR TODO
        } else {
            tenisList = tenisService.listarTenis();
        }

        model.addAttribute("tenisList", tenisList);
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("buscar", buscar);
        model.addAttribute("idCategoria", idCategoria);

        return "index";
    }

    @GetMapping("/detalle/{id}")
    public String mostrarDetalleProducto(@PathVariable("id") Long idTenis, Model model) {

        Optional<Tenis> tenisOpt = tenisService.buscarPorId(idTenis);

        if (tenisOpt.isEmpty()) {
            model.addAttribute("error", "El producto no existe o fue eliminado.");
            return "redirect:/";
        }

        model.addAttribute("tenis", tenisOpt.get());
        return "detalle_tenis";
    }

    @PostMapping("/agregar-carrito/{id}")
    public String agregarAlCarrito(
            @PathVariable("id") Long idTenis, // CAMBIADO A LONG
            @RequestParam(defaultValue = "1") int cantidad,
            @SessionAttribute(name = "cliente", required = false) Cliente cliente,
            RedirectAttributes ra) {

        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para agregar productos al carrito.");
            return "redirect:/login";
        }

        try {
            carritoService.agregarProducto(cliente, idTenis.intValue(), cantidad); // SE MANTIENE INT PARA EL SERVICIO
            ra.addFlashAttribute("ok", "Producto agregado al carrito.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/";
    }
}

