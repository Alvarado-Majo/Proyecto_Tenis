package com.ProyectoTenis.demo.controller.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.CarritoDetalle;
import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.service.CarritoService;
import com.ProyectoTenis.demo.service.CarritoDetalleService;
import com.ProyectoTenis.demo.service.OrdenService;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/carrito")
@SessionAttributes("cliente")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CarritoDetalleService carritoDetalleService;

    @Autowired
    private OrdenService ordenService;

    /**
     * Muestra el carrito actual del cliente con todos los productos agregados.
     */
    @GetMapping
    public String verCarrito(@SessionAttribute(name = "cliente", required = false) Cliente cliente,
                             Model model) {
        if (cliente == null) {
            model.addAttribute("error", "Debe iniciar sesión para ver su carrito.");
            return "redirect:/login";
        }

        Carrito carrito = carritoService.obtenerCarritoActivo(cliente);
        List<CarritoDetalle> detalles = carritoDetalleService.listarPorCarrito(carrito);
        BigDecimal total = carritoService.calcularTotal(cliente);

        model.addAttribute("carrito", carrito);
        model.addAttribute("detalles", detalles);
        model.addAttribute("total", total);

        return "carrito"; // plantilla carrito.html
    }

    /**
     * Elimina un producto del carrito.
     */
    @GetMapping("/eliminar/{idTenis}")
    public String eliminarDelCarrito(@PathVariable("idTenis") Integer idTenis,
                                     @SessionAttribute(name = "cliente", required = false) Cliente cliente,
                                     RedirectAttributes ra) {
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para modificar el carrito.");
            return "redirect:/login";
        }

        try {
            carritoService.eliminarProducto(cliente, idTenis);
            ra.addFlashAttribute("ok", "Producto eliminado del carrito.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito";
    }

    /**
     * Vacía todo el carrito del cliente.
     */
    @GetMapping("/vaciar")
    public String vaciarCarrito(@SessionAttribute(name = "cliente", required = false) Cliente cliente,
                                RedirectAttributes ra) {
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para vaciar el carrito.");
            return "redirect:/login";
        }

        carritoService.vaciarCarrito(cliente);
        ra.addFlashAttribute("ok", "Carrito vaciado correctamente.");
        return "redirect:/carrito";
    }

    /**
     * Confirma la compra (genera una orden a partir del carrito).
     */
    @PostMapping("/confirmar")
    public String confirmarCompra(@SessionAttribute(name = "cliente", required = false) Cliente cliente,
                                  Model model,
                                  RedirectAttributes ra) {
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión para confirmar la compra.");
            return "redirect:/login";
        }

        try {
            ordenService.crearOrden(cliente);
            ra.addFlashAttribute("ok", "¡Compra confirmada exitosamente!");
            return "redirect:/ordenes";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito";
        }
    }
}
