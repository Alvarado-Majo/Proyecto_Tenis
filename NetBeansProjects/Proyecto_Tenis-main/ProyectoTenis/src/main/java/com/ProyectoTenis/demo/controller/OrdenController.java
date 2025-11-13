package com.ProyectoTenis.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.domain.OrdenDetalle;
import com.ProyectoTenis.demo.service.OrdenService;
import com.ProyectoTenis.demo.service.OrdenDetalleService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/ordenes")
@SessionAttributes("clienteSesion")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private OrdenDetalleService ordenDetalleService;

    /**
     * Lista todas las órdenes del cliente
     */
    @GetMapping
    public String listarOrdenes(
            @SessionAttribute(name = "clienteSesion", required = false) Cliente cliente,
            Model model) {

        if (cliente == null) {
            model.addAttribute("error", "Debe iniciar sesión para ver sus órdenes.");
            return "redirect:/login";
        }

        List<Orden> ordenes = ordenService.listarPorCliente(cliente);
        model.addAttribute("ordenes", ordenes);

        return "ordenes_list";
    }

    /**
     * Ver detalle de una orden específica
     */
    @GetMapping("/detalle/{id}")
    public String verDetalle(
            @PathVariable("id") Long idOrden,
            @SessionAttribute(name = "clienteSesion", required = false) Cliente cliente,
            Model model,
            RedirectAttributes ra) {

        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para ver esta orden.");
            return "redirect:/login";
        }

        Orden orden = ordenService.buscarPorId(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));

        // Validación del dueño de la orden
        if (!orden.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            ra.addFlashAttribute("error", "No tiene permiso para ver esta orden.");
            return "redirect:/ordenes";
        }

        List<OrdenDetalle> detalles = ordenDetalleService.listarPorOrden(orden);
        BigDecimal total = BigDecimal.valueOf(ordenService.calcularTotal(orden));

        model.addAttribute("orden", orden);
        model.addAttribute("detalles", detalles);
        model.addAttribute("total", total);

        return "orden_detalle";
    }

    /**
     * Cancelar una orden
     */
    @PostMapping("/cancelar/{id}")
    public String cancelarOrden(
            @PathVariable("id") Long idOrden,
            @SessionAttribute(name = "clienteSesion", required = false) Cliente cliente,
            RedirectAttributes ra) {

        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para cancelar una orden.");
            return "redirect:/login";
        }

        try {
            ordenService.cambiarEstado(idOrden, "CANCELADA");
            ra.addFlashAttribute("ok", "La orden fue cancelada exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo cancelar la orden: " + e.getMessage());
        }

        return "redirect:/ordenes";
    }
}
