package com.ProyectoTenis.demo.controller.cliente;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.service.CarritoService;
import com.ProyectoTenis.demo.service.OrdenDetalleService;
import com.ProyectoTenis.demo.service.OrdenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orden")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private OrdenDetalleService ordenDetalleService;

    @GetMapping("/confirmar")
    public String confirmarCompra(HttpSession session,
                                  Model model,
                                  RedirectAttributes ra) {

        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para confirmar la compra.");
            return "redirect:/login";
        }

        Carrito carrito = carritoService.getOrCreateCarrito(cliente);

        try {
            Orden orden = ordenService.procesarCompra(carrito);
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
