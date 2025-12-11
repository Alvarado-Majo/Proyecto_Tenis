package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.CarritoDetalle;
import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.domain.OrdenDetalle;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.CarritoRepository;
import com.ProyectoTenis.demo.repository.OrdenDetalleRepository;
import com.ProyectoTenis.demo.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private OrdenDetalleRepository ordenDetalleRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    /**
     * Crear la orden a partir del carrito del cliente.
     */
    @Transactional
    public Orden procesarCompra(Carrito carrito) {
        // Obtener detalles del carrito
        List<CarritoDetalle> detalles = carritoDetalleRepository.findByCarrito(carrito);

        if (detalles.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío. No se puede procesar la compra.");
        }

        // Calcular total
        double total = detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();

        // Crear la orden
        Orden orden = new Orden();
        orden.setCliente(carrito.getCliente());
        orden.setTotal(total);
        orden.setEstado("PAGADA");
        orden.setFechaCreacion(LocalDateTime.now());

        // Opcional: usar la dirección del cliente como dirección de envío
        if (carrito.getCliente() != null) {
            orden.setDireccionEnvio(carrito.getCliente().getDireccion());
        }

        orden = ordenRepository.save(orden);

        // Crear los detalles de la orden
        for (CarritoDetalle d : detalles) {
            OrdenDetalle od = new OrdenDetalle();
            od.setOrden(orden);
            od.setTenis(d.getTenis());
            od.setCantidad(d.getCantidad());
            od.setPrecioUnitario(d.getPrecioUnitario());
            od.setSubtotal(d.getCantidad() * d.getPrecioUnitario());

            ordenDetalleRepository.save(od);
        }

        // Vaciar carrito
        carritoDetalleRepository.deleteAll(detalles);
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);

        return orden;
    }
}
