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
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.ClienteRepository;
import com.ProyectoTenis.demo.repository.TenisRepository;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TenisRepository tenisRepository;

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    
    @GetMapping("/{idCliente}")
    public String mostrarCarrito(@PathVariable Long idCliente, Model model) {
        Cliente cliente = clienteRepository.findById(idCliente).orElseThrow();
        Carrito carrito = carritoService.getOrCreateCarrito(cliente);

        model.addAttribute("cliente", cliente);
        model.addAttribute("carrito", carrito);
        model.addAttribute("detalles", carritoService.getDetalles(carrito));
        return "carrito"; 
    }

    
    @PostMapping("/{idCliente}/agregar/{idTenis}")
    public String agregarProducto(
            @PathVariable Long idCliente,
            @PathVariable Long idTenis) {

        Cliente cliente = clienteRepository.findById(idCliente).orElseThrow();
        Carrito carrito = carritoService.getOrCreateCarrito(cliente);
        carritoService.agregarProducto(carrito, idTenis);

        return "redirect:/carrito/" + idCliente;
    }

    @PostMapping("/eliminar/{idDetalle}")
    public String eliminarProducto(@PathVariable Long idDetalle) {
        CarritoDetalle detalle = carritoDetalleRepository.findById(idDetalle).orElseThrow();
        Long idCliente = detalle.getCarrito().getCliente().getIdCliente();

        carritoService.eliminarProducto(detalle);
        return "redirect:/carrito/" + idCliente;
    }
    
    @PostMapping("/{idCliente}/confirmar")
    public String confirmarCompra(@PathVariable Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente).orElseThrow();
        carritoService.confirmarOrden(cliente);
        return "redirect:/confirmacion"; 
    }
}
