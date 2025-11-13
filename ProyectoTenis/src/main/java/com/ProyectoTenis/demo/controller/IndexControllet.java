package com.ProyectoTenis.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import practica.practica.domain.Categoria;
import practica.practica.domain.Cliente;
import practica.practica.domain.Tenis;
import practica.practica.service.CategoriaService;
import practica.practica.service.TenisService;
import practica.practica.service.CarritoService;

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

    /**
     * Página principal del catálogo de productos.
     * Muestra todos los tenis disponibles o filtrados por categoría.
     */
    @GetMapping
    public String mostrarCatalogo(@RequestParam(value = "categoria", required = false) Integer idCategoria,
                                  @RequestParam(value = "buscar", required = false) String buscar,
                                  Model model) {
        List<Tenis> tenisList;

        if (idCategoria != null) {
            Categoria categoria = categoriaService.buscarPorId(idCategoria).orElse(null);
            tenisList = (categoria != null)
                    ? tenisService.listarPorCategoria(categoria)
                    : tenisService.listarTenis();
        } else if (buscar != null && !buscar.isBlank()) {
            tenisList = tenisService.buscarPorNombre(buscar);
        } else {
            tenisList = tenisService.listarTenis();
        }

        model.addAttribute("tenisList", tenisList);
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("buscar", buscar);
        model.addAttribute("idCategoria", idCategoria);

        return "index"; // plantilla principal
    }

    /**
     * Muestra los detalles de un producto individual.
     */
    @GetMapping("/detalle/{id}")
    public String mostrarDetalleProducto(@PathVariable("id") Integer idTenis, Model model) {
        Optional<Tenis> tenisOpt = tenisService.buscarPorId(idTenis);
        if (tenisOpt.isEmpty()) {
            model.addAttribute("error", "El producto no existe o fue eliminado.");
            return "redirect:/";
        }
        model.addAttribute("tenis", tenisOpt.get());
        return "detalle_tenis"; // plantilla de detalle
    }

    /**
     * Agrega un producto al carrito desde el catálogo o el detalle.
     */
    @PostMapping("/agregar-carrito/{id}")
    public String agregarAlCarrito(@PathVariable("id") Integer idTenis,
                                   @RequestParam(defaultValue = "1") int cantidad,
                                   @SessionAttribute(name = "cliente", required = false) Cliente cliente,
                                   Model model) {
        if (cliente == null) {
            model.addAttribute("error", "Debe iniciar sesión para agregar productos al carrito.");
            return "redirect:/login";
        }

        try {
            carritoService.agregarProducto(cliente, idTenis, cantidad);
            model.addAttribute("ok", "Producto agregado al carrito.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/";
    }
}
