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
@SessionAttributes("cliente")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private OrdenDetalleService ordenDetalleService;

    /**
     * Lista todas las órdenes de un cliente.
     */
    @GetMapping
    public String listarOrdenes(
            @SessionAttribute(name = "cliente", required = false) Cliente cliente,
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

        return "ordenes_list";
    }

    /**
     * Detalles de una orden.
     */
    @GetMapping("/detalle/{id}")
    public String verDetalle(
            @PathVariable("id") Long idOrden,
            @SessionAttribute(name = "cliente", required = false) Cliente cliente,
            Model model,
            RedirectAttributes ra) {

        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para ver los detalles de la orden.");
            return "redirect:/login";
        }

        Orden orden = ordenService.buscarPorId(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));

        // Validar propietario de la orden
        if (!orden.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            ra.addFlashAttribute("error", "No tiene permiso para ver esta orden.");
            return "redirect:/ordenes";
        }

        List<OrdenDetalle> detalles = ordenDetalleService.listarPorOrden(orden);
        BigDecimal total = ordenService.calcularTotal(orden);

        model.addAttribute("orden", orden);
        model.addAttribute("detalles", detalles);
        model.addAttribute("total", total);

        return "orden_detalle";
    }

    /**
     * Cancelar orden.
     */
    @PostMapping("/cancelar/{id}")
    public String cancelarOrden(
            @PathVariable("id") Long idOrden,
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
