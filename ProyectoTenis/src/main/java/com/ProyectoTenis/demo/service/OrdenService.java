package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ProyectoTenis.demo.domain.*;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.OrdenDetalleRepository;
import com.ProyectoTenis.demo.repository.OrdenRepository;
import java.math.BigDecimal;
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
     * Crea una orden a partir del carrito activo del cliente.
     * Genera los detalles y calcula el total automáticamente.
     */
    public Orden crearOrden(Cliente cliente) {
        // Obtener el carrito activo del cliente
        Carrito carrito = carritoService.obtenerCarritoActivo(cliente);
        List<CarritoDetalle> detallesCarrito = carritoDetalleRepository.findByCarrito(carrito);

        if (detallesCarrito.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío. No se puede generar la orden.");
        }

        // Calcular total
        BigDecimal total = detallesCarrito.stream()
                .map(cd -> cd.getPrecio_unit().multiply(BigDecimal.valueOf(cd.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Crear la orden
        Orden orden = new Orden();
        orden.setCliente(cliente);
        orden.setFecha(LocalDateTime.now());
        orden.setEstado("PENDIENTE");
        orden.setTotal(total);
        orden = ordenRepository.save(orden);

        // Crear los detalles asociados
        for (CarritoDetalle cd : detallesCarrito) {
            OrdenDetalle od = new OrdenDetalle();
            od.setOrden(orden);
            od.setTenis(cd.getTenis());
            od.setCantidad(cd.getCantidad());
            od.setPrecio_unit(cd.getPrecio_unit());
            ordenDetalleRepository.save(od);
        }

        // Marcar el carrito como PAGADO
        carrito.setEstado("PAGADO");
        carritoService.cerrarCarrito(cliente);

        // Vaciar el carrito
        carritoDetalleRepository.deleteAll(detallesCarrito);

        return orden;
    }

    /**
     * Lista todas las órdenes de un cliente.
     */
    public List<Orden> listarPorCliente(Cliente cliente) {
        return ordenRepository.findByCliente(cliente);
    }

    /**
     * Busca una orden por su ID.
     */
    public Optional<Orden> buscarPorId(Integer idOrden) {
        return ordenRepository.findById(idOrden);
    }

    /**
     * Cambia el estado de una orden (por ejemplo: PAGADA, CANCELADA).
     */
    public void cambiarEstado(Integer idOrden, String nuevoEstado) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));
        orden.setEstado(nuevoEstado.toUpperCase());
        ordenRepository.save(orden);
    }

    /**
     * Calcula el total de una orden según sus detalles.
     */
    public BigDecimal calcularTotal(Orden orden) {
        List<OrdenDetalle> detalles = ordenDetalleRepository.findByOrden(orden);
        return detalles.stream()
                .map(d -> d.getPrecio_unit().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
