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
     * Crea una orden completa a partir del carrito activo.
     */
    public Orden crearOrden(Cliente cliente) {

        Carrito carrito = carritoService.obtenerCarritoActivo(cliente);
        List<CarritoDetalle> detallesCarrito = carritoDetalleRepository.findByCarrito(carrito);

        if (detallesCarrito.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío. No se puede generar la orden.");
        }

        // Calcular total con Double
        double total = detallesCarrito.stream()
                .mapToDouble(cd -> cd.getPrecioUnit() * cd.getCantidad())
                .sum();

        // Crear Orden
        Orden orden = new Orden();
        orden.setCliente(cliente);
        orden.setFecha(LocalDateTime.now().toString()); // porque tu campo es String
        orden.setEstado("PENDIENTE");
        orden.setTotal(total);
        orden = ordenRepository.save(orden);

        // Crear detalles
        for (CarritoDetalle cd : detallesCarrito) {
            OrdenDetalle od = new OrdenDetalle();
            od.setOrden(orden);
            od.setTenis(cd.getTenis());
            od.setCantidad(cd.getCantidad());
            od.setPrecioUnit(cd.getPrecioUnit());
            ordenDetalleRepository.save(od);
        }

        // Cerrar carrito y eliminar contenido
        carritoService.cerrarCarrito(cliente);
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
     * Busca una orden por ID.
     */
    public Optional<Orden> buscarPorId(Long idOrden) {
        return ordenRepository.findById(idOrden);
    }

    /**
     * Cambia el estado de una orden.
     */
    public void cambiarEstado(Long idOrden, String nuevoEstado) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada."));
        orden.setEstado(nuevoEstado.toUpperCase());
        ordenRepository.save(orden);
    }

    /**
     * Calcula el total de la orden usando Double.
     */
    public double calcularTotal(Orden orden) {
        List<OrdenDetalle> detalles = ordenDetalleRepository.findByOrden(orden);
        return detalles.stream()
                .mapToDouble(d -> d.getPrecioUnit() * d.getCantidad())
                .sum();
    }
}
