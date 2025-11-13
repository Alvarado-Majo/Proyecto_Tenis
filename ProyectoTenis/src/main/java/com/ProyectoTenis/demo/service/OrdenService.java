package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ProyectoTenis.demo.domain.*;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.OrdenDetalleRepository;
import com.ProyectoTenis.demo.repository.OrdenRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private OrdenDetalleRepository ordenDetalleRepository;

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    @Autowired
    private CarritoService carritoService;

    /**
     * Crear una orden desde el carrito.
     */
    public Orden crearOrden(Cliente cliente) {

        Carrito carrito = carritoService.obtenerCarritoActivo(cliente);
        List<CarritoDetalle> detallesCarrito = carritoDetalleRepository.findByCarrito(carrito);

        if (detallesCarrito.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        // Calcular total con Double
        double total = detallesCarrito.stream()
                .mapToDouble(cd -> cd.getPrecioUnit().doubleValue() * cd.getCantidad())
                .sum();

        // Crear orden
        Orden orden = new Orden();
        orden.setCliente(cliente);
        orden.setFecha(LocalDateTime.now()); // OK porque tu campo es LocalDateTime
        orden.setEstado("PENDIENTE");
        orden.setTotal(total);

        orden = ordenRepository.save(orden);

        // Crear detalles de la orden
        for (CarritoDetalle cd : detallesCarrito) {
            OrdenDetalle od = new OrdenDetalle();
            od.setOrden(orden);
            od.setTenis(cd.getTenis());
            od.setCantidad(cd.getCantidad());
            od.setPrecioUnit(cd.getPrecioUnit()); // BigDecimal está bien
            ordenDetalleRepository.save(od);
        }

        // Cerrar carrito y vaciarlo
        carritoService.cerrarCarrito(cliente);
        carritoDetalleRepository.deleteAll(detallesCarrito);

        return orden;
    }

    public Optional<Orden> buscarPorId(Long idOrden) {
        return ordenRepository.findById(idOrden);
    }

    public List<Orden> listarPorCliente(Cliente cliente) {
        return ordenRepository.findByCliente(cliente);
    }

    public void cambiarEstado(Long idOrden, String nuevoEstado) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        orden.setEstado(nuevoEstado.toUpperCase());
        ordenRepository.save(orden);
    }

    public double calcularTotal(Orden orden) {
        List<OrdenDetalle> detalles = ordenDetalleRepository.findByOrden(orden);

        return detalles.stream()
                .mapToDouble(d -> d.getPrecioUnit().doubleValue() * d.getCantidad())
                .sum();
    }
}
