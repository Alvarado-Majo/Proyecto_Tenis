package com.ProyectoTenis.demo.controller.cliente;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.service.CarritoDetalleService;
import com.ProyectoTenis.demo.service.CarritoService;
import com.ProyectoTenis.demo.service.TenisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    /** VER CARRITO */
    @GetMapping("")
    public String verCarrito(HttpSession session, Model model, RedirectAttributes ra) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para ver el carrito.");
            return "redirect:/login";
        }

        Carrito carrito = carritoService.getOrCreateCarrito(cliente);
        var detalles = carritoDetalleService.listarPorCarrito(carrito);

        double total = detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();

        model.addAttribute("carrito", carrito);
        model.addAttribute("detalles", detalles);
        model.addAttribute("total", total);

        return "cliente/carrito";
    }

    /** AGREGAR PRODUCTO DESDE OTRA VISTA (OPCIONAL) */
    @GetMapping("/agregar/{idTenis}")
    public String agregar(@PathVariable Long idTenis,
                          HttpSession session,
                          RedirectAttributes redirect) {

        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            redirect.addFlashAttribute("error", "Debe iniciar sesión para agregar productos al carrito.");
            return "redirect:/login";
        }

        Carrito carrito = carritoService.getOrCreateCarrito(cliente);
        carritoService.agregarProducto(carrito, idTenis);

        redirect.addFlashAttribute("ok", "Producto agregado al carrito.");
        return "redirect:/carrito";
    }

    /** ELIMINAR UNA LÍNEA DEL CARRITO */
    @GetMapping("/eliminar/{idDetalle}")
    public String eliminar(@PathVariable Long idDetalle,
                           HttpSession session,
                           RedirectAttributes redirect) {

        Cliente cliente = (Cliente) session.getAttribute("clienteLogueado");
        if (cliente == null) {
            redirect.addFlashAttribute("error", "Debe iniciar sesión.");
            return "redirect:/login";
        }

        // Usamos el servicio que valida que el detalle pertenezca al carrito del cliente
        carritoService.eliminarProducto(cliente, idDetalle);

        redirect.addFlashAttribute("ok", "Producto eliminado del carrito.");
        return "redirect:/carrito";
    }
}
