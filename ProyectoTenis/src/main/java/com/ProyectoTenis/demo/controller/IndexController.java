package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Categoria;
import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.service.CarritoService;
import com.ProyectoTenis.demo.service.CategoriaService;
import com.ProyectoTenis.demo.service.TenisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /** CATÁLOGO PRINCIPAL */
    @GetMapping
    public String mostrarCatalogo(
            @RequestParam(value = "categoria", required = false) Integer idCategoria,
            @RequestParam(value = "buscar", required = false) String buscar,
            Model model) {

        List<Tenis> tenisList;

        // FILTRO POR CATEGORIA
        if (idCategoria != null) {
            Optional<Categoria> categoriaOpt = categoriaService.buscarPorId(idCategoria.longValue());

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

    /** DETALLE DE PRODUCTO */
    @GetMapping("/detalle/{id}")
    public String mostrarDetalleProducto(@PathVariable("id") Long idTenis,
                                         Model model,
                                         RedirectAttributes ra) {

        Optional<Tenis> tenisOpt = tenisService.buscarPorId(idTenis);

        if (tenisOpt.isEmpty()) {
            ra.addFlashAttribute("error", "El producto no existe o fue eliminado.");
            return "redirect:/";
        }

        model.addAttribute("tenis", tenisOpt.get());
        return "detalle_tenis";
    }

    /** AGREGAR AL CARRITO DESDE EL CATÁLOGO O DETALLE */
    @PostMapping("/agregar-carrito/{id}")
    public String agregarAlCarrito(
            @PathVariable("id") Long idTenis,
            @RequestParam(defaultValue = "1") int cantidad,
            HttpSession session,
            RedirectAttributes ra) {

        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para agregar productos al carrito.");
            return "redirect:/login";
        }

        try {
            var carrito = carritoService.getOrCreateCarrito(cliente);

            // Agregamos el producto 'cantidad' veces
            for (int i = 0; i < cantidad; i++) {
                carritoService.agregarProducto(carrito, idTenis);
            }

            ra.addFlashAttribute("ok", "Producto agregado al carrito.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al agregar al carrito: " + e.getMessage());
        }

        return "redirect:/";
    }
}
