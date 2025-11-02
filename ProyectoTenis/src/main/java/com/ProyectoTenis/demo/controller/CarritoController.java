package com.ProyectoTenis.demo.controller.cliente;

import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.service.CarritoDetalleService;
import com.ProyectoTenis.demo.service.CarritoService;
import com.ProyectoTenis.demo.service.TenisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CarritoDetalleService carritoDetalleService;

    @Autowired
    private TenisService tenisService;

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        var cliente = (Cliente) session.getAttribute("clienteLogueado");
        var carrito = carritoService.getOrCreateCarrito(cliente);
        var detalles = carritoDetalleService.getDetalles(carrito);

        model.addAttribute("carrito", carrito);
        model.addAttribute("detalles", detalles);
        return "/cliente/carrito";
    }

    @PostMapping("/agregar/{idTenis}")
    public String agregar(@PathVariable Long idTenis, HttpSession session, RedirectAttributes redirect) {
        var cliente = (Cliente) session.getAttribute("clienteLogueado");
        var carrito = carritoService.getOrCreateCarrito(cliente);

        carritoService.agregarProducto(carrito, idTenis);
        redirect.addFlashAttribute("mensaje", "Producto agregado al carrito");
        return "redirect:/carrito";
    }

    @GetMapping("/eliminar/{idDetalle}")
    public String eliminar(@PathVariable Long idDetalle, RedirectAttributes redirect) {
        carritoDetalleService.delete(idDetalle);
        redirect.addFlashAttribute("mensaje", "Producto eliminado del carrito");
        return "redirect:/carrito";
    }
}
