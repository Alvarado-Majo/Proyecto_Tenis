package com.ProyectoTenis.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import practica.practica.domain.Cliente;
import practica.practica.domain.Orden;
import practica.practica.domain.OrdenDetalle;
import practica.practica.service.OrdenService;
import practica.practica.service.OrdenDetalleService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/ordenes")
@SessionAttributes("cliente")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private OrdenDetalleService ordenDetalleService;

    /**
     * Muestra todas las órdenes del cliente logueado.
     */
    @GetMapping
    public String listarOrdenes(@SessionAttribute(name = "cliente", required = false) Cliente cliente,
                                Model model,
                                @RequestParam(value = "ok", required = false) String ok,
                                @RequestParam(value = "error", required = false) String error) {
        if (cliente == null) {
            model.addAttribute("error", "Debe iniciar sesión para ver sus órdenes.");
            return "redirect:/login";
        }

        List<Orden> ordenes = ordenService.listarPorCliente(cliente);
        model.addAttribute("ordenes", ordenes);
        model.addAttribute("ok", ok);
        model.addAttribute("error", error);

        return "ordenes_list"; // plantilla HTML con historial de compras
    }

    /**
     * Muestra los detalles de una orden específica.
     */
    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable("id") Integer idOrden,
                             @SessionAttribute(name = "cliente", required = false) Cliente cliente,
                             Model model,
                             RedirectAttributes ra) {
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para ver los detalles de la orden.");
            return "redirect:/login";
        }

        Orden orden = ordenService.buscarPorId(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));

        // Verificar que la orden pertenece al cliente logueado
        if (!orden.getCliente().getId_cliente().equals(cliente.getId_cliente())) {
            ra.addFlashAttribute("error", "No tiene permiso para ver esta orden.");
            return "redirect:/ordenes";
        }

        List<OrdenDetalle> detalles = ordenDetalleService.listarPorOrden(orden);
        BigDecimal total = ordenDetalleService.calcularTotal(orden);

        model.addAttribute("orden", orden);
        model.addAttribute("detalles", detalles);
        model.addAttribute("total", total);

        return "orden_detalle"; // plantilla HTML con detalle de productos comprados
    }

    /**
     * Permite cancelar una orden (opcional).
     */
    @PostMapping("/cancelar/{id}")
    public String cancelarOrden(@PathVariable("id") Integer idOrden,
                                @SessionAttribute(name = "cliente", required = false) Cliente cliente,
                                RedirectAttributes ra) {
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para cancelar una orden.");
            return "redirect:/login";
        }

        try {
            ordenService.cambiarEstado(idOrden, "CANCELADA");
            ra.addFlashAttribute("ok", "La orden fue cancelada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo cancelar la orden: " + e.getMessage());
        }

        return "redirect:/ordenes";
    }
}
