package com.ProyectoTenis.demo.controller.cliente;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.service.CarritoService;
import com.ProyectoTenis.demo.service.OrdenDetalleService;
import com.ProyectoTenis.demo.service.OrdenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orden")
public class OrdenController {

    private final OrdenService ordenService;
    private final CarritoService carritoService;
    private final OrdenDetalleService ordenDetalleService;

    public OrdenController(OrdenService ordenService,
                           CarritoService carritoService,
                           OrdenDetalleService ordenDetalleService) {
        this.ordenService = ordenService;
        this.carritoService = carritoService;
        this.ordenDetalleService = ordenDetalleService;
    }

    @GetMapping("/confirmar")
    public String confirmarCompra(HttpSession session,
                                  Model model,
                                  RedirectAttributes ra) {

        // 1. Validar cliente logueado
        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para confirmar la compra.");
            return "redirect:/login";
        }

        // 2. Obtener carrito ABIERTO
        Carrito carrito = carritoService.obtenerCarritoDeCliente(cliente);
        if (carrito == null) {
            ra.addFlashAttribute("error", "No tiene un carrito activo.");
            return "redirect:/";
        }

        try {
            // Procesa la compra (crear orden y vaciar carrito)
            Orden orden = ordenService.procesarCompra(carrito);

            // Permite traer los detalles de esa orden para mostrarlos en la factura
            var detalles = ordenDetalleService.listarPorOrden(orden);

            model.addAttribute("orden", orden);
            model.addAttribute("detalles", detalles);

            
            return "cliente/orden-confirmada";

        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo procesar la compra: " + e.getMessage());
            return "redirect:/carrito";
        }
    }
}
