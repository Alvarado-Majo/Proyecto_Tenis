package com.ProyectoTenis.demo.controller;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.domain.MetodoPago;
import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.service.CarritoService;
import com.ProyectoTenis.demo.service.OrdenDetalleService;
import com.ProyectoTenis.demo.service.OrdenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String mostrarConfirmacion(HttpSession session,
            Model model,
            RedirectAttributes ra) {

        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión.");
            return "redirect:/login";
        }

        Carrito carrito = carritoService.obtenerCarritoDeCliente(cliente);
        if (carrito == null) {
            ra.addFlashAttribute("error", "No tiene un carrito activo.");
            return "redirect:/";
        }

        model.addAttribute("total", carrito.getTotal());
        return "cliente/orden-confirmar";
    }

    @PostMapping("/confirmar")
    public String confirmarCompra(HttpSession session,
            @RequestParam MetodoPago metodoPago,
            Model model,
            RedirectAttributes ra) {

        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión.");
            return "redirect:/login";
        }

        Carrito carrito = carritoService.obtenerCarritoDeCliente(cliente);
        if (carrito == null) {
            ra.addFlashAttribute("error", "No tiene un carrito activo.");
            return "redirect:/";
        }

        Orden orden = ordenService.procesarCompra(carrito, metodoPago);
        var detalles = ordenDetalleService.listarPorOrden(orden);

        model.addAttribute("orden", orden);
        model.addAttribute("detalles", detalles);

        return "cliente/orden-confirmada";
    }

}
